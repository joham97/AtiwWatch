package com.socketserver.hebe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;

import collision.MyRectangle;
import collision.MyVector;
import consts.Hero;
import consts.MyColors;
import helper.FPSCounter;
import helper.InputHandler;
import hud.InGameHud;
import networking.GameClient;
import world.World;
import world.heros.OtherPlayer;

public class LobbyScreen implements Screen {
	public static final int WIDTH = 750;
	public static final int HEIGHT = 750;

	private static final String introduction = ""
			+ "Ziel des Spiels ist es den Punkt in der Mitte der Karte einzunehmen,\n"
			+ "und einen Fortschritt von 100% zuerreichen.\n" + "Du bist Team Blau, vernichte Team Rot!\n"
			+ "Viel Spaﬂ!" + "\n" + "\n" + "Steuerung: A/S/D/W oder Rechte Maustaste\n"
			+ "Zielen/Schieﬂen: Linke Maustaste\n" + "Spezialattacke: Shift Links / Mausrad\n" + "\n"
			+ "NEW CONSOLE!! F9 (/respawn und Chat)";

	private OrthographicCamera cam;
	private FitViewport viewport;
	private FPSCounter fps;

	private final byte[] bots = { Hero.SOLDIER_BOT, Hero.ASSASSIN_BOT, 0, Hero.TANK_BOT, Hero.GRABBER_BOT, 0, Hero.MEDIC_BOT };
	private MyRectangle[] botsRects = new MyRectangle[this.bots.length];
	private MyVector[] botsTextPos = new MyVector[this.bots.length];

	private final byte[] heros = { Hero.SOLDIER, Hero.ASSASSIN, 0, Hero.TANK, Hero.GRABBER, 0, Hero.MEDIC };
	private MyRectangle[] heroRects = new MyRectangle[this.heros.length];
	private MyVector[] heroTextPos = new MyVector[this.heros.length];

	private AtiwWatch game;
	private GameClient client;
	private World world;

	GlyphLayout layout = new GlyphLayout();

	public LobbyScreen(AtiwWatch game, World world, GameClient client) {
		this.game = game;
		this.client = client;
		this.world = world;

		this.cam = new OrthographicCamera();
		this.cam.position.set(WIDTH / 2, HEIGHT / 2, 0);
		this.viewport = new FitViewport(WIDTH, HEIGHT, this.cam);
		InputHandler.setCam(this.cam);
		InputHandler.setViewport(this.viewport);
		this.fps = new FPSCounter(1);

		for (int i = 0; i < this.heros.length; i++) {
			if (heros[i] != 0) {
				this.heroRects[i] = new MyRectangle(WIDTH / 2 - (((float) this.heros.length) / 2) * 74 + i * 74, 5, 70,
						70);
				layout.setText(this.game.font, Hero.getHeroName(this.heros[i]));
				this.heroTextPos[i] = new MyVector(
						WIDTH / 2 - (((float) this.heros.length) / 2) * 74 + i * 74 + 35 - layout.width / 2,
						40 + layout.height / 2);
			}
		}
		for (int i = 0; i < this.bots.length; i++) {
			if (bots[i] != 0) {
				this.botsRects[i] = new MyRectangle(WIDTH / 2 - (((float) this.bots.length) / 2) * 74 + i * 74, 80, 70,
						35);
				layout.setText(this.game.font, Hero.getHeroName(this.bots[i]));
				this.botsTextPos[i] = new MyVector(
						WIDTH / 2 - (((float) this.bots.length) / 2) * 74 + i * 74 + 35 - layout.width / 2,
						40 + 57 + layout.height / 2);
			}
			
		}
	}

