package main;
import java.io.IOException;
import java.net.BindException;

import server.GameServer;

public class ServerStart {

	public static GameServer server;
	
	public static void main(String[] args) throws IOException, BindException{		
		while(true){
			server = new GameServer();
			server.loadContent();
			server.open();
			server.updateLoop();
			server.getServer().stop();
		}
	}
}
