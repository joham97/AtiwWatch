package com.socketserver.hebe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.GUIConsole;

import helper.FPSCounter;
import helper.InputHandler;
import hud.InGameHud;
import hud.Scoreboard;
import hud.killfeed.KillFeed;
import networking.GameClient;
import world.World;

public class PlayScreen implements Screen {
	public static final int WIDTH = 750;
	public static final int HEIGHT = 750;

	private OrthographicCamera cam;
	private FitViewport viewport, viewportFest;
	private FPSCounter fps;

	private Scoreboard scoreboard;
	private KillFeed killFeed;

	private GUIConsole console;

	private AtiwWatch game;
	private GameClient client;

	private World world;

	private float timeTillRestart = 5f;

	public PlayScreen(AtiwWatch game, World world, GameClient client) {
		this.game = game;
		this.client = client;
		this.world = world;

		this.cam = new OrthographicCamera();
		this.viewport = new FitViewport(WIDTH, HEIGHT, this.cam);
		this.viewportFest = new FitViewport(WIDTH, HEIGHT);
		this.viewportFest.getCamera().position.set(WIDTH / 2, HEIGHT / 2, 0);
		InputHandler.setCam(this.cam);
		InputHandler.setViewport(this.viewport);
		this.fps = new FPSCounter(1);

		this.scoreboard = new Scoreboard(this.world);

		this.killFeed = new KillFeed();
		world.setKillFeed(this.killFeed);

		console = new GUIConsole();
		console.setCommandExecutor(new AtiwWatchCommandExecutor());
		console.setDisplayKeyID(Keys.F9);
		world.setConsole(console);
		console.printCommands();

		client.sendPlayerData();
	}

	private void update(float delta) {
		if (!client.isConnected()) {
			timeTillRestart -= delta;
			if (timeTillRestart <= 0 || InGameHud.winnerTeam == 0) {
				game.newConnection();
			}
		} else {
			this.fps.add(delta);
			InGameHud.fps = this.fps.getFPS();

			this.world.update(delta);
			InputHandler.allowGameInputs = !console.isVisible();
			if (console.isVisible()) {
				InGameHud.gotMessage = false;
			}
			if (InputHandler.gameKeyJustPressed(Keys.H) && this.world.playerInSpawn()) {
				this.game.selectHero();
			}
			this.killFeed.update(delta);
		}
	}

	@Override
	public void render(float delta) {
		update(delta);

		this.viewport.getCamera().position.set(this.world.getMe().x, this.world.getMe().y, 0);
		this.viewport.apply();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		this.game.shape.setProjectionMatrix(this.viewport.getCamera().combined);
		this.game.batch.setProjectionMatrix(this.viewport.getCamera().combined);
		this.world.render(this.game.shape, this.game.batch, this.game.font);

		
		/*List<PathEntry> path = null;
		boolean[][] terrain = null;
		if (world.getMe() instanceof GrabberBot) {
			path = ((GrabberBot) world.getMe()).getCurrentPath();
			terrain = ((GrabberBot) world.getMe()).getPathfinding().getTerrain();
		} else if (world.getMe() instanceof SoldierBot) {
			path = ((SoldierBot) world.getMe()).getCurrentPath();
			terrain = ((SoldierBot) world.getMe()).getPathfinding().getTerrain();
		} else if (world.getMe() instanceof MedicBot) {
			path = ((MedicBot) world.getMe()).getCurrentPath();
			terrain = ((MedicBot) world.getMe()).getPathfinding().getTerrain();
		}

		this.game.shape.begin(ShapeType.Filled);
		if(path != null){			
			for (int i = 0; i < path.size() - 1; i++) {
				this.game.shape.rectLine(path.get(i).getXInReal(), path.get(i).getYInReal(), path.get(i + 1).getXInReal(),
						path.get(i + 1).getYInReal(), 5);
			}
		}
		if(terrain != null){
			this.game.shape.setColor(Color.RED);
			for(int x = 0; x < terrain.length; x++){
				for(int y = 0; y < terrain[x].length; y++){
					if(terrain[x][y]){
						this.game.shape.rect(x*50, y*50, 50, 50);
					}
				}
			}
		}
		
		this.game.shape.end();*/
		Gdx.gl.glDisable(GL20.GL_BLEND);

		
		
		
		this.game.shape.setProjectionMatrix(this.viewportFest.getCamera().combined);
		this.game.batch.setProjectionMatrix(this.viewportFest.getCamera().combined);
		InGameHud.render(this.game.batch, this.game.font, this.game.shape);

		if (InputHandler.gameKeyPressed(Keys.TAB) || InGameHud.winnerTeam != 0) {
			this.scoreboard.render(this.game.shape, this.game.batch, this.game.font);
		} else {
			this.killFeed.render(this.game.shape, this.game.batch, this.game.font);
		}

		try {
			console.draw();
		} catch (Exception e) {
			// Nix
		}
	}

	private class AtiwWatchCommandExecutor extends CommandExecutor {
		@Override
		public void defaultCommand(String command) {
			client.sendMessage(command);
		}

		/*
		 * public void width(int width) { world.getMe().width = width; } public
		 * void height(int height) { world.getMe().height = height; } public
		 * void shotspeed(float shotfreq) { world.getMe().setShotFreq(shotfreq);
		 * } public void speed(int speed) { world.getMe().setSpeed(speed); }
		 */
		public void respawn() {
			world.getMe().respawn();
		}

		public void clear() {
			console.clear();
		}
	}

	@Override
	public void resize(int width, int height) {
		this.viewport.update(width, height);
		this.viewportFest.update(width, height);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}
}
