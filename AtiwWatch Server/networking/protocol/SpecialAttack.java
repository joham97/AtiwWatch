package protocol;

public class SpecialAttack {

	public int id;
	
	public static final byte DASH = 1;
	public static final byte HOOK = 2;
	public static final byte AURA = 3;
	
	public int player_id;
	
	public byte special;
	public byte type;
	public byte team;
	public float x1, y1;
	public float x2, y2;
	
}
