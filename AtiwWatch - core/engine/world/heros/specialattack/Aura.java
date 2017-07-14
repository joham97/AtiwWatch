package world.heros.specialattack;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import consts.Hero;
import consts.MyColors;
import protocol.SpecialAttack;

public class Aura extends HeroSpecialAttack{

	public static final byte SPEED = 1;
	public static final byte HEAL = 2;
	
	private byte auraType = SPEED;
	
	public Aura() {
		super(SpecialAttack.AURA);
	}

	public Aura(SpecialAttack specialAttack) {
		super(specialAttack);
	}
	
	@Override
	public void update(float delta) {}

	@Override
	public void render(ShapeRenderer shape) {
		shape.begin(ShapeType.Line);
		if(auraType == HEAL){
			shape.setColor(MyColors.YELLOW);
		}else if(auraType == SPEED){
			shape.setColor(MyColors.GREEN);
		}
		shape.circle(player.x, player.y, Hero.MEDIC_AURA_RANGE);
		shape.end();
	}
	
	public void switchAura() {
		if(auraType == HEAL){
			auraType = SPEED;
		}else if(auraType == SPEED){
			auraType = HEAL;
		}
	}
	
	@Override
	public SpecialAttack getData() {
		SpecialAttack data = super.getData();
		data.special = auraType;
		return data;
	}
	
	public void setAuraType(byte auraType) {
		this.auraType = auraType;
	}
	
	public byte getAuraType() {
		return auraType;
	}
}
