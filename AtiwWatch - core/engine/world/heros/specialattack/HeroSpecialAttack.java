package world.heros.specialattack;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import protocol.SpecialAttack;
import world.Player;
import world.heros.OtherPlayer;

public abstract class HeroSpecialAttack {

	protected int id;

	protected Player player;

	protected byte type;
	protected byte team;
	protected float x1, y1;
	protected float x2, y2;
	
	protected boolean toDispose = false;

	public HeroSpecialAttack(byte type) {
		this.type = type;
	}
	
	public HeroSpecialAttack(SpecialAttack specialAttack) {
		this.id = specialAttack.id;
		this.player = new OtherPlayer(null);
		this.player.id = specialAttack.player_id;
		this.x1 = specialAttack.x1;
		this.y1 = specialAttack.y1;
		this.x2 = specialAttack.x2;
		this.y2 = specialAttack.y2;
		this.type = specialAttack.type;
		this.team = specialAttack.team;
	}

	public SpecialAttack getData() {
		SpecialAttack specialAttack = new SpecialAttack();
		specialAttack.id = this.id;
		specialAttack.type = this.type;
		specialAttack.team = this.team;
		specialAttack.x1 = this.x1;
		specialAttack.y1 = this.y1;
		specialAttack.x2 = this.x2;
		specialAttack.y2 = this.y2;
		if(this.player != null){
			specialAttack.player_id = this.player.id;
		}
		return specialAttack;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getPlayerID() {
		return this.player.id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public byte getType() {
		return this.type;
	}

	public abstract void update(float delta);

	public abstract void render(ShapeRenderer shape);

	public boolean toDispose() {
		return this.toDispose;
	}

}
