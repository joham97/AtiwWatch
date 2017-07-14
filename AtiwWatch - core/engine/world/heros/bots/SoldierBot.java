package world.heros.bots;

import java.util.List;

import collision.MyLine;
import consts.Hero;
import helper.FinallySmartVectors;
import hud.InGameHud;
import world.World;
import world.heros.OtherPlayer;
import world.heros.bots.pathfinding.Pathfinding;
import world.heros.herotypes.Soldier;

public class SoldierBot extends Soldier {

	private List<OtherPlayer> otherPlayers;

	public SoldierBot(World world) {
		super(world);
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		// Andere Spieler
		otherPlayers = world.getOtherPlayers();

		// Search for Enemies
		OtherPlayer nextEnemy = null;
		OtherPlayer nextMedic = null;
		for (OtherPlayer otherPlayer : otherPlayers) {
			if (otherPlayer.getTeam() != team) {
				if (nextEnemy == null || new FinallySmartVectors(otherPlayer.x - this.x, otherPlayer.y - this.y)
						.len() < new FinallySmartVectors(nextEnemy.x - this.x, nextEnemy.y - this.y).len()) {
					nextEnemy = otherPlayer;
				}
			} else if (otherPlayer.getHeroType() == Hero.MEDIC
					&& (nextMedic == null || new FinallySmartVectors(otherPlayer.x - this.x, otherPlayer.y - this.y)
							.len() < new FinallySmartVectors(nextMedic.x - this.x, nextMedic.y - this.y).len())) {
				nextMedic = otherPlayer;
			}
		}

		// Shooting Enemies
		if (nextEnemy != null && new FinallySmartVectors(nextEnemy.x - this.x, nextEnemy.y - this.y).len() < 375) {
			if (!this.world.getWalls().doesCollides(new MyLine(this.x, this.y, nextEnemy.x, nextEnemy.y))
					&& canShoot()) {
				shoot(nextEnemy.x, nextEnemy.y);
			}
		}

		// New Pathfinding
		if (pathfinding == null && world.getTerrain() != null && world.getTerrain().getBoolArray() != null) {
			pathfinding = new Pathfinding(world.getTerrain().getBoolArray());
		}

		if (pathfinding != null) {
			if(health < maxHealth*0.7f && nextMedic != null){
				goTo((int)nextMedic.x, (int)nextMedic.y);
				InGameHud.brain = "Auf zum Medic";
			}else{
				goOnPoint();
				InGameHud.brain = "Auf zum Punkt";
			}
		}
	}
	
	@Override
	public void updateMovement(float delta) {
		updateMovementBot(delta);
	}
}
