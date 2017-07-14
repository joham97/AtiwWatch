package world.heros.bots.pathfinding;

import helper.FinallySmartVectors;

public class PathEntry implements Comparable<PathEntry>{

	private int x, y, endX, endY;
	private float distance, distanceEnd;
	private PathEntry prev;
	
	public PathEntry(int x, int y, int startX, int startY, int endX, int endY, float distance) {
		this.x = x;
		this.y = y;
		this.endX = endX;
		this.endY = endY;
		this.distance = distance;
		this.distanceEnd = new FinallySmartVectors(x-endX, y-endY).len();
	}

	public void setDistanceEnd(float distance) {
		this.distanceEnd = distance;
	}
	
	public float getDistanceEnd() {
		return distanceEnd;
	}
	
	public void setDistance(float distance) {
		this.distance = distance;
	}
	
	public float getDistance() {
		return distance;
	}

	public int getX() {
		return x;
	}
	
	public int getXInReal() {
		return x*50+25;
	}
		
	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public int getYInReal() {
		return y*50+25;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public boolean isEnd() {
		return x == endX && y == endY;
	}

	@Override
	public int compareTo(PathEntry pathEntry) {
		return Float.compare(this.distanceEnd, pathEntry.distanceEnd);
	}
	
	public PathEntry getPrev() {
		return prev;
	}
	
	public void setPrev(PathEntry prev) {
		this.prev = prev;
		if(prev != null){
			distance += new FinallySmartVectors(x-prev.getX(), y-prev.getY()).len();
		}
	}
}
