package world;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.socketserver.hebe.AtiwWatch;
import com.strongjoshua.console.GUIConsole;

import collision.MyCollision;
import collision.MyVector;
import consts.Hero;
import consts.MyColors;
import hud.InGameHud;
import hud.killfeed.KillFeed;
import networking.GameClient;
import protocol.SpecialAttack;
import protocol.events.NewBullet;
import protocol.message.Message;
import protocol.worlddata.HealthPackData;
import protocol.worlddata.PlayerData;
import protocol.worlddata.ShieldData;
import protocol.worlddata.TerrainData;
import protocol.worlddata.WorldData;
import serverworld.ServerTerrain;
import serverworld.entities.HealthPack;
import world.heros.Assassin;
import world.heros.Grabber;
import world.heros.Medic;
import world.heros.OtherPlayer;
import world.heros.Soldier;
import world.heros.Tank;
import world.heros.bots.AssassinBot;
import world.heros.bots.GrabberBot;
import world.heros.bots.MedicBot;
import world.heros.bots.SoldierBot;
import world.heros.bots.TankBot;
import world.heros.specialattack.Aura;
import world.heros.specialattack.Dash;
import world.heros.specialattack.HeroSpecialAttack;
import world.heros.specialattack.Hook;

public class World {

	private GameClient client;

	private Player me;
	private List<OtherPlayer> otherPlayers;
	private List<ProjectTile> projectTiles;

	private Terrain terrain;
	private MyCollision walls;

	private GUIConsole console;

	private List<Shield> shields;

	private ArrayList<HealthPack> healthPacks;

	private List<HeroSpecialAttack> heroSpecialAttacks;

	private KillFeed killFeed;

	public World() {
		this.walls = new MyCollision();

		selectHero();

		this.otherPlayers = new LinkedList<>();
		this.terrain = new Terrain(this.walls);
		this.projectTiles = new LinkedList<>();
		this.shields = new ArrayList<>();
		this.heroSpecialAttacks = new ArrayList<>();
		this.healthPacks = new ArrayList<>();
	}

	public void selectHero() {
		if (AtiwWatch.hero == Hero.SOLDIER) {
			this.me = new Soldier(this);
		} else if (AtiwWatch.hero == Hero.TANK) {
			this.me = new Tank(this);
		} else if (AtiwWatch.hero == Hero.TANK_BOT) {
			this.me = new TankBot(this);
		} else if (AtiwWatch.hero == Hero.ASSASSIN) {
			this.me = new Assassin(this);
		} else if (AtiwWatch.hero == Hero.ASSASSIN_BOT) {
			this.me = new AssassinBot(this);
		} else if (AtiwWatch.hero == Hero.GRABBER) {
			this.me = new Grabber(this);
		} else if (AtiwWatch.hero == Hero.GRABBER_BOT) {
			this.me = new GrabberBot(this);
		} else if (AtiwWatch.hero == Hero.MEDIC) {
			this.me = new Medic(this);
			heroSpecialAttacks.add(((Medic) this.me).getAura());
		} else if (AtiwWatch.hero == Hero.MEDIC_BOT) {
			this.me = new MedicBot(this);
			heroSpecialAttacks.add(((MedicBot) this.me).getAura());
		} else if (AtiwWatch.hero == Hero.SOLDIER_BOT) {
			this.me = new SoldierBot(this);
		} else {
			this.me = new OtherPlayer(null);
		}
		if (this.me != null) {
			this.me.id = AtiwWatch.ID;
			this.me.team = AtiwWatch.team;
			this.me.respawn();
		}
	}

	public void update(float delta) {
		updateServerInput();

		for (OtherPlayer player : otherPlayers) {
			if (player.heroType == Hero.MEDIC && player.getAura().getAuraType() == Aura.SPEED && me.team == player.team
					&& new MyVector(me.x - player.x, me.y - player.y).len() < Hero.MEDIC_AURA_RANGE) {
				me.addSpeed(Hero.MEDIC_AURA_SPEED_BOOST);
			}
		}

		this.me.update(delta);
		InGameHud.stunned = this.me.isStunned();
		InGameHud.showStunned = this.me.isShowStunned();

		synchronized (this.projectTiles) {
			for (ProjectTile projectTile : this.projectTiles) {
				projectTile.update(delta);
			}
		}

		synchronized (this.heroSpecialAttacks) {
			for (int i = 0; i < this.heroSpecialAttacks.size(); i++) {
				this.heroSpecialAttacks.get(i).update(delta);
				if (this.heroSpecialAttacks.get(i).toDispose()) {
					this.heroSpecialAttacks.remove(i);
					i--;
				}
			}
		}
	}

