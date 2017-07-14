package world.heros.herotypes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import consts.Hero;
import hud.InGameHud;
import protocol.worlddata.PlayerData;
import world.World;
import world.heros.Bot;
import world.heros.specialattack.Aura;

public class Medic extends Bot {

	protected int shotsLeft;
	protected float shotsTime;

	protected Aura aura;

	public Medic(World world) {
		super(world);

		this.width = Hero.MEDIC_WIDTH;
		this.height = Hero.MEDIC_HEIGHT;

		this.speed = Hero.MEDIC_SPEED;

		this.shotFreq = Hero.MEDIC_SHOT_FREQ;
		this.shotDmg = Hero.MEDIC_SHOT_DMG;

		this.maxHealth = Hero.MEDIC_MAX_HEALTH;
		this.health = this.maxHealth;

		this.dmg = Hero.MEDIC_SHOT_DMG;
		this.shotSpeed = Hero.MEDIC_SHOT_SPEED;

		this.cooldownMax = Hero.MEDIC_AURA_COOLDOWN;
		this.cooldown = 0;

		InGameHud.cooldownMax = this.cooldownMax;
		InGameHud.setCooldown(this.cooldown);

		InGameHud.ability = "Aura Switch";
		InGameHud.key = "Shift/Mouse Mid";

		this.heroType = Hero.MEDIC;

		aura = new Aura();
		aura.setPlayer(this);
	}

	@Override
	public void update(float delta) {
		if(aura.getAuraType() == Aura.SPEED){
			addSpeed(Hero.MEDIC_AURA_SPEED_BOOST);
		}
		super.update(delta);
		cooldown -= delta;

		if (!this.stopInputs() && Gdx.input.isButtonPressed(Buttons.LEFT) && canShoot()) {
			startShooting();
		}
		this.shotsTime -= delta;
		if (this.shotsLeft > 0 && this.shotsTime <= 0) {
			this.shotsLeft--;
			this.shotsTime = Hero.MEDIC_SHOT_LENGTH;
			shoot();
		}
		if (isSpecialAttachTriggered()) {
			switchAura();
		}

		InGameHud.setCooldown(this.cooldown);
	}
	
	protected void switchAura(){
		this.cooldown = this.cooldownMax;
		aura.switchAura();
		world.sendSpecialAttack(aura);
	}
	
	protected void startShooting(){
		this.shotsLeft = Hero.MEDIC_SHOT_COUNT;
		this.shotsTime = 0;
	}

	@Override
	public PlayerData getPlayerData() {
		PlayerData data = super.getPlayerData();
		data.extra = aura.getAuraType();
		return data;
	}

	@Override
	public void renderFirst(ShapeRenderer shape) {
		aura.render(shape);
	}

	public Aura getAura() {
		return aura;
	}
}
