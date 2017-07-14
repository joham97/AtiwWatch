package world.heros.specialattack;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.socketserver.hebe.AtiwWatch;

import collision.MyVector;
import consts.Hero;
import consts.MyColors;
import protocol.SpecialAttack;

public class Dash extends HeroSpecialAttack {

	private float dash_showTimeLeft;
	private float dash_showTime;

	public Dash(SpecialAttack specialAttack) {
		super(specialAttack);
		this.type = SpecialAttack.DASH;
		this.dash_showTime = new MyVector(this.x2 - this.x1, this.y2 - this.y1).len() / Hero.ASSASSIN_DASH_SPEED;
		this.dash_showTime *= 2f;
		this.dash_showTimeLeft = this.dash_showTime;
	}

	@Override
	public void update(float delta) {
		this.dash_showTimeLeft -= delta;
	}

	@Override
	public void render(ShapeRenderer shape) {
		if (this.player != null) {
			shape.setColor((AtiwWatch.team == this.team) ? MyColors.CYAN : MyColors.RED);

			shape.begin(ShapeType.Filled);
			if (this.dash_showTimeLeft > this.dash_showTime / 2f) {
				shape.rectLine(this.x1, this.y1, this.player.x, this.player.y, 4);
			} else {
				float p = 1 - (this.dash_showTimeLeft / (this.dash_showTime / 2f));
				shape.rectLine(this.x1 + p * (this.x2 - this.x1), this.y1 + p * (this.y2 - this.y1), this.x2, this.y2, 4);
			}
			shape.end();
		}
	}

	@Override
	public boolean toDispose() {
		return this.dash_showTimeLeft <= 0;
	}
}
