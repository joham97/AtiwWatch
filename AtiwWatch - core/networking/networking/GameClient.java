package networking;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.socketserver.hebe.AtiwWatch;

import consts.Hero;
import protocol.SpecialAttack;
import protocol.events.KillBullet;
import protocol.events.NewBullet;
import protocol.events.PlayerKilledBy;
import protocol.events.Respawn;
import protocol.events.SetPlayerToPos;
import protocol.events.StunData;
import protocol.info.TeamInformation;
import protocol.message.Message;
import protocol.worlddata.HealthPackData;
import protocol.worlddata.PlayerData;
import protocol.worlddata.PointData;
import protocol.worlddata.ShieldData;
import protocol.worlddata.TerrainData;
import protocol.worlddata.WorldData;
import world.World;
import world.heros.Tank;

public class GameClient {

	private Client client;
	private Kryo kryo;

	public WorldData currentWorld;
	private World world;

	public SetPlayerToPos newPos;

	public GameClient(World world) {
		this.client = new Client(50000, 50000);
		this.world = world;
		world.setClient(this);
	}

	public void connect() throws IOException {
		this.kryo = this.client.getKryo();
		this.kryo.register(PlayerData.class);
		this.kryo.register(PlayerData[].class);
		this.kryo.register(WorldData.class);
		this.kryo.register(Byte[].class);
		this.kryo.register(Byte[][].class);
		this.kryo.register(TerrainData.class);
		this.kryo.register(NewBullet.class);
		this.kryo.register(PointData.class);
		this.kryo.register(String[].class);
		this.kryo.register(SetPlayerToPos.class);
		this.kryo.register(ShieldData.class);
		this.kryo.register(ShieldData[].class);
		this.kryo.register(SpecialAttack.class);
		this.kryo.register(HealthPackData.class);
		this.kryo.register(HealthPackData[].class);
		this.kryo.register(TeamInformation.class);
		this.kryo.register(KillBullet.class);
		this.kryo.register(StunData.class);
		this.kryo.register(Respawn.class);
		this.kryo.register(PlayerKilledBy.class);
		this.kryo.register(Message.class);
		this.client.start();
		this.client.addListener(new GameClientListener(this));

		while (!isConnected()) {
			try {
				while(AtiwWatch.IP == null) {}					 
				this.client.connect(500000, AtiwWatch.IP, 25567, 25566);
			} catch (IOException e) {
			}

			// this.client.connect(5000, Inet4Address.getLocalHost(), 25567,
			// 25566);
		}

		this.sendPlayerData();
	}

	public boolean isConnected() {
		return this.client.isConnected();
	}

	public void setWorldData(WorldData worldData) {
		this.currentWorld = worldData;
	}

	public void sendPlayerData() {
		this.client.sendTCP(this.world.getMe().getPlayerData());
		if (this.world.getMe().heroType == Hero.TANK) {
			this.client.sendTCP(((Tank) this.world.getMe()).getShield().getShieldData());
		}
	}

	public void sendMessage(String msg) {
		Message message = new Message();
		message.message = msg;
		this.client.sendTCP(message);
	}

	public void sendNewBullet(int x, int y, float x2, float y2, int dmg, int speed, int range) {
		NewBullet newBullet = new NewBullet();
		newBullet.x = x;
		newBullet.y = y;
		newBullet.dX = x2;
		newBullet.dY = y2;
		newBullet.dmg = dmg;
		newBullet.speed = speed;
		newBullet.range = range;
		newBullet.team = AtiwWatch.team;
		this.client.sendTCP(newBullet);
	}

	public void sendShield(ShieldData shieldData) {
		this.client.sendTCP(shieldData);
	}

	public void newSpecialAttack(SpecialAttack specialAttack) {
		this.world.newSpecialAttack(specialAttack);
	}

	public void setNewBullet(NewBullet newBullet) {
		this.world.newBullet(newBullet);
	}

	public void setWorldTerrain(TerrainData terrainData) {
		AtiwWatch.GAME_TO_START = true;
		AtiwWatch.ID = terrainData.id;
		this.world.setTerrain(terrainData);
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setPlayerToPos(SetPlayerToPos object) {
		this.newPos = object;
	}

	public void sendSpecialAttack(SpecialAttack specialAttack) {
		this.client.sendTCP(specialAttack);
	}

	public void killBullet(int id) {
		this.world.killBullet(id);
	}

	public void stun(boolean stunned) {
		this.world.stun(stunned);
	}

	public void respawn() {
		if (this.world.getMe() != null) {
			this.world.getMe().respawn();
		}
	}

	public void playerGotKilled(String message) {
		this.world.playerGotKilled(message);
	}

	public void recieveMessage(Message message) {
		world.recieveMessage(message);
	}
}
