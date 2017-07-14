package com.socketserver.hebe;

import java.io.IOException;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import consts.MyColors;
import main.ServerStart;
import networking.GameClient;
import world.World;

public class AtiwWatch extends Game {

	public final static int SCREEN_WIDTH_HEIGHT = 750;
	public final static boolean SERVER = true;
	public static String IP = null;

	public static boolean GAME_TO_START = false;
	public static int ID = -1;
	
	public static boolean TEST = false;

	public ShapeRenderer shape;
	public BitmapFont font;
	public SpriteBatch batch;

	public static byte team;
	public static byte hero = -1;
	private GameClient client;

	private World world;

	public PlayScreen playScreen;
	public LobbyScreen lobbyScreen;
	public WaitScreen waitScreen;

	@Override
	public void create() {
		this.batch = new SpriteBatch();
		this.shape = new ShapeRenderer();
		this.font = new BitmapFont();
		this.font.setColor(MyColors.WHITE);
		
		if(!TEST){
			try {
				if(Inet4Address.getLocalHost().getHostAddress().equals(AtiwWatch.IP)){
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								ServerStart.main(null);
							}catch (BindException be){
								System.out.println("Cannot start server, address already in use: bind");
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}		
			newConnection();
		}else{
			setScreen(new TestScreen(this));
		}
	}

	public void newConnection() {
		this.world = new World();
		this.client = new GameClient(world);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AtiwWatch.this.client.connect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		waitForServer();
	}

	public void waitForServer(){
		this.waitScreen = new WaitScreen(this, this.client);
		setScreen(this.waitScreen);
	}
	
	public void startGame() {
		this.playScreen = new PlayScreen(this, this.world, this.client);
		setScreen(this.playScreen);
	}

	public void selectHero() {
		AtiwWatch.hero=-1;
		this.lobbyScreen = new LobbyScreen(this, this.world, this.client);
		setScreen(this.lobbyScreen);
	}
}
