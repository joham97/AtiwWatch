package world.heros.specialattack;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.socketserver.hebe.AtiwWatch;

import collision.MyVector;
import consts.Hero;
import consts.MyColors;
import protocol.SpecialAttack;

public class Hook extends HeroSpecialAttack {

	private float hook_speed;
	private MyVector vec1, vec2, unitDir;
	private int hookWidth = 15;
	private float startX, startY;

	private boolean reverse = false;

	public Hook(SpecialAttack specialAttack) {
		super(specialAttack);
		this.type = SpecialAttack.HOOK;
		this.hook_speed = Hero.GRABBER_HOOK_SPEED;
		this.unitDir = new MyVector(this.x2, this.y2).unit();
		this.x2 = this.unitDir.x;
		this.y2 = this.unitDir.y;
		this.startX = x1;
		this.startY = y1;
		this.vec1 = this.unitDir.orth().scl(this.hookWidth);
		this.vec2 = new MyVector(-this.vec1.x, -this.vec1.y);
	}

	@Override
	public void update(float delta) {
		if(!this.reverse){
			this.x1 += this.hook_speed * delta * this.x2;
			this.y1 += this.hook_speed * delta * this.y2;
		}else{
			this.x1 -= this.hook_speed * delta * this.x2;
			this.y1 -= this.hook_speed * delta * this.y2;
			if(new MyVector(this.x1-startX, this.y1-startY).len() < Hero.GRABBER_HOOK_SHORT_RANGE){
				this.toDispose = true;
				this.player.stun(-1);
			}
		}
	}

	@Override
	public void render(ShapeRenderer shape) {
		if (this.player != null) {
			shape.setColor((AtiwWatch.team == this.team) ? MyColors.CYAN : MyColors.RED);

			shape.begin(ShapeType.Filled);

			shape.rectLine(this.player.x, this.player.y, this.x1, this.y1, 4);
			shape.rectLine(this.x1, this.y1, this.x1 + this.vec1.x, this.y1 + this.vec1.y, 4);
			shape.rectLine(this.x1, this.y1, this.x1 + this.vec2.x, this.y1 + this.vec2.y, 4);
			shape.rectLine(this.x1 + this.vec1.x, this.y1 + this.vec1.y, this.x1 + this.vec1.x + this.unitDir.x * this.hookWidth, this.y1 + this.vec1.y + this.unitDir.y * this.hookWidth, 4);
			shape.rectLine(this.x1 + this.vec2.x, this.y1 + this.vec2.y, this.x1 + this.vec2.x + this.unitDir.x * this.hookWidth, this.y1 + this.vec2.y + this.unitDir.y * this.hookWidth, 4);

			shape.end();
		}
	}

	public void reverse() {
		this.reverse = true;
	}
}
