package server;

import java.io.IOException;
import java.net.BindException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import helper.FPSCounter;
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
import serverworld.ServerPlayer;
import serverworld.ServerWorld;

public class GameServer {

	public static boolean STARTED = false;

	public static String IP = "";

	private Server server;
	private Kryo kryo;

	private ServerWorld world;

	private FPSCounter ups;

	public static boolean restart;

	public GameServer() {
		GameServer.restart = false;
		this.ups = new FPSCounter(1);
		this.server = new Server(50000, 50000);
		System.out.println("Server initialized...");
	}

	public void loadContent() {
		this.world = new ServerWorld(this);
		System.out.println("Terrain generated...");
	}

	public void open() throws IOException, BindException {
		this.kryo = this.server.getKryo();
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
		this.server.start();
		this.server.bind(25567, 25566);
		this.server.addListener(new GameServerListener(this));
		System.out.println("Server started...");
		STARTED = true;
	}

	public void updateLoop() {
		long thisFrame = System.currentTimeMillis();
		long lastFrame = thisFrame;
		float delta;

		// while(this.inGame!=0){
		while (!GameServer.restart) {
			thisFrame = System.currentTimeMillis();
			delta = (thisFrame - lastFrame) / 1000f;
			lastFrame = thisFrame;
			this.ups.add(delta);

			this.world.update(delta);

			try {
				int sleepTime = 17 - (int) (System.currentTimeMillis() - lastFrame);
				Thread.sleep(Math.max(sleepTime, 0));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for(Connection con : server.getConnections()){
			sendWorldData(con);
		}
		server.close();
	}

	public Server getServer() {
		return this.server;
	}

	public void addPlayerData(PlayerData playerData, int id, String ip) {
		ServerPlayer newPlayer = this.world.setPlayerData(playerData, id);
		newPlayer.ip = ip;
		newPlayer.hero = -1;
		this.world.setTeam(playerData.team, id);
	}

	public void setPlayerData(PlayerData playerData, int id) {
		this.world.setPlayerData(playerData, id);
	}

	public void removePlayer(int id) {
		this.world.removePlayer(id);
	}

	public void sendWorldData(Connection sender) {
		WorldData worldData = this.world.getData();
		worldData.ups = this.ups.getFPS();
		sender.sendTCP(worldData);
	}

	public void sendWorldTerrain(int id) {
		TerrainData terrain = new TerrainData();
		terrain.terrain = this.world.getTerrain().getTerrain();
		for (ServerPlayer player : this.world.getPlayers()) {
			if (id == player.id) {
				terrain.team = player.team;
				terrain.id = player.id;
			}
			this.server.sendToTCP(id, terrain);
		}
	}

	public void newBullet(Connection con, NewBullet newBullet) {
		for (ServerPlayer player : this.world.getPlayers()) {
			if (player.id == con.getID()) {
				int id = this.world.newBullet(newBullet, player.team, con.getID());
				newBullet.id = id;
				this.server.sendToAllTCP(newBullet);
			}
		}
	}

	public void killBullet(int id) {
		KillBullet killBullet = new KillBullet();
		killBullet.id = id;
		this.server.sendToAllTCP(killBullet);
	}

	public void stun(int id, boolean stunned) {
		StunData stun = new StunData();
		stun.stunned = stunned;
		this.server.sendToTCP(id, stun);
	}

	public void updateShield(int id, ShieldData shield) {
		this.world.getShields().put(id, shield);
	}

	public void sendSetPlayerToPos(float x, float y, int id) {
		SetPlayerToPos pos = new SetPlayerToPos();
		pos.x = x;
		pos.y = y;
		this.server.sendToTCP(id, pos);

	}

	public void doSpecialAttack(SpecialAttack specialAttack) {
		this.world.doSpecialAttack(specialAttack);
	}

	public void sendSpecialAttack(SpecialAttack specialAttack) {
		this.server.sendToAllTCP(specialAttack);
	}

	public void respawn(int id) {
		this.server.sendToTCP(id, new Respawn());
	}

	public void playerKilledBy(String killer, String killed){
		PlayerKilledBy pkb = new PlayerKilledBy();
		pkb.killed = killed;
		pkb.killer = killer;
		this.server.sendToAllTCP(pkb);
	}
	
	public byte teamWithLessPlayers() {
		return (this.world.getPlayersTeam1().size() > this.world.getPlayersTeam2().size()) ? (byte) 2 : (byte) 1;
	}
	
	public void recieveMessage(Message message, int id){
		for(ServerPlayer player : world.getPlayers()){
			if(player.id == id){
				message.r = player.r;
				message.g = player.g;
				message.b = player.b;
				message.message = player.name + ": " + message.message;
			}
		}
		server.sendToAllTCP(message);
	}
}