package serverworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import collision.MyCollision;
import collision.MyLine;
import collision.MyVector;
import consts.Hero;
import protocol.SpecialAttack;
import protocol.events.NewBullet;
import protocol.worlddata.HealthPackData;
import protocol.worlddata.PlayerData;
import protocol.worlddata.ShieldData;
import protocol.worlddata.WorldData;
import server.GameServer;
import serverworld.entities.HealthPack;
import serverworld.projecttiles.ServerHook;

public class ServerWorld {

	private List<ServerPlayer> players;
	private ServerTerrain terrain;

	private List<ServerProjectTile> projectTiles;
	private List<ServerProjectTile> specialProjectTiles;
	private MyCollision myCollision;

	private HashMap<Integer, ShieldData> shields;

	private GameServer server;

	public ServerWorld(GameServer server) {
		this.server = server;
		this.players = new LinkedList<ServerPlayer>();
		this.myCollision = new MyCollision();
		this.terrain = new ServerTerrain(this.players, this);
		this.terrain.loadTerrain();
		this.projectTiles = new LinkedList<>();
		this.shields = new HashMap<>();
		this.specialProjectTiles = new ArrayList<>();
	}

	public void update(float delta) {
		synchronized (this.projectTiles) {
			for (int i = 0; i < this.projectTiles.size(); i++) {
				this.projectTiles.get(i).update(delta, this);
				if (this.projectTiles.get(i).toDispose) {
					this.server.killBullet(this.projectTiles.get(i).getId());
					this.projectTiles.remove(i);
					i--;
				}
			}
		}
		synchronized (this.specialProjectTiles) {
			for (int i = 0; i < this.specialProjectTiles.size(); i++) {
				this.specialProjectTiles.get(i).update(delta, this);
				if (this.specialProjectTiles.get(i).toDispose) {
					this.server.killBullet(this.specialProjectTiles.get(i).getId());
					if (this.specialProjectTiles.get(i).type == SpecialAttack.HOOK) {
						ServerPlayer hooked = ((ServerHook) this.specialProjectTiles.get(i)).getHookedTo();
						if (hooked != null) {
							this.server.stun(hooked.id, false);
						}
					}
					this.specialProjectTiles.remove(i);
					i--;
				}
			}
		}
		synchronized (this.players) {
			for(ServerPlayer player : players){
				if(player.hero == Hero.MEDIC){
					if(player.extra == Hero.MEDIC_AURA_HEAL){
						for(ServerPlayer player2 : players){
							if(player2.team == player.team && new MyVector(player2.x-player.x, player2.y-player.y).len()<Hero.MEDIC_AURA_RANGE){
								player2.heal(Hero.MEDIC_AURA_HEAL_PER_SECOND*delta);
							}
						}
					}
				}
			}
		}
		this.terrain.update(delta);
	}

	public WorldData getData() {
		WorldData worldData = new WorldData();

		PlayerData[] playerData = new PlayerData[this.players.size()];
		for (int i = 0; i < this.players.size(); i++) {
			playerData[i] = this.players.get(i).getData();
		}
		worldData.playerData = playerData;

		worldData.pointData = this.terrain.getPoint().getData();

		worldData.shields = new ShieldData[this.shields.values().size()];
		int i = 0;
		for (ShieldData shield : this.shields.values()) {
			worldData.shields[i] = shield;
			i++;
		}

		worldData.healthPacks = new HealthPackData[this.terrain.getHealthPacks().size()];
		i = 0;
		for (HealthPack healthPack : this.terrain.getHealthPacks()) {
			worldData.healthPacks[i] = healthPack.getData();
			i++;
		}

		return worldData;
	}

	public void setTeam(byte team, int id) {
		for (ServerPlayer player : this.players) {
			if (player.id == id) {
				player.team = team;
			}
		}
	}

