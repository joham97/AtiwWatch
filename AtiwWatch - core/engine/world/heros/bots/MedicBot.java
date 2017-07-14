package world.heros.bots;

import java.util.List;

import collision.MyLine;
import consts.Hero;
import helper.FinallySmartVectors;
import hud.InGameHud;
import world.World;
import world.heros.OtherPlayer;
import world.heros.bots.pathfinding.Pathfinding;
import world.heros.herotypes.Medic;

public class MedicBot extends Medic {

	private List<OtherPlayer> otherPlayers;

	private float shootX, shootY; 

	public MedicBot(World world) {
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
		
		otherPlayers = world.getOtherPlayers();

		boolean needHealing = (this.health < this.maxHealth);
		OtherPlayer nextPlayer = null;
		OtherPlayer nextEnemy = null;
		for (OtherPlayer otherPlayer : otherPlayers) {
			if (otherPlayer.getTeam() == team) {
				if (nextPlayer == null || new FinallySmartVectors(otherPlayer.x - this.x, otherPlayer.y - this.y)
						.len() < new FinallySmartVectors(nextPlayer.x - this.x, nextPlayer.y - this.y).len()) {
					nextPlayer = otherPlayer;
				}
				if ((otherPlayer.getHealth() < otherPlayer.getMaxHealth())
						&& new FinallySmartVectors(otherPlayer.x - this.x, otherPlayer.y - this.y)
								.len() < Hero.MEDIC_AURA_RANGE) {
					needHealing = true;					
				}
			} else {
				if (nextEnemy == null || new FinallySmartVectors(otherPlayer.x - this.x, otherPlayer.y - this.y)
						.len() < new FinallySmartVectors(nextEnemy.x - this.x, nextEnemy.y - this.y).len()) {
					nextEnemy = otherPlayer;
				}
			}
		}
		
		if (needHealing && aura.getAuraType() != Hero.MEDIC_AURA_HEAL) {
			switchAura();
		}
		if (!needHealing && aura.getAuraType() == Hero.MEDIC_AURA_HEAL) {
			switchAura();
		}
		
		//Shooting Enemies
		if (nextEnemy != null && new FinallySmartVectors(nextEnemy.x - this.x, nextEnemy.y - this.y).len() < 375) {
			if (!this.world.getWalls().doesCollides(new MyLine(this.x, this.y, nextEnemy.x, nextEnemy.y)) && canShoot()) {
				shootX = nextEnemy.x;
				shootY = nextEnemy.y;
				startShooting();
			}
		}	
		
		/**
		 * Movement Logic
		 */
		if (pathfinding != null) {
			if (nextPlayer != null) {
				tempPath = this.pathfinding.getPath((int) this.x / 50, (int) this.y / 50, (int) nextPlayer.x / 50,
						(int) nextPlayer.y / 50);
				if (tempPath.size() > 1) {
					currentPath = tempPath;
					InGameHud.brain = "Auf zu " + nextEnemy.name;
				}
			} else {
				goOnPoint();
			}
		}
	}
	
	@Override
	public void updateMovement(float delta) {
		updateMovementBot(delta);
	}
	
	@Override
	protected void shoot() {
		shoot(shootX, shootY);
	}	
}
