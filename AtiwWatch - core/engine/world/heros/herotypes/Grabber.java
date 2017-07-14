package world.heros.herotypes;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.socketserver.hebe.AtiwWatch;

import collision.MyVector;
import consts.Hero;
import helper.FinallySmartVectors;
import helper.InputHandler;
import hud.InGameHud;
import protocol.SpecialAttack;
import world.World;
import world.heros.Bot;
import world.heros.specialattack.Hook;

public class Grabber extends Bot {

	private Random random;

	public Grabber(World world) {
		super(world);
		this.width = Hero.GRABBER_WIDTH;
		this.height = Hero.GRABBER_HEIGHT;

		this.speed = Hero.GRABBER_SPEED;

		this.shotFreq = Hero.GRABBER_SHOT_FREQ;
		this.shotDmg = Hero.GRABBER_SHOT_DMG;

		this.maxHealth = Hero.GRABBER_MAX_HEALTH;
		this.health = this.maxHealth;

		this.dmg = Hero.GRABBER_SHOT_DMG;
		this.shotSpeed = Hero.GRABBER_SHOT_SPEED;

		this.cooldownMax = Hero.GRABBER_HOOK_COOLDOWN;

		InGameHud.cooldownMax = this.cooldownMax;
		InGameHud.setCooldown(0);

		InGameHud.ability = "Hook";
		InGameHud.key = "Shift/Mouse Mid";

		this.heroType = Hero.GRABBER;

		this.random = new Random();
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		this.cooldown -= delta;	
		InGameHud.setCooldown(this.cooldown);
	}
	
	@Override
	public void updateMovement(float delta){
		super.updateMovement(delta);
		if (!this.stopInputs() && Gdx.input.isButtonPressed(Buttons.LEFT) && canShoot()) {
			shoot();
		}
		if (isSpecialAttachTriggered()) {
			grab(InputHandler.getUnprojectedX(), InputHandler.getUnprojectedY());
		}
	}
	
	protected void grab(int targetX, int targetY){
		this.stun(1, false);
		this.cooldown = this.cooldownMax;
		SpecialAttack specialAttack = new SpecialAttack();
		specialAttack.type = SpecialAttack.HOOK;
		specialAttack.x1 = (int) this.x;
		specialAttack.y1 = (int) this.y;
		specialAttack.x2 = targetX - (int) this.x;
		specialAttack.y2 = targetY - (int) this.y;
		specialAttack.team = AtiwWatch.team;
		this.world.sendSpecialAttack(new Hook(specialAttack));
	}

	@Override
	protected void shoot(float shootX, float shootY) {
		FinallySmartVectors vector = new FinallySmartVectors(shootX, shootY).sub(this.x, this.y).unit();

		for (int i = 0; i < Hero.GRABBER_SHOT_COUNT; i++) {
			double angleOfBullet = vector.getAngle() + (this.random.nextFloat() * 2f - 1f) * (Math.PI * 2f * (Hero.GRABBER_SHOT_ANGLE / 2f / 360f));
			MyVector vectorOfBullet = new MyVector((float) Math.cos(angleOfBullet), (float) Math.sin(angleOfBullet)).unit();
			this.world.newBullet((int) this.x, (int) this.y, vectorOfBullet.x, vectorOfBullet.y, this.dmg, this.shotSpeed - 150 + this.random.nextInt(300), Hero.GRABBER_SHOT_RANGE);
		}
	}

	@Override
	public boolean canShoot() {
		if (this.lastShot > this.shotFreq) {
			this.lastShot = 0;
			return true;
		}
		return false;
	}

}
