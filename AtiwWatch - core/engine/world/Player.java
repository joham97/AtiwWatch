package world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.socketserver.hebe.AtiwWatch;

import collision.MyLine;
import collision.MyVector;
import consts.MyColors;
import helper.FinallySmartVectors;
import helper.InputHandler;
import hud.InGameHud;
import protocol.worlddata.PlayerData;
import serverworld.ServerTerrain;

public abstract class Player {

	public int id;
	public String name;

	// Position
	public float x, y;
	public int width, height;

	// Movement
	protected int speed;
	protected int currentSpeed = 0;
	protected int speedBoost;

	// Team
	protected byte team;

	// Shooting
	protected float lastShot, shotFreq, shotDmg;
	protected int shotSpeed;

	// Collision
	public World world;

	public byte heroType = 0;

	protected boolean stopInputs = false;

	// Health
	protected int dmg;
	protected int health = 200;
	protected int maxHealth = 200;

	// Kills/Deaths
	protected int kills, deaths;

	protected float cooldown;
	protected float cooldownMax;

	private int stunned = 0;
	protected boolean showStunned = true;

	public Player(World world) {
		this.world = world;
		this.name = System.getProperty("user.name");
		if (this.name.contains(".")) {
			String[] split = this.name.split("\\.");
			if (split.length > 1 && split[1].length() > 1) {
				this.name = split[0] + "." + split[1].charAt(0) + split[1].charAt(1);
			}
		}
		InGameHud.reset();
	}

	public void update(float delta) {
		this.currentSpeed = this.speed;
		updateMovement(delta);
		speedBoost = 0;
		this.lastShot += delta;
	}

	protected void shoot() {
		FinallySmartVectors vector = new FinallySmartVectors(InputHandler.getUnprojectedX(),
				InputHandler.getUnprojectedY());
		shoot(vector.x, vector.y);
	}

	protected void shoot(float shootX, float shootY) {
		FinallySmartVectors vector = new FinallySmartVectors(shootX, shootY).sub(this.x, this.y).unit();
		this.world.newBullet((int) this.x, (int) this.y, vector.x, vector.y, this.dmg, this.shotSpeed, 50000);
	}