	public void sendSpecialAttack(HeroSpecialAttack heroSpecialAttack) {
		heroSpecialAttack.setPlayer(this.me);
		this.client.sendSpecialAttack(heroSpecialAttack.getData());
	}

	public void newSpecialAttack(SpecialAttack specialAttack) {
		if (specialAttack.type == SpecialAttack.DASH) {
			Dash dash = new Dash(specialAttack);
			if (specialAttack.player_id == this.me.id) {
				dash.setPlayer(this.me);
			}
			synchronized (this.heroSpecialAttacks) {
				this.heroSpecialAttacks.add(dash);
			}
		} else if (specialAttack.type == SpecialAttack.HOOK) {
			Hook hook = new Hook(specialAttack);
			if (specialAttack.player_id == this.me.id) {
				hook.setPlayer(this.me);
			}
			synchronized (this.heroSpecialAttacks) {
				this.heroSpecialAttacks.add(hook);
			}
		}
	}

	public void newBullet(int x, int y, float x2, float y2, int dmg, int speed, int range) {
		this.client.sendNewBullet(x, y, x2, y2, dmg, speed, range);
	}

	public void newBullet(NewBullet newBullet) {
		synchronized (this.projectTiles) {
			this.projectTiles.add(new ProjectTile(newBullet));
		}
	}

	public void killBullet(int id) {
		boolean killedSomething = false;
		synchronized (this.projectTiles) {
			for (int i = 0; i < this.projectTiles.size(); i++) {
				if (this.projectTiles.get(i).id == id) {
					this.projectTiles.remove(i);
					killedSomething = true;
					i--;
				}
			}
		}
		if (!killedSomething) {
			synchronized (this.heroSpecialAttacks) {
				for (int i = 0; i < this.heroSpecialAttacks.size(); i++) {
					if (this.heroSpecialAttacks.get(i).getId() == id) {
						if (this.heroSpecialAttacks.get(i).getType() == SpecialAttack.HOOK) {
							((Hook) this.heroSpecialAttacks.get(i)).reverse();
						} else {
							this.heroSpecialAttacks.remove(i);
							i--;
						}
					}
				}
			}
		}
	}

	private void updateServerInput() {
		WorldData worldData = this.client.currentWorld;

		if (worldData != null) {
			this.otherPlayers.clear();
			for (PlayerData player : worldData.playerData) {
				if (player.id != AtiwWatch.ID) {
					OtherPlayer otherPlayer = new OtherPlayer(player);
					this.otherPlayers.add(otherPlayer);
					for (HeroSpecialAttack hsa : this.heroSpecialAttacks) {
						if (hsa.getPlayerID() == otherPlayer.id) {
							hsa.setPlayer(otherPlayer);
						}
					}
				} else {
					this.me.setKills(player.kills);
					this.me.setDeaths(player.deaths);
					InGameHud.health = player.health;
					this.me.health = player.health;
				}
			}

			this.shields.clear();
			for (ShieldData shield : worldData.shields) {
				this.shields.add(new Shield(shield));
			}

			this.healthPacks.clear();
			for (HealthPackData healthPack : worldData.healthPacks) {
				this.healthPacks.add(new HealthPack(healthPack));
			}

			InGameHud.setPointData(worldData.pointData);
			InGameHud.serverUPS = worldData.ups;

			InGameHud.maxHealth = this.me.maxHealth;

		}
		if (this.client.newPos != null) {
			this.me.x = this.client.newPos.x;
			this.me.y = this.client.newPos.y;
			this.client.newPos = null;
		}
	}

