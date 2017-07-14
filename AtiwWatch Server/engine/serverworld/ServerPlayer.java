package serverworld;

import collision.MyRectangle;
import consts.Hero;
import protocol.SpecialAttack;
import protocol.worlddata.PlayerData;
import server.GameServer;

public class ServerPlayer {

	public int id;
	public String ip;
	public String name;

	public float x, y;
	public int width, height;
	public int kills, deaths;

	public byte team, hero;

	public float health;
	public int maxHealth;

	public byte extra;

	public float r, g, b;

	private SpecialAttack specialAttack;

	private GameServer server;

	public ServerPlayer(GameServer server) {
		this.server = server;
		this.maxHealth = 200;
		this.health = this.maxHealth;
		if(Math.random()<0.5) r = 0.5f + (float) Math.random() / 2f;
		if(Math.random()<0.5) g = 0.5f + (float) Math.random() / 2f;
		if(Math.random()<0.5) b = 0.5f + (float) Math.random() / 2f;
	}

	public void respawn() {
		this.health = this.maxHealth;
		this.server.respawn(id);
	}

	public void setPlayerData(PlayerData playerData) {
		this.x = playerData.x;
		this.y = playerData.y;
		this.width = playerData.width;
		this.height = playerData.height;
		this.maxHealth = playerData.maxHealth;
		this.hero = playerData.heroType;
		this.name = playerData.name;
		this.extra = playerData.extra;
	}

	public PlayerData getData() {
		PlayerData playerData = new PlayerData();

		playerData.id = this.id;
		playerData.x = this.x;
		playerData.y = this.y;
		playerData.width = this.width;
		playerData.height = this.height;
		playerData.team = this.team;
		playerData.name = this.name;
		playerData.heroType = this.hero;
		playerData.health = (int) this.health;
		playerData.maxHealth = this.maxHealth;
		playerData.kills = kills;
		playerData.deaths = deaths;
		playerData.extra = extra;

		return playerData;
	}

	public void heal(float value) {
		if (hero == Hero.MEDIC) {
			this.health += value / 2;
		} else {
			this.health += value;
		}
		if (health > maxHealth) {
			health = maxHealth;
		}
	}

	public void setSpecialAttack(SpecialAttack specialAttack) {
		this.specialAttack = specialAttack;
	}

	public SpecialAttack getSpecialAttack() {
		return specialAttack;
	}

	public void fullLife() {
		this.health = this.maxHealth;
	}

	public MyRectangle getHitbox() {
		return new MyRectangle(this.x - this.width / 2, this.y - this.height / 2, this.width, this.height);
	}

}