	public void updateMovement(float delta) {
		currentSpeed += speedBoost;
		float tempX = 0;
		float tempY = 0;
		if (!this.stopInputs()) {
			if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
				int moveToX = InputHandler.getUnprojectedX();
				int moveToY = InputHandler.getUnprojectedY();
				MyVector vec = new MyVector(moveToX - this.x, moveToY - this.y).unit();

				tempX = vec.x * delta * this.currentSpeed;
				tempY = vec.y * delta * this.currentSpeed;
			} else {
				if (InputHandler.gameKeyPressed(Keys.A)) {
					tempX = -1;
				}
				if (InputHandler.gameKeyPressed(Keys.D)) {
					tempX = 1;
				}
				if (InputHandler.gameKeyPressed(Keys.W)) {
					tempY = 1;
				}
				if (InputHandler.gameKeyPressed(Keys.S)) {
					tempY = -1;
				}
				MyVector temp = new MyVector(tempX, tempY).unit();
				tempX = temp.x * this.currentSpeed * delta;
				tempY = temp.y * this.currentSpeed * delta;
			}
		}
		doMovement(tempX, tempY);
	}

	protected void doMovement(float dX, float dY) {
		if (dX != 0 || dY != 0) {
			if (this.world != null) {
				if (this.world.getWalls().doesCollides(new MyLine(this.x, this.y, this.x + dX, this.y))) {
					dX = 0;
				}
				if (this.world.getWalls().doesCollides(new MyLine(this.x, this.y, this.x, this.y + dY))) {
					dY = 0;
				}

				if (this.world.getTerrain().get((int) ((this.x + dX) / 50),
						(int) (this.y / 50)) == ServerTerrain.WALL) {
					dX = 0;
				}
				if (this.world.getTerrain().get((int) ((this.x) / 50),
						(int) ((this.y + dY) / 50)) == ServerTerrain.WALL) {
					dY = 0;
				}
			}

			this.x += dX;
			this.y += dY;
		}
	}

	public void renderFirst(ShapeRenderer shape) {
	}

	public void render(ShapeRenderer shape) {
		shape.begin(ShapeType.Filled);
		shape.setColor((this.team == AtiwWatch.team) ? MyColors.CYAN : MyColors.RED);
		shape.rect(this.x - this.width / 2, this.y - this.height / 2, this.width, this.height);

		shape.setColor(MyColors.GRAY);
		shape.rect(this.x - this.width / 2, this.y + this.height / 2 + 10, this.width, 8);
		if (this.maxHealth != 0) {
			shape.setColor((this.team == AtiwWatch.team) ? MyColors.BLUE : MyColors.RED);
			shape.rect(this.x - this.width / 2, this.y + this.height / 2 + 10,
					this.width * this.health / this.maxHealth, 8);
		}
		shape.end();

	}

	public boolean canShoot() {
		if (this.lastShot > this.shotFreq) {
			this.lastShot = 0;
			return true;
		}
		return false;
	}

	public boolean isSpecialAttachTriggered() {
		return canDoSpecialAttack() && (InputHandler.gameKeyJustPressed(Keys.SHIFT_LEFT)
				|| (Gdx.input.isButtonPressed(Buttons.MIDDLE) && Gdx.input.justTouched()));
	}
	
	public boolean canDoSpecialAttack(){
		return !this.stopInputs() && this.cooldown <= 0;
	}

	protected boolean stopInputs() {
		return this.stopInputs || this.stunned > 0;
	}

	public int getDmg() {
		return this.dmg;
	}

	public int getShotSpeed() {
		return this.shotSpeed;
	}

	public int getKills() {
		return this.kills;
	}

	public int getDeaths() {
		return this.deaths;
	}

	public byte getTeam() {
		return this.team;
	}

	public void setTeam(byte team) {
		this.team = team;
	}

	public PlayerData getPlayerData() {
		PlayerData playerData = new PlayerData();
		playerData.x = this.x;
		playerData.y = this.y;
		playerData.name = this.name;
		playerData.width = this.width;
		playerData.height = this.height;
		playerData.heroType = this.heroType;
		playerData.maxHealth = this.maxHealth;

		return playerData;
	}

	public void resetCooldown() {
		this.cooldown = 0;
	}

	public void stun(int i) {
		stun(i, true);
	}

	public void stun(int i, boolean showStunned) {
		this.showStunned = showStunned;
		this.stunned += i;
		if (this.stunned < 0)
			this.stunned = 0;
	}

	public boolean isStunned() {
		return this.stunned > 0;
	}

	public boolean isShowStunned() {
		return this.showStunned;
	}

	public void respawn() {
		if (AtiwWatch.team == 1) {
			this.x = Terrain.team1Spawn.x * 50 + 25;
			this.y = Terrain.team1Spawn.y * 50 + 25;
		} else if (AtiwWatch.team == 2) {
			this.x = Terrain.team2Spawn.x * 50 + 25;
			this.y = Terrain.team2Spawn.y * 50 + 25;
		}
		this.stunned = 0;
		resetCooldown();
	}

	public float getKD() {
		if (this.deaths > 0) {
			return (float) this.kills / (float) this.deaths;
		} else {
			return this.kills;
		}
	}

	public void addSpeed(int value) {
		speedBoost = value;
	}

	public void setKills(int kills) {
		this.deaths = kills;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}
	
	public void setDeaths(int deaths) {
		this.kills = deaths;

	}

	public void setShotFreq(float shotFreq) {
		this.shotFreq = shotFreq;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public byte getHeroType() {
		return heroType;
	}
}