	public void render(ShapeRenderer shape, SpriteBatch batch, BitmapFont font) {
		renderWorldGrid(shape);
		this.terrain.render((int) this.me.x, (int) this.me.y, shape);
		renderProjectTiles(shape);

		for (int i = 0; i < this.heroSpecialAttacks.size(); i++) {
			this.heroSpecialAttacks.get(i).render(shape);
		}

		renderPlayers(shape);

		for (Shield shield : this.shields) {
			shield.render(shape);
		}
		batch.begin();
		font.setColor(MyColors.WHITE);
		for (HealthPack healthPack : this.healthPacks) {
			if (healthPack.cooldownLeft > 0)
				font.draw(batch, "" + (int) (healthPack.getCooldownLeft() + 0.9f), healthPack.x * 50 + 21,
						healthPack.y * 50 + 30);
		}
		batch.end();

		/*
		 * WALL - HITBOX shape.begin(ShapeType.Line);
		 * shape.setColor(MyColors.CYAN); for (MyLine line : this.walls.lines) {
		 * shape.line(line.x1, line.y1, line.x2, line.y2); } shape.end();
		 */
	}

	public void renderWorldGrid(ShapeRenderer shape) {
		shape.begin(ShapeType.Line);
		shape.setColor(MyColors.GREEN);
		for (int xy = -AtiwWatch.SCREEN_WIDTH_HEIGHT / 2 - 50; xy < AtiwWatch.SCREEN_WIDTH_HEIGHT / 2 + 50; xy += 50) {

			shape.line((int) this.me.x / 50 * 50 - AtiwWatch.SCREEN_WIDTH_HEIGHT / 2 - 50,
					(int) this.me.y / 50 * 50 + xy + 25,
					(int) this.me.x / 50 * 50 + AtiwWatch.SCREEN_WIDTH_HEIGHT / 2 + 50,
					(int) this.me.y / 50 * 50 + xy + 25);

			shape.line((int) this.me.x / 50 * 50 + xy + 25,
					(int) this.me.y / 50 * 50 - AtiwWatch.SCREEN_WIDTH_HEIGHT / 2 - 50,
					(int) this.me.x / 50 * 50 + xy + 25,
					(int) this.me.y / 50 * 50 + AtiwWatch.SCREEN_WIDTH_HEIGHT / 2 + 50);
		}
		shape.end();
	}

	public void renderPlayers(ShapeRenderer shape) {
		for (OtherPlayer player : this.otherPlayers) {
			player.renderFirst(shape);
		}
		this.me.renderFirst(shape);
		for (OtherPlayer player : this.otherPlayers) {
			player.render(shape);
		}
		this.me.render(shape);
	}

	public void renderProjectTiles(ShapeRenderer shape) {
		synchronized (this.projectTiles) {
			shape.begin(ShapeType.Filled);
			shape.setColor(MyColors.YELLOW);
			for (ProjectTile projectTile : this.projectTiles) {
				projectTile.render(shape);
			}
		}
		shape.end();
	}

	public void setClient(GameClient client) {
		this.client = client;
	}

	public void setTerrain(TerrainData terrainData) {
		AtiwWatch.team = terrainData.team;
		this.terrain.setTerrain(terrainData.terrain);
	}

	public Player getMe() {
		return this.me;
	}

	public MyCollision getWalls() {
		return this.walls;
	}

	public GameClient getClient() {
		return this.client;
	}

	public Terrain getTerrain() {
		return this.terrain;
	}

	public void stun(boolean stunned) {
		this.me.stun((stunned) ? 1 : -1);
	}

	public List<OtherPlayer> getOtherPlayers() {
		return this.otherPlayers;
	}

	public ArrayList<HealthPack> getHealthPacks() {
		return healthPacks;
	}
	
	public List<ProjectTile> getProjectTiles() {
		return projectTiles;
	}
	
	public boolean playerInSpawn() {
		return this.terrain.get((int) this.me.x / 50, (int) this.me.y / 50) == ServerTerrain.SPAWN_1
				|| this.terrain.get((int) this.me.x / 50, (int) this.me.y / 50) == ServerTerrain.SPAWN_2;
	}

	public void setKillFeed(KillFeed killFeed) {
		this.killFeed = killFeed;
	}

	public void playerGotKilled(String message) {
		if (this.killFeed != null) {
			this.killFeed.add(message);
		}
	}

	public void setConsole(GUIConsole console) {
		this.console = console;
	}

	public void recieveMessage(Message message) {
		if (console != null) {
			console.log(message.message);
			InGameHud.gotMessage = true;
		}
	}
}
