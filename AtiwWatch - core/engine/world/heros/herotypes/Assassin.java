package world.heros.herotypes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.socketserver.hebe.AtiwWatch;

import collision.MyLine;
import collision.MyVector;
import consts.Hero;
import helper.InputHandler;
import hud.InGameHud;
import protocol.SpecialAttack;
import serverworld.ServerTerrain;
import world.World;
import world.heros.Bot;
import world.heros.specialattack.Dash;

public class Assassin extends Bot {

	private int dashCharges;

	private float dashFromX;
	private float dashFromY;
	private float dashToX;
	private float dashToY;
	private boolean dashActive;
	private float timeToDash;
	private float timeDashing;

	private int shotsLeft;
	private float shotsTime;

	public Assassin(World world) {
		super(world);

		this.width = Hero.ASSASSIN_WIDTH;
		this.height = Hero.ASSASSIN_HEIGHT;

		this.speed = Hero.ASSASSIN_SPEED;

		this.shotFreq = Hero.ASSASSIN_SHOT_FREQ;
		this.shotDmg = Hero.ASSASSIN_SHOT_DMG;

		this.maxHealth = Hero.ASSASSIN_MAX_HEALTH;
		this.health = this.maxHealth;

		this.dmg = Hero.ASSASSIN_SHOT_DMG;
		this.shotSpeed = Hero.ASSASSIN_SHOT_SPEED;

		this.cooldownMax = Hero.ASSASSIN_DASH_COOLDOWN;
		this.cooldown = 0;
		this.dashCharges = Hero.ASSASSIN_DASH_CHARGES;
		InGameHud.charges = this.dashCharges;

		InGameHud.cooldownMax = this.cooldownMax;
		InGameHud.setCooldown(this.cooldown);

		InGameHud.ability = "Dash";
		InGameHud.key = "Shift/Mouse Mid";

		this.heroType = Hero.ASSASSIN;
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		updateDash(delta);

		if (!this.stopInputs() && Gdx.input.isButtonPressed(Buttons.LEFT) && canShoot()) {
			this.shotsLeft = Hero.ASSASSIN_SHOT_COUNT;
			this.shotsTime = 0;
		}
		this.shotsTime -= delta;
		if (this.shotsLeft > 0 && this.shotsTime <= 0) {
			this.shotsLeft--;
			this.shotsTime = Hero.ASSASSIN_SHOT_LENGTH;
			shoot();
		}
	}

	private void updateDash(float delta) {
		this.cooldown -= delta;
		InGameHud.setCooldown(this.cooldown);
		if (this.cooldown <= 0) {
			if (this.dashCharges == 0) {
				this.dashCharges++;
				this.cooldown = this.cooldownMax;
			} else if (this.dashCharges == 1) {
				this.dashCharges++;
			}
		}
		if (canDoSpecialAttack() && (InputHandler.gameKeyJustPressed(Keys.SHIFT_LEFT)
				|| (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Buttons.MIDDLE)))) {
			dash(InputHandler.getUnprojectedX(), InputHandler.getUnprojectedY(), delta);
		}

		if (this.dashActive) {
			this.timeDashing += delta;
			if (this.timeDashing >= this.timeToDash) {
				this.dashActive = false;
				this.stopInputs = false;
				this.x = this.dashToX;
				this.y = this.dashToY;
			} else {
				this.x = this.dashFromX + (this.timeDashing / this.timeToDash) * (this.dashToX - this.dashFromX);
				this.y = this.dashFromY + (this.timeDashing / this.timeToDash) * (this.dashToY - this.dashFromY);
			}
		}
		InGameHud.charges = this.dashCharges;
	}

	protected void dash(float dashToX, float dashToY, float delta){
		this.stopInputs = true;
		this.dashCharges--;
		if (this.dashCharges == 1) {
			this.cooldown = this.cooldownMax;
		}

		this.dashFromX = this.x;
		this.dashFromY = this.y;
		this.dashToX = dashToX;
		this.dashToY = dashToY;

		float dX = (this.dashToX - this.dashFromX) / 20f;
		float dY = (this.dashToY - this.dashFromY) / 20f;

		this.dashToX = this.dashFromX;
		this.dashToY = this.dashFromY;

		for (int i = 0; i < 20; i++) {
			if (this.world.getWalls().doesCollides(new MyLine(this.dashToX, this.dashToY, this.dashToX + dX, this.dashToY + dY))) {
				dX = 0;
				dY = 0;
			}
			if (this.world.getTerrain().get((int) ((this.dashToX + dX) / 50),
					(int) ((this.dashToY + dY) / 50)) == ServerTerrain.WALL) {
				dX = 0;
				dY = 0;
			}

			this.dashToX += dX;
			this.dashToY += dY;
		}

		this.timeToDash = new MyVector(this.dashToX - this.x, this.dashToY - this.y).len() / Hero.ASSASSIN_DASH_SPEED;
		this.timeDashing = delta;

		SpecialAttack specialAttack = new SpecialAttack();
		specialAttack.type = SpecialAttack.DASH;
		specialAttack.x1 = (int) this.dashFromX;
		specialAttack.y1 = (int) this.dashFromY;
		specialAttack.x2 = (int) this.dashToX;
		specialAttack.y2 = (int) this.dashToY;
		specialAttack.team = AtiwWatch.team;
		this.world.sendSpecialAttack(new Dash(specialAttack));

		this.dashActive = true;
	}
	
	@Override
	public boolean canDoSpecialAttack() {
		return !this.stopInputs() && this.dashCharges > 0 && !this.dashActive;
	}
	
	@Override
	public void resetCooldown() {
		super.resetCooldown();
		this.dashCharges = Hero.ASSASSIN_DASH_CHARGES;
	}

}
