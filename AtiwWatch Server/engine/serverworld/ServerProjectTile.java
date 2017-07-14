package serverworld;

import collision.MyLine;
import protocol.worlddata.ShieldData;

public class ServerProjectTile {

	protected static int ID_COUNTER = 1;

	protected int senderID;
	
	public float type;
	protected int id;
	public float x, y;
	protected float mX, mY;
	protected float dX, dY;
	protected float speed, range;
	protected byte team;

	public boolean toDispose = false;

	protected int dmg;

	public ServerProjectTile(float x, float y, float dX, float dY, float speed, byte team, int dmg, int range, String senderName, int senderID) {
		this.id = ID_COUNTER++;
		this.x = x;
		this.y = y;
		this.dX = dX;
		this.dY = dY;
		this.speed = speed;
		this.team = team;
		this.dmg = dmg;
		this.range = range;
		this.senderID = senderID;
	}

	
	protected MyLine tempLine;
	public void update(float delta, ServerWorld world) {
		if (!this.toDispose) {
			this.range -= this.speed * delta;
			
			this.mX = this.speed * delta * this.dX;
			this.mY = this.speed * delta * this.dY;
			this.tempLine = new MyLine(this.x, this.y, this.x + this.mX, this.y + this.mY);
			if (world.getMyCollision().doesCollides(this.tempLine)) {
				this.toDispose = true;
				return;
			}
			for (ShieldData shield : world.getShields().values()) {
				if (shield.active && shield.team != this.team && this.tempLine.collides(
						new MyLine(shield.calcX1(), shield.calcY1(), shield.calcX2(), shield.calcY2())) != null) {
					this.toDispose = true;
					return;
				}
			}

			for (ServerPlayer player : world.getPlayers()) {
				if (player.getHitbox().collides(this.tempLine) && player.team != this.team) {
					hitSomeone(player, world);
					return;
				}
			}

			this.x += this.mX;
			this.y += this.mY;

			if(this.range<0){
				this.toDispose = true;
			}
		}

	}
	
	protected void hitSomeone(ServerPlayer player, ServerWorld world){
		player.health -= this.dmg;
		if (player.health <= 0) {
			player.respawn();
			world.playerKilledBy(this.senderID, player.id);
		}
		this.toDispose = true;
	}
	
	public int getId() {
		return this.id;
	}
}
