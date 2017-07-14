package main;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.socketserver.hebe.AtiwWatch;

public class DesktopGame {
	
    public static void main (String[] args) {
    	try {
			AtiwWatch.IP = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    	/*if(!AtiwWatch.TEST){
			String ip = "";
			try {
				ip = Inet4Address.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		    String input = JOptionPane.showInputDialog("Enter IP:", ip);
		    AtiwWatch.IP = input;
    	}*/
        start();
    }
    
    
    public static void start(){
    	LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = AtiwWatch.SCREEN_WIDTH_HEIGHT;
		config.height = AtiwWatch.SCREEN_WIDTH_HEIGHT;
		config.vSyncEnabled = true;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		LwjglApplicationConfiguration.disableAudio = true; // Disable audio
		new LwjglApplication(new AtiwWatch(), config);
    }
}
