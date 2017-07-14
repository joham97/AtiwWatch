package networking;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.socketserver.hebe.AtiwWatch;

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
import protocol.worlddata.TerrainData;
import protocol.worlddata.WorldData;

public class GameClientListener extends Listener {

	private GameClient client;

	private FPSCounter ms;
	private long send, recieve;

	public GameClientListener(GameClient client) {
		this.client = client;
		this.ms = new FPSCounter(1);
	}

	@Override
	public void received(Connection con, Object object) {
		if (object instanceof WorldData) {
			this.recieve = System.currentTimeMillis();
			this.ms.add((this.recieve - this.send) / 1000f);
			((WorldData) object).ms = this.ms.getFPS();

			this.client.setWorldData((WorldData) object);
			this.client.sendPlayerData();

			this.send = System.currentTimeMillis();
		} else if (object instanceof TerrainData) {
			this.client.setWorldTerrain((TerrainData) object);
		} else if (object instanceof SetPlayerToPos) {
			this.client.setPlayerToPos((SetPlayerToPos) object);
		} else if (object instanceof TeamInformation) {
			AtiwWatch.team = ((TeamInformation) object).team;
		} else if (object instanceof NewBullet) {
			this.client.setNewBullet((NewBullet) object);
		} else if (object instanceof KillBullet) {
			this.client.killBullet(((KillBullet) object).id);
		} else if (object instanceof SpecialAttack) {
			this.client.newSpecialAttack((SpecialAttack) object);
		} else if (object instanceof StunData) {
			this.client.stun(((StunData) object).stunned);
		} else if (object instanceof Respawn) {
			this.client.respawn();
		} else if (object instanceof PlayerKilledBy) {
			PlayerKilledBy by = (PlayerKilledBy) object;
			this.client.playerGotKilled(by.killer + " killed by " + by.killed);
		}else if(object instanceof Message){
			client.recieveMessage((Message)object);
		}
	}

}
