package world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.socketserver.hebe.AtiwWatch;

import collision.MyCollision;
import collision.MyRectangle;
import collision.MyVector;
import consts.MyColors;
import hud.InGameHud;
import serverworld.ServerTerrain;

public class Terrain {

	private Byte[][] terrain;

	private MyCollision walls;
	private boolean[][] boolArray;

	public static MyVector team1Spawn = new MyVector(0,0);
	public static MyVector team2Spawn = new MyVector(0,0);

	public Terrain(MyCollision walls) {
		this.walls = walls;
	}

	public void render(int x, int y, ShapeRenderer shape) {
		x /= 50;
		y /= 50;
		shape.begin(ShapeType.Filled);
		if (this.terrain != null) {
			for (int tileX = x - AtiwWatch.SCREEN_WIDTH_HEIGHT / 50 / 2 - 1; tileX < x
					+ AtiwWatch.SCREEN_WIDTH_HEIGHT / 50 / 2 + 2; tileX++) {
				for (int tileY = y - AtiwWatch.SCREEN_WIDTH_HEIGHT / 50 / 2 - 1; tileY < y
						+ AtiwWatch.SCREEN_WIDTH_HEIGHT / 50 / 2 + 2; tileY++) {
					if (tileX < 0 || tileY < 0 || tileX >= this.terrain.length || tileY >= this.terrain[0].length
							|| this.terrain[tileX][tileY] == ServerTerrain.WALL) {
						shape.setColor(MyColors.GREEN);
						shape.rect(tileX * 50, tileY * 50, 50, 50);
					} else if (this.terrain[tileX][tileY] == ServerTerrain.POINT) {
						if (InGameHud.pointTeam == 0) {
							shape.setColor(MyColors.GRAY_ALPHA);
						} else if (InGameHud.pointTeam == AtiwWatch.team) {
							shape.setColor(MyColors.BLUE_ALPHA);
						} else {
							shape.setColor(MyColors.RED_ALPHA);
						}
						shape.rect(tileX * 50, tileY * 50, 50, 50);
					} else if (this.terrain[tileX][tileY] == ServerTerrain.HEALPACK) {
						shape.setColor(MyColors.WHITE);
						shape.circle(tileX * 50 + 25, tileY * 50 + 24, 22);
						shape.setColor(MyColors.RED);
						shape.rect(tileX * 50 + 5, tileY * 50 + 20, 40, 10);
						shape.rect(tileX * 50 + 20, tileY * 50 + 5, 10, 40);
					} else if (this.terrain[tileX][tileY] == ServerTerrain.SPAWN_1
							|| this.terrain[tileX][tileY] == ServerTerrain.SPAWN_2) {
						shape.setColor(MyColors.YELLOW);
						shape.rect(tileX * 50, tileY * 50, 50, 50);
					} else if (this.terrain[tileX][tileY] == ServerTerrain.TELEPORTER_SRC
							|| this.terrain[tileX][tileY] == ServerTerrain.TELEPORTER_DEST) {
						shape.setColor(MyColors.PURPLE);
						shape.rect(tileX * 50, tileY * 50, 50, 50);
					} else if ((AtiwWatch.team != 1 && this.terrain[tileX][tileY] == ServerTerrain.WALL_1)
							|| (AtiwWatch.team != 2 && this.terrain[tileX][tileY] == ServerTerrain.WALL_2)) {
						shape.setColor(new Color(0f, 1f, 0f, 0.4f));
						shape.rect(tileX * 50, tileY * 50, 50, 50);
					}

				}
			}
		}
		shape.end();

	}

	public void setTerrain(Byte[][] terrain) {
		this.terrain = terrain;
		boolArray = new boolean[terrain.length][terrain[0].length];
		for (int x = 0; x < terrain.length; x++) {
			for (int y = 0; y < terrain[x].length; y++) {
				if (terrain[x][y] == ServerTerrain.WALL) {
					this.walls.add(new MyRectangle(x * 50, y * 50, 50, 50));
					boolArray[x][y] = true;
				}
				else if (terrain[x][y] == ServerTerrain.SPAWN_1) {
					team1Spawn = new MyVector(x, y);
				}
				else if (terrain[x][y] == ServerTerrain.SPAWN_2) {
					team2Spawn = new MyVector(x, y);
				}
			}
		}
		//this.walls.makeEfficent();
	}

	public byte get(int x, int y) {
		if (this.terrain != null && 0 <= x && x < this.terrain.length && 0 <= y && y < this.terrain[0].length) {
			if (this.terrain[x][y] == null) {
				return ServerTerrain.NONE;
			}
			return this.terrain[x][y];
		} else {
			return ServerTerrain.WALL;
		}
	}
	
	public boolean[][] getBoolArray() {
		return boolArray;
	}
}
