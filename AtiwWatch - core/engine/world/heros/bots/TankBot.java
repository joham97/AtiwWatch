package world.heros.bots;

import java.util.List;

import collision.MyLine;
import consts.Hero;
import helper.FinallySmartVectors;
import world.ProjectTile;
import world.World;
import world.heros.OtherPlayer;
import world.heros.bots.pathfinding.PathEntry;
import world.heros.bots.pathfinding.Pathfinding;
import world.heros.herotypes.Tank;

public class TankBot extends Tank {

	private List<OtherPlayer> otherPlayers;
	private List<ProjectTile> projectTiles;

	public TankBot(World world) {
		super(world);
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
	
	@Override
	public void updateMovement(float delta) {
		updateMovementBot(delta);
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
