package world.heros.bots;

import java.util.List;

import collision.MyLine;
import consts.Hero;
import helper.FinallySmartVectors;
import world.World;
import world.heros.OtherPlayer;
import world.heros.bots.pathfinding.Pathfinding;
import world.heros.herotypes.Grabber;

public class GrabberBot extends Grabber {

	private List<OtherPlayer> otherPlayers;

	public GrabberBot(World world) {
		super(world);
	}

	/**
	 * Jeden Frame
	 * super.update ruft updateMovement auf
	 */
	@Override
	public void update(float delta) {
		super.update(delta);
		//New Pathfinding
		if (pathfinding == null && world.getTerrain() != null && world.getTerrain().getBoolArray() != null) {
			pathfinding = new Pathfinding(world.getTerrain().getBoolArray());
		}
				
		//Andere Spieler
		otherPlayers = world.getOtherPlayers();

		//Search for nearest Enemies
		OtherPlayer nextEnemy = null;
		for (OtherPlayer otherPlayer : otherPlayers) {
			if (otherPlayer.getTeam() != team) {
				if (nextEnemy == null || new FinallySmartVectors(otherPlayer.x - this.x, otherPlayer.y - this.y)
						.len() < new FinallySmartVectors(nextEnemy.x - this.x, nextEnemy.y - this.y).len()) {
					nextEnemy = otherPlayer;
				}
				if (!this.world.getWalls().doesCollides(new MyLine(this.x, this.y, nextEnemy.x, nextEnemy.y)) && canDoSpecialAttack()){
					grab((int)nextEnemy.x, (int)nextEnemy.y);
				}
			}
		}
		
		//Shooting Enemies
		if (nextEnemy != null && new FinallySmartVectors(nextEnemy.x - this.x, nextEnemy.y - this.y).len() < Hero.GRABBER_SHOT_RANGE) {
			if (!this.world.getWalls().doesCollides(new MyLine(this.x, this.y, nextEnemy.x, nextEnemy.y)) && canShoot()) {
				shoot(nextEnemy.x, nextEnemy.y);				
			}
		}
		
		goOnPoint();
		
	}
	
	@Override
	public void updateMovement(float delta) {
		updateMovementBot(delta);
	}
}