	private void update(float delta) {
		if (!client.isConnected()) {
			try {
				if (InGameHud.winnerTeam != 0)
					Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			game.newConnection();
		}

		this.fps.add(delta);
		world.update(delta);

		if (AtiwWatch.GAME_TO_START && AtiwWatch.hero != -1) {
			this.world.selectHero();
			this.game.startGame();
		}

		if (Gdx.input.isTouched()) {
			for (int i = 0; i < this.heros.length; i++) {
				if (this.heroRects[i] != null
						&& this.heroRects[i].contains(InputHandler.getUnprojectedX(), InputHandler.getUnprojectedY())) {
					AtiwWatch.hero = this.heros[i];
					client.sendPlayerData();
				}
			}
			for (int i = 0; i < this.bots.length; i++) {
				if (this.botsRects[i] != null
						&& this.botsRects[i].contains(InputHandler.getUnprojectedX(), InputHandler.getUnprojectedY())) {
					AtiwWatch.hero = this.bots[i];
					client.sendPlayerData();
				}
			}
		}
	}

	@Override
	public void render(float delta) {
		update(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		this.game.shape.setProjectionMatrix(this.viewport.getCamera().combined);
		this.game.batch.setProjectionMatrix(this.viewport.getCamera().combined);

		this.game.shape.begin(ShapeType.Filled);
		this.game.shape.setColor(MyColors.RED);
		for (int i = 0; i < this.heros.length; i++) {
			if (heros[i] != 0) {
				if (AtiwWatch.hero == this.heros[i]) {
					this.game.shape.rect(this.heroRects[i].x, this.heroRects[i].y, this.heroRects[i].width,
							this.heroRects[i].height);
				}
			}
		}
		for (int i = 0; i < this.bots.length; i++) {
			if (bots[i] != 0) {
				if (AtiwWatch.hero == this.bots[i]) {
					this.game.shape.rect(this.botsRects[i].x, this.botsRects[i].y, this.botsRects[i].width,
							this.botsRects[i].height);
				}
			}
		}
		this.game.shape.end();

		this.game.shape.begin(ShapeType.Line);
		this.game.shape.setColor(MyColors.WHITE);
		for (int i = 0; i < this.heroRects.length; i++) {
			if (heros[i] != 0) {
				this.game.shape.rect(this.heroRects[i].x, this.heroRects[i].y, this.heroRects[i].width,
						this.heroRects[i].height);
			}
		}
		for (int i = 0; i < this.botsRects.length; i++) {
			if (bots[i] != 0) {
				this.game.shape.rect(this.botsRects[i].x, this.botsRects[i].y,
						this.botsRects[i].width, this.botsRects[i].height);
			}
		}
		this.game.shape.end();

		this.game.batch.begin();
		this.game.font.setColor(Color.WHITE);
		this.game.font.draw(this.game.batch, introduction, 175, 525);
		this.game.font.draw(this.game.batch, "Select your hero...", 325, 170);

		this.game.font.draw(this.game.batch, "Team 1:", 10, 740);
		this.game.font.draw(this.game.batch, "Team 2:", 693, 740);

		int team1 = 0;
		int team2 = 0;
		if (AtiwWatch.team == 1) {
			team1++;
			this.game.font.draw(this.game.batch, world.getMe().name + " - ", 10, 725 - team1 * 15);

		} else if (AtiwWatch.team == 2) {
			team2++;
			layout.setText(this.game.font, world.getMe().name + " - ");
			this.game.font.draw(this.game.batch, world.getMe().name + " - ", WIDTH - layout.width - 5,
					725 - team2 * 15);

		}

		for (OtherPlayer player : world.getOtherPlayers()) {
			if (player.getTeam() == 1) {
				team1++;
				if (AtiwWatch.team == 1) {
					this.game.font.draw(this.game.batch, player.name + " - " + Hero.getHeroName(player.heroType), 10,
							725 - team1 * 15);
				} else {
					this.game.font.draw(this.game.batch, player.name, 10, 725 - team1 * 15);
				}
			} else if (player.getTeam() == 2) {
				team2++;
				if (AtiwWatch.team == 2) {
					layout.setText(this.game.font, player.name + " - " + Hero.getHeroName(player.heroType));
					this.game.font.draw(this.game.batch, player.name + " - " + Hero.getHeroName(player.heroType),
							WIDTH - layout.width - 5, 725 - team2 * 15);
				} else {
					layout.setText(this.game.font, player.name);
					this.game.font.draw(this.game.batch, player.name, WIDTH - layout.width - 5, 725 - team2 * 15);
				}
			}
		}

		for (int i = 0; i < this.heros.length; i++) {
			if (heros[i] != 0) {
				this.game.font.draw(this.game.batch, Hero.getHeroName(this.heros[i]), this.heroTextPos[i].x,
						this.heroTextPos[i].y);
			}
			if (bots[i] != 0) {
				this.game.font.draw(this.game.batch, Hero.getHeroName(this.bots[i]), this.botsTextPos[i].x,
						this.botsTextPos[i].y);
			}
		}

		this.game.batch.end();

		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	public void resize(int width, int height) {
		this.viewport.update(width, height);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
	}
}
