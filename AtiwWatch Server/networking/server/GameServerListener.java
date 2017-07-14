package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import protocol.SpecialAttack;
import protocol.events.NewBullet;
import protocol.info.TeamInformation;
import protocol.message.Message;
import protocol.worlddata.PlayerData;
import protocol.worlddata.ShieldData;

public class GameServerListener extends Listener {

	private GameServer gameServer;

	public GameServerListener(GameServer gameServer) {
		this.gameServer = gameServer;
	}

	@Override
	public void connected(Connection sender) {
		PlayerData playerData = new PlayerData();
		playerData.team = this.gameServer.teamWithLessPlayers();
		this.gameServer.addPlayerData(playerData, sender.getID(), sender.getRemoteAddressTCP().getAddress().toString());
		TeamInformation team = new TeamInformation();
		team.team = playerData.team;
		sender.sendTCP(team);		
		this.gameServer.sendWorldTerrain(sender.getID());
	}
	
	@Override
	public void disconnected(Connection arg0) {
		this.gameServer.removePlayer(arg0.getID());
	}

	@Override
	public void received(Connection sender, Object object) {
		if (object instanceof PlayerData) {
			this.gameServer.setPlayerData((PlayerData) object, sender.getID());
			this.gameServer.sendWorldData(sender);
		} else if (object instanceof NewBullet) {
			this.gameServer.newBullet(sender, (NewBullet) object);
		} else if (object instanceof ShieldData) {
			this.gameServer.updateShield(sender.getID(), (ShieldData) object);
		}else if (object instanceof SpecialAttack){
			SpecialAttack specialAttack = (SpecialAttack) object;
			specialAttack.player_id = sender.getID();
			this.gameServer.doSpecialAttack(specialAttack);
		}else if(object instanceof Message){
			gameServer.recieveMessage((Message)object, sender.getID());
		}
	}
}