	public ServerPlayer setPlayerData(PlayerData playerData, int id) {
		boolean contains = false;
		for (ServerPlayer player : this.players) {
			if (player.id == id) {
				contains = true;
				if (player.hero != playerData.heroType) {
					this.shields.remove(player.id);
					player.respawn();
				}
				player.setPlayerData(playerData);
				return player;
			}
		}
		if (!contains) {
			ServerPlayer newPlayer = new ServerPlayer(this.server);
			newPlayer.id = id;
			newPlayer.setPlayerData(playerData);
			this.players.add(newPlayer);
			return newPlayer;
		}
		return null;
	}

	public void setPlayerHero(int id, byte hero) {
		for (ServerPlayer player : this.players) {
			if (player.id == id) {
				player.hero = hero;
			}
		}
	}

	public ServerTerrain getTerrain() {
		return this.terrain;
	}

	public int newBullet(NewBullet newBullet, byte team, int playerID) {
		ServerPlayer player = null;
		for (ServerPlayer player2 : this.players) {
			if (playerID == player2.id) {
				player = player2;
			}
		}
		ServerProjectTile serverProjectTile = new ServerProjectTile(newBullet.x, newBullet.y, newBullet.dX,
				newBullet.dY, newBullet.speed, team, newBullet.dmg, newBullet.range, player.name, playerID);
		this.projectTiles.add(serverProjectTile);
		return serverProjectTile.getId();
	}

	public List<ServerPlayer> getPlayers() {
		return this.players;
	}

	public List<ServerPlayer> getPlayersTeam1() {
		List<ServerPlayer> team1 = new LinkedList<>();
		for (ServerPlayer player : this.players) {
			if (player.team == 1) {
				team1.add(player);
			}
		}
		return team1;
	}

	public List<ServerPlayer> getPlayersTeam2() {
		List<ServerPlayer> team2 = new LinkedList<>();
		for (ServerPlayer player : this.players) {
			if (player.team == 2) {
				team2.add(player);
			}
		}
		return team2;
	}

	public HashMap<Integer, ShieldData> getShields() {
		return this.shields;
	}

	public MyCollision getMyCollision() {
		return this.myCollision;
	}

	public void doSpecialAttack(SpecialAttack specialAttack) {
		if (specialAttack.type == SpecialAttack.DASH) {
			MyLine tempLine = new MyLine(specialAttack.x1, specialAttack.y1, specialAttack.x2, specialAttack.y2);
			for (ServerPlayer player : this.players) {
				if (player.getHitbox().collides(tempLine) && player.team != specialAttack.team) {
					player.health -= Hero.ASSASSIN_DASH_DMG;
					if (player.health <= 0) {
						player.respawn();
					}
				}
			}
			this.server.sendSpecialAttack(specialAttack);
		} else if (specialAttack.type == SpecialAttack.HOOK) {
			synchronized (this.specialProjectTiles) {
				this.specialProjectTiles.add(new ServerHook(specialAttack));
			}
			this.server.sendSpecialAttack(specialAttack);
		}
	}

	public void stun(int id, boolean stun) {
		this.server.stun(id, stun);
	}

	public void setPlayerToPos(int id, float x, float y) {
		this.server.sendSetPlayerToPos(x, y, id);
	}

	public void killBullet(int id) {
		this.server.killBullet(id);
	}

	public void removePlayer(int id) {
		synchronized (this.players) {
			for (int i = 0; i < this.players.size(); i++) {
				if (this.players.get(i).id == id) {
					this.players.remove(i);
					this.shields.remove(id);
					i--;
				}
			}
		}
	}

	public void playerKilledBy(int killerID, int killedID) {
		String killer = "";
		String killed = "";
		for (ServerPlayer player : this.players) {
			if (player.id == killedID) {
				player.kills++;
				killer = player.name;
			}
			if (player.id == killerID) {
				player.deaths++;
				killed = player.name;
			}
		}
		this.server.playerKilledBy(killer, killed);
	}
}
