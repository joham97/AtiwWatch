package com.socketserver.hebe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;

import helper.FPSCounter;
import helper.InputHandler;
import networking.GameClient;

public class WaitScreen implements Screen {
	public static final int WIDTH = 750;
	public static final int HEIGHT = 750;
	
	private OrthographicCamera cam;
	private FitViewport viewport;
	private FPSCounter fps;

	private AtiwWatch game;
	private GameClient client;

	public WaitScreen(AtiwWatch game, GameClient client) {
		this.game = game;
		this.client = client;

		this.cam = new OrthographicCamera();
		this.cam.position.set(WIDTH / 2, HEIGHT / 2, 0);
		this.viewport = new FitViewport(WIDTH, HEIGHT, this.cam);
		InputHandler.setCam(this.cam);
		InputHandler.setViewport(this.viewport);
		this.fps = new FPSCounter(1);

	}

	private void update(float delta) {
		this.fps.add(delta);
		if(client.isConnected()){
			game.selectHero();	
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
		
		this.game.shape.end();

		
		this.game.batch.begin();
		this.game.font.setColor(Color.WHITE);
		this.game.font.draw(this.game.batch, "Searching for server...", 325, 375);
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
