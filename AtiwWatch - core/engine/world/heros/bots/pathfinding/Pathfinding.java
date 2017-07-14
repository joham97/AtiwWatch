package world.heros.bots.pathfinding;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Pathfinding {

	private boolean[][] terrain;

	private PriorityQueue<PathEntry> prioqueue;

	private PathEntry[][] entryMap;

	private int startX, startY, endX, endY;

	public Pathfinding(boolean[][] terrain) {
		this.terrain = terrain;		
	}

	public List<PathEntry> getPath(int startX, int startY, int endX, int endY){
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;

		
		entryMap = new PathEntry[terrain.length][terrain[0].length];

		prioqueue = new PriorityQueue<>();
		
		//Start Point
		PathEntry newPathEntry = new PathEntry(startX, startY, startX, startY, endX, endY, 0);
		entryMap[startX][startY] = newPathEntry;
		prioqueue.add(newPathEntry);
		
		while(!prioqueue.isEmpty()){
			PathEntry entry = null;
	
			entry = prioqueue.peek();
			prioqueue.poll();
			addPathEntry(entry, entry.getX() - 1, entry.getY() - 1);
			addPathEntry(entry, entry.getX(), entry.getY() - 1);
			addPathEntry(entry, entry.getX() + 1, entry.getY() - 1);
			addPathEntry(entry, entry.getX() - 1, entry.getY());
			addPathEntry(entry, entry.getX() + 1, entry.getY());
			addPathEntry(entry, entry.getX() - 1, entry.getY() + 1);
			addPathEntry(entry, entry.getX(), entry.getY() + 1);
			addPathEntry(entry, entry.getX() + 1, entry.getY() + 1);
		}

		
		List<PathEntry> path = new ArrayList<PathEntry>();
		PathEntry prevEntry = entryMap[endX][endY];
		while (prevEntry != null) {
			path.add(prevEntry);
			prevEntry = prevEntry.getPrev();
		}
		return path;
	}

	private void addPathEntry(PathEntry pathEntry, int x, int y) {
		if (0 <= x && x < terrain.length && 0 <= y && y < terrain[0].length && !terrain[x][y]) {
			float distance = (pathEntry==null)?0:pathEntry.getDistance();
			PathEntry newPathEntry = new PathEntry(x, y, startX, startY, endX, endY, distance);
			newPathEntry.setPrev(pathEntry);
			if (entryMap[x][y] == null || newPathEntry.getDistance() < entryMap[x][y].getDistance()) {
				entryMap[x][y] = newPathEntry;
				prioqueue.add(newPathEntry);
			}
		}
	}
	
	public boolean[][] getTerrain() {
		return terrain;
	}
}
