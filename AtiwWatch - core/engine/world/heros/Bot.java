package world.heros;

import java.util.List;
import java.util.Random;

import collision.MyVector;
import helper.FinallySmartVectors;
import serverworld.ServerTerrain;
import world.World;
import world.heros.bots.pathfinding.PathEntry;
import world.heros.bots.pathfinding.Pathfinding;

public class Bot extends Player{

	protected Pathfinding pathfinding;
	protected List<PathEntry> currentPath;
	protected List<PathEntry> tempPath;

	private Random random;
	
	public Bot(World world) {
		super(world);
		random = new Random();
	}
	
	public void updateMovementBot(float delta) {
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
