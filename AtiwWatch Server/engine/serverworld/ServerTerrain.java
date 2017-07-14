package serverworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import collision.MyRectangle;
import collision.MyVector;
import serverworld.entities.HealthPack;

public class ServerTerrain {

	public static final Byte NONE = 0;
	public static final Byte WALL = 1;
	public static final Byte WALL_1 = 2;
	public static final Byte WALL_2 = 3;
	public static final Byte SPAWN_1 = 4;
	public static final Byte SPAWN_2 = 5;
	public static final Byte POINT = 6;
	public static final Byte HEALPACK = 7;
	public static final Byte TELEPORTER_SRC = 8;
	public static final Byte TELEPORTER_DEST = 9;

	private Byte[][] terrain;
	private ServerWorld world;

	private ServerPoint serverPoint;

	private List<ServerPlayer> players;

	public static MyVector team1Spawn;
	public static MyVector team2Spawn;

	private ArrayList<HealthPack> healthPacks;
	
	public final boolean TEST_MAP = false;
	
	public ServerTerrain(List<ServerPlayer> players, ServerWorld world) {
		this.players = players;
		this.world = world;
		this.serverPoint = new ServerPoint();
		team1Spawn = new MyVector();
		team2Spawn = new MyVector();
		this.healthPacks = new ArrayList<>();
	}

	public void update(float delta) {
		boolean onPoint1 = false;
		boolean onPoint2 = false;
		byte whichTeam = 0;

		for(HealthPack healthPack : this.healthPacks){
			healthPack.update(delta);
		}
		
		for (ServerPlayer player : this.players) {
			if (inWorld((int) player.x / 50, (int) player.y / 50)) {
				if (this.terrain[(int) player.x / 50][(int) player.y / 50] == POINT) {
					if (player.team == 1) {
						onPoint1 = true;
						whichTeam = 1;
					} else if (player.team == 2) {
						onPoint2 = true;
						whichTeam = 2;
					}
				}else if (this.terrain[(int) player.x / 50][(int) player.y / 50] == HEALPACK) {
					for(HealthPack healthPack : this.healthPacks){
						if(healthPack.x == (int) player.x / 50 && healthPack.y == (int) player.y / 50){
							healthPack.use(player);
						}
					}
				}else if (this.terrain[(int) player.x / 50][(int) player.y / 50] == SPAWN_1
						|| this.terrain[(int) player.x / 50][(int) player.y / 50] == SPAWN_2){
					player.fullLife();					
				}else if (this.terrain[(int) player.x / 50][(int) player.y / 50] == TELEPORTER_SRC){
					for(int i = 0; i < terrain.length; i++){
						for(int j = 0; j < terrain[i].length; j++){
							if(terrain[i][j] == TELEPORTER_DEST){
								world.setPlayerToPos(player.id, i*50+25, j*50+25);
							}
						}
					}
				}
			}
		}

		this.serverPoint.update(delta, onPoint1, onPoint2, whichTeam);
	}

	public void loadTerrain() {
		try {
			BufferedReader br = null;
			if(this.TEST_MAP){
				br = new BufferedReader(new FileReader(new File("").getAbsolutePath() + "/src/testterrain.txt"));
			}else{
				br = new BufferedReader(new FileReader(new File("").getAbsolutePath() + "/src/terrain.txt"));
			}
			String line = null;
			int width = 0;
			int height = 0;
			while ((line = br.readLine()) != null) {
				height = line.length();
				width++;
			}
			br.close();

			this.terrain = new Byte[width][height];
			if(this.TEST_MAP){
				br = new BufferedReader(new FileReader(new File("").getAbsolutePath() + "/src/testterrain.txt"));
			}else{
				br = new BufferedReader(new FileReader(new File("").getAbsolutePath() + "/src/terrain.txt"));
			}
			int x = 0;
			int y = 0;
			while ((line = br.readLine()) != null) {
				y = 0;
				for (char c : line.toCharArray()) {
					try{
						addToTerrain(x, y, Byte.parseByte(c + ""));
					}catch(NumberFormatException nfe){
						System.out.println(x+" "+y);
					}
					y++;
				}
				x++;
			}
			this.world.getMyCollision().makeEfficent();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addToTerrain(int x, int y, Byte type) {
		if (type == WALL || type == WALL_1 || type == WALL_2) {
			this.terrain[x][y] = type;
		} else if (type == SPAWN_1) {
			team1Spawn.x = x * 50;
			team1Spawn.y = y * 50;
			this.terrain[x][y] = type;
		} else if (type == SPAWN_2) {
			team2Spawn.x = x * 50;
			team2Spawn.y = y * 50;
			this.terrain[x][y] = type;
		} else if (type == POINT) {
			this.terrain[x][y] = type;
		} else if (type == HEALPACK) {
			this.terrain[x][y] = type;
			this.healthPacks.add(new HealthPack(x, y));
		} else if (type == TELEPORTER_SRC) {
			this.terrain[x][y] = type;
		} else if (type == TELEPORTER_DEST) {
			this.terrain[x][y] = type;
		} else {
			this.terrain[x][y] = NONE;
		}
		if (type == WALL || type == WALL_1 || type == WALL_2) {
			this.world.getMyCollision().add(new MyRectangle(x * 50, y * 50, 50, 50));
		}
	}

	public Byte[][] getTerrain() {
		return this.terrain;
	}

	public ServerPoint getPoint() {
		return this.serverPoint;
	}

	public boolean inWorld(int x, int y) {
		return (0 <= x && 0 <= y && x < this.terrain.length && y < this.terrain[0].length);
	}
	
	public ArrayList<HealthPack> getHealthPacks() {
		return this.healthPacks;
	}
}
