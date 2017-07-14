package world.heros;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.socketserver.hebe.AtiwWatch;

import consts.Hero;
import helper.FinallySmartVectors;
import helper.InputHandler;
import hud.InGameHud;
import world.Player;
import world.World;

public class Soldier extends Player {

	private final float aimBotTime;
	private float aimBotTimeLeft;
	private OtherPlayer nearstPlayer;

	public Soldier(World world) {
		super(world);
		this.width = Hero.SOLDIER_WIDTH;
		this.height = Hero.SOLDIER_HEIGHT;

		this.speed = Hero.SOLDIER_SPEED;

		this.shotFreq = Hero.SOLDIER_SHOT_FREQ;
		this.shotDmg = Hero.SOLDIER_SHOT_DMG;

		this.maxHealth = Hero.SOLDIER_MAX_HEALTH;
		this.health = this.maxHealth;

		this.dmg = Hero.SOLDIER_SHOT_DMG;
		this.shotSpeed = Hero.SOLDIER_SHOT_SPEED;

		this.cooldownMax = Hero.SOLDIER_AIM_BOT_COOLDOWN;

		aimBotTime = Hero.SOLDIER_AIM_BOT_TIME;

		InGameHud.cooldownMax = this.cooldownMax;
		InGameHud.setCooldown(0);

		InGameHud.ability = "Aim Bot";
		InGameHud.key = "Shift/Mouse Mid";

		this.heroType = Hero.SOLDIER;
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		this.cooldown -= delta;
		this.aimBotTimeLeft -= delta;
		InGameHud.setCooldown(this.cooldown);

		if(aimBotTimeLeft>0){
			int x = InputHandler.getUnprojectedX();
			int y = InputHandler.getUnprojectedY();
			nearstPlayer = null;
			for (OtherPlayer player : world.getOtherPlayers()) {
				if (player.getTeam() != AtiwWatch.team) {
					if (nearstPlayer == null) {
						nearstPlayer = player;
					} else {
						if (new FinallySmartVectors(x, y).sub(nearstPlayer.x, nearstPlayer.y)
								.len() > new FinallySmartVectors(x, y).sub(player.x, player.y).len()) {
							nearstPlayer = player;
						}
					}
				}
			}
			if (nearstPlayer != null) {
				nearstPlayer.mark();
			}
		}
		
		if (!this.stopInputs() && Gdx.input.isButtonPressed(Buttons.LEFT) && canShoot()) {
			shoot();
		}
	}

	@Override
	protected void shoot() {
		if (aimBotTimeLeft <= 0) {
			super.shoot();
		} else {
			shoot(nearstPlayer.x, nearstPlayer.y);
		}
	}

	@Override
	public void updateMovement(float delta) {
		if (!this.stopInputs() && cooldown < 0
				&& (InputHandler.gameKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isButtonPressed(Buttons.MIDDLE))) {
			this.cooldown = cooldownMax;
			aimBotTimeLeft = aimBotTime;
		}
		super.updateMovement(delta);
	}

	@Override
	public boolean canShoot() {
		if (this.lastShot > this.shotFreq || (aimBotTimeLeft <= 0 && this.lastShot > this.shotFreq)) {
			this.lastShot = 0;
			return true;
		}
		return false;
	}

}
