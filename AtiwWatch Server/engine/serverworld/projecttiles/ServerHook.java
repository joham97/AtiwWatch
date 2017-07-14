package serverworld.projecttiles;

import collision.MyVector;
import consts.Hero;
import protocol.SpecialAttack;
import serverworld.ServerPlayer;
import serverworld.ServerProjectTile;
import serverworld.ServerWorld;

public class ServerHook extends ServerProjectTile {

	private ServerPlayer hookedTo;
	private float startX, startY;
	private boolean stunAfterHook;
	private float stunAfterHookTime;

	public ServerHook(SpecialAttack specialAttack) {
		super(specialAttack.x1, specialAttack.y1, specialAttack.x2, specialAttack.y2, Hero.GRABBER_HOOK_SPEED,
				specialAttack.team, 0, Hero.GRABBER_HOOK_RANGE, null, specialAttack.player_id);
		this.startX = specialAttack.x1;
		this.startY = specialAttack.y1;
		specialAttack.id = this.id;
		this.type = SpecialAttack.HOOK;

		this.stunAfterHook = false;
		this.stunAfterHookTime = Hero.GRABBER_HOOK_STUN;
	}

	@Override
	public void update(float delta, ServerWorld world) {
		if (this.hookedTo == null) {
			super.update(delta, world);
		} else if (!this.stunAfterHook) {
			this.x -= this.speed * delta * this.dX;
			this.y -= this.speed * delta * this.dY;
			if (new MyVector(this.startX - this.hookedTo.x, this.startY - this.hookedTo.y)
					.len() < Hero.GRABBER_HOOK_SHORT_RANGE) {
				this.x = this.startX + this.dX * Hero.GRABBER_HOOK_SHORT_RANGE;
				this.y = this.startY + this.dY * Hero.GRABBER_HOOK_SHORT_RANGE;
				this.stunAfterHook = true;
			}
			world.setPlayerToPos(this.hookedTo.id, this.x, this.y);
		} else {
			this.stunAfterHookTime -= delta;
			if (this.stunAfterHookTime <= 0) {
				world.stun(this.hookedTo.id, false);
				this.toDispose = true;
			}
		}
	}

	@Override
	protected void hitSomeone(ServerPlayer player, ServerWorld world) {
		this.hookedTo = player;
		world.stun(player.id, true);
		world.setPlayerToPos(player.id, this.x, this.y);
		world.killBullet(this.id);
	}

	public void setHookedTo(ServerPlayer hookedTo) {
		this.hookedTo = hookedTo;
	}

	public ServerPlayer getHookedTo() {
		return this.hookedTo;
	}
}
