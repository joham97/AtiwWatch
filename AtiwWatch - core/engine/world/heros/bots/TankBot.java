package world.heros.bots;

import java.util.List;
import java.util.Random;

import collision.MyLine;
import collision.MyVector;
import consts.Hero;
import helper.FinallySmartVectors;
import serverworld.ServerTerrain;
import world.ProjectTile;
import world.World;
import world.heros.OtherPlayer;
import world.heros.Tank;
import world.heros.bots.pathfinding.PathEntry;
import world.heros.bots.pathfinding.Pathfinding;

public class TankBot extends Tank {

	private Pathfinding pathfinding;
	private List<PathEntry> currentPath;
	private List<PathEntry> tempPath;

	private List<OtherPlayer> otherPlayers;
	private List<ProjectTile> projectTiles;

	private Random random;

	public TankBot(World world) {
		super(world);
		random = new Random();
	}

	/**
	 * Jeden Frame super.update ruft updateMovement auf
	 */
	@Override
	public void update(float delta) {
		super.update(delta);
		// New Pathfinding
		if (pathfinding == null && world.getTerrain() != null && world.getTerrain().getBoolArray() != null) {
			pathfinding = new Pathfinding(world.getTerrain().getBoolArray());
		}

		// Andere Spieler
		otherPlayers = world.getOtherPlayers();

		// Search for nearest Enemies
		OtherPlayer nextEnemy = null;
		for (OtherPlayer otherPlayer : otherPlayers) {
			if (otherPlayer.getTeam() != team) {
				if (nextEnemy == null || new FinallySmartVectors(otherPlayer.x - this.x, otherPlayer.y - this.y)
						.len() < new FinallySmartVectors(nextEnemy.x - this.x, nextEnemy.y - this.y).len()) {
					nextEnemy = otherPlayer;
				}
			}
		}

		// Shooting Enemies
		if (nextEnemy != null && new FinallySmartVectors(nextEnemy.x - this.x, nextEnemy.y - this.y)
				.len() < Hero.TANK_SHATTER_RANGE / 2) {
			if (!this.world.getWalls().doesCollides(new MyLine(this.x, this.y, nextEnemy.x, nextEnemy.y))
					&& canShoot()) {
				shoot(nextEnemy.x, nextEnemy.y);
			}
		}

		if (nextEnemy != null && canDoSpecialAttack()) {
			goTo((int) nextEnemy.x, (int) nextEnemy.y);
		}else{
			goOnPoint();
		}

	}

	@Override
	protected void updateShield() {
		projectTiles = world.getProjectTiles();

		ProjectTile nextProjectTile = null;
		for (ProjectTile projectTile : projectTiles) {
			if (projectTile.team != team) {
				float thisLen = new FinallySmartVectors(projectTile.x - this.x, projectTile.y - this.y).len();
				if (thisLen > Hero.TANK_SHIELD_DISTANCE) {
					if (nextProjectTile == null) {
						nextProjectTile = projectTile;
					} else {
						float nextLen = new FinallySmartVectors(nextProjectTile.x - this.x, nextProjectTile.y - this.y)
								.len();
						if (thisLen < nextLen) {
							nextProjectTile = projectTile;
						}
					}
				}
			}
		}
		if (nextProjectTile != null) {
			shieldX = nextProjectTile.x;
			shieldY = nextProjectTile.y;
		}
	}

	/**
	 * Arbeitet den aktuellen Pfad ab
	 * 
	 * @param delta
	 *            Zeit zwischen den Frames
	 */
	@Override
	public void updateMovement(float delta) {
		// Moving
		if (currentPath != null && currentPath.size() > 1) {
			PathEntry entry = currentPath.get(currentPath.size() - 1);
			if (new FinallySmartVectors(entry.getXInReal() - this.x, entry.getYInReal() - this.y).len() < 50) {
				currentPath.remove(currentPath.size() - 1);
			}
			entry = currentPath.get(currentPath.size() - 1);
			MyVector temp = new MyVector(entry.getXInReal() - this.x, entry.getYInReal() - this.y).unit()
					.scl(this.currentSpeed).scl(delta);
			this.x += temp.x;
			this.y += temp.y;
		}
	}

	/**
	 * Geht zu einem bestimmten Punkt In Pixel angegeben, geht aber in die
	 * Mittes der jeweiligen Kachel
	 * 
	 * @param gotoX
	 *            X in Pixel (1 Kachel = 50px)
	 * @param gotoY
	 *            Y in Pixel (1 Kachel = 50px)
	 */
	public void goTo(int gotoX, int gotoY) {
		currentPath = this.pathfinding.getPath((int) this.x / 50, (int) this.y / 50, gotoX / 50, gotoY / 50);
	}

	/**
	 * Er geht auf eine zufällige Kachel auf dem Punkt
	 */
	public void goOnPoint() {
		if (pathfinding != null) {
			// New Path
			if (currentPath == null || currentPath.size() < 2) {
				int gotoX = 0;
				int gotoY = 0;
				do {
					do {
						gotoX = random.nextInt(world.getTerrain().getBoolArray().length);
						gotoY = random.nextInt(world.getTerrain().getBoolArray()[0].length);
					} while (world.getTerrain().get(gotoX, gotoY) != ServerTerrain.POINT);
					tempPath = this.pathfinding.getPath((int) this.x / 50, (int) this.y / 50, gotoX, gotoY);

				} while (tempPath.size() < 3);
				currentPath = tempPath;
			}
		}
	}

	public List<PathEntry> getCurrentPath() {
		return currentPath;
	}

	public Pathfinding getPathfinding() {
		return pathfinding;
	}

	@Override
	public void respawn() {
		super.respawn();
		currentPath = null;
	}

}
