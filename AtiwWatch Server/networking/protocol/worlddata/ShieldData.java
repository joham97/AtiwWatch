package protocol.worlddata;

public class ShieldData {

	public boolean active;
	public int x, y, width, team;
	public float angle;
	
	public int calcX1(){
		return this.x-(int)(Math.cos(((this.angle + 90)/360)*2*Math.PI)*this.width);
	}
	public int calcX2(){
		return this.x+(int)(Math.cos(((this.angle + 90)/360)*2*Math.PI)*this.width);
	}
	public int calcY1(){
		return this.y-(int)(Math.sin(((this.angle + 90)/360)*2*Math.PI)*this.width);
	}
	public int calcY2(){
		return this.y+(int)(Math.sin(((this.angle + 90)/360)*2*Math.PI)*this.width);
	}
	
}
