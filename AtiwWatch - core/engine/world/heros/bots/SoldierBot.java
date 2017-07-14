package world.heros.bots;

import java.util.List;
import java.util.Random;

import collision.MyLine;
import collision.MyVector;
import consts.Hero;
import helper.FinallySmartVectors;
import hud.InGameHud;
import serverworld.ServerTerrain;
import world.World;
import world.heros.OtherPlayer;
import world.heros.Soldier;
import world.heros.bots.pathfinding.PathEntry;
import world.heros.bots.pathfinding.Pathfinding;

public class SoldierBot extends Soldier {

	private Pathfinding pathfinding;
	private List<PathEntry> currentPath;
	private List<PathEntry> tempPath;

	private List<OtherPlayer> otherPlayers;

	private Random random;

	public SoldierBot(World world) {
		super(world);
		random = new Random();
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
		//Moving
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
		}else{
			InGameHud.brain = "Kein Bock ";
			if(currentPath == null){
				InGameHud.brain += "weil ich keinen Pfad hab.";
			}else if(currentPath.size() == 1){
				currentPath = null;
				InGameHud.brain += "weil ich da bin.";
			}else if(currentPath.size() == 0){
				currentPath = null;
				InGameHud.brain += "weil ich nirgends bin.";
			}
		}
	}
	
	/**
	 * Geht zu einem bestimmten Punkt
	 * In Pixel angegeben, geht aber in die Mittes der jeweiligen Kachel 
	 * @param gotoX X in Pixel (1 Kachel = 50px)
	 * @param gotoY Y in Pixel (1 Kachel = 50px)
	 */
	public void goTo(int gotoX, int gotoY){
		currentPath = this.pathfinding.getPath((int) this.x / 50, (int) this.y / 50, gotoX/50, gotoY/50);
	}
	
	/**
	 * Er geht auf eine zufällige Kachel auf dem Punkt
	 */
	public void goOnPoint(){
		if (pathfinding != null) {
			//New Path
			if (currentPath == null || currentPath.size() < 2) {
				int gotoX = 0;
				int gotoY = 0;
				do{
					do {
						gotoX = random.nextInt(world.getTerrain().getBoolArray().length);
						gotoY = random.nextInt(world.getTerrain().getBoolArray()[0].length);
					} while (world.getTerrain().get(gotoX, gotoY) != ServerTerrain.POINT);
					tempPath = this.pathfinding.getPath((int) this.x / 50, (int) this.y / 50, gotoX, gotoY);
					
				} while (tempPath.size() < 3);
				currentPath = tempPath;		
				InGameHud.brain = "Auf zum Punkt";
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
