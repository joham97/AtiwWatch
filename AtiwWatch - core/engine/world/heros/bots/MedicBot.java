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
import world.heros.Medic;
import world.heros.OtherPlayer;
import world.heros.bots.pathfinding.PathEntry;
import world.heros.bots.pathfinding.Pathfinding;

public class MedicBot extends Medic {

	private Pathfinding pathfinding;
	private List<PathEntry> currentPath;
	private List<PathEntry> tempPath;

	private List<OtherPlayer> otherPlayers;

	private Random random;
	
	private float shootX, shootY; 

	public MedicBot(World world) {
		super(world);
		random = new Random();
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
		
	/**
	 * Arbeitet den aktuellen Pfad ab
	 * @param delta Zeit zwischen den Frames
	 */
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
	
	public Pathfinding getPathfinding() {
		return pathfinding;
	}
	
	@Override
	protected void shoot() {
		shoot(shootX, shootY);
	}
	
	public List<PathEntry> getCurrentPath() {
		return currentPath;
	}

	@Override
	public void respawn() {
		super.respawn();
		currentPath = null;
	}
	
}
