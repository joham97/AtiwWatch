package world.heros;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.socketserver.hebe.AtiwWatch;

import collision.MyVector;
import consts.Hero;
import helper.InputHandler;
import hud.InGameHud;
import world.Player;
import world.Shield;
import world.World;

public class Tank extends Player {

	private Shield shield;
	private int shield_distance;

	private int shatter_count;
	private int shatter_range;

	protected float shieldX, shieldY;

	public Tank(World world) {
		super(world);
		this.width = Hero.TANK_WIDTH;
		this.height = Hero.TANK_HEIGHT;

		this.speed = Hero.TANK_SPEED;

		this.shotFreq = Hero.TANK_SHOT_FREQ;
		this.shotSpeed = Hero.TANK_SHATTER_SPEED;

		this.shield = new Shield(null);
		this.shield.width = Hero.TANK_SHIELD_WIDTH;
		this.shield.team = AtiwWatch.team;
		this.shield_distance = Hero.TANK_SHIELD_DISTANCE;

		this.maxHealth = Hero.TANK_MAX_HEALTH;
		this.health = this.maxHealth;

		this.dmg = Hero.TANK_SHATTER_DMG;
		this.shatter_count = Hero.TANK_SHATTER_COUNT;

		this.shatter_range = Hero.TANK_SHATTER_RANGE;
		this.cooldownMax = Hero.TANK_SHATTER_COOLDOWN;

		InGameHud.cooldownMax = this.cooldown;
		InGameHud.setCooldown(0);

		InGameHud.ability = "Melee";
		InGameHud.key = "Shift/Mouse Mid";

		this.heroType = Hero.TANK;
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		this.cooldown -= delta;
		InGameHud.setCooldown(this.cooldown);
		
		updateShield();
		MyVector vec = new MyVector(shieldX - this.x, shieldY - this.y)
				.unit();
		this.shield.set((int) (this.x + vec.scl(this.shield_distance).x),
				(int) (this.y + vec.scl(this.shield_distance).y), (float) (Math.atan2(vec.y, vec.x) * 180d / Math.PI));

		
		if (this.cooldown <= this.cooldownMax - Hero.TANK_SHATTER_SHIELD_AWAY) {
			this.shield.active = true;
		}
		if (canDoSpecialAttack() && InputHandler.gameKeyJustPressed(Keys.SHIFT_LEFT)
				|| (Gdx.input.isButtonPressed(Buttons.MIDDLE) && Gdx.input.justTouched())) {
			shoot();
		}
	}
	
	protected void updateShield(){
		this.shieldX = InputHandler.getUnprojectedX();
		this.shieldY = InputHandler.getUnprojectedY();
	}
	
	@Override
	protected void shoot() {
		this.cooldown = this.cooldownMax;
		for (int i = 0; i < shatter_count; i++) {
			this.world.newBullet((int) this.x, (int) this.y, (float) Math.cos(i / (float)shatter_count * (Math.PI * 2)),
					(float) Math.sin(i / (float)shatter_count * (Math.PI * 2)), this.dmg, this.shotSpeed, this.shatter_range);
			this.shield.active = false;
		}
	}

	public Shield getShield() {
		return this.shield;
	}

	@Override
	public void setTeam(byte team) {
		super.setTeam(team);
		this.shield.team = team;
	}

	@Override
	public boolean canShoot() {
		return false;
	}
}
