package helper;

public class FinallySmartVectors {

	public float x = 0;
	public float y = 0;
		
	public FinallySmartVectors() {}

	public FinallySmartVectors(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public FinallySmartVectors(FinallySmartVectors fsm) {
		this.x = fsm.x;
		this.y = fsm.y;
	}
	
	public FinallySmartVectors add(float x, float y){
		return new FinallySmartVectors(this.x+x, this.y+y);
	}
	public FinallySmartVectors add(FinallySmartVectors vec){
		return new FinallySmartVectors(x+vec.x, y+vec.y);
	}

	public FinallySmartVectors sub(float x, float y){
		return new FinallySmartVectors(this.x-x, this.y-y);
	}
	public FinallySmartVectors sub(FinallySmartVectors vec){
		return new FinallySmartVectors(x-vec.x, y-vec.y);
	}
	
	public FinallySmartVectors scl(float scala){
		return new FinallySmartVectors(x*scala, y*scala);
	}

	public FinallySmartVectors unit(){
		return new FinallySmartVectors(x/len(), y/len());
	}
	
	public float dst(FinallySmartVectors vec){
		return vec.sub(this).len();
	}
	
	public float len(){
		return (float )Math.sqrt(x*x+y*y);
	}	
	
	public double getAngle() {
		return Math.atan2(this.y, this.x);
	}
	
	@Override
	public String toString() {
		return "["+x+", "+y+"]";
	}
	
	public String toIString() {
		return "["+(int)x+", "+(int)y+"]";
	}
	
}
