package world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.socketserver.hebe.AtiwWatch;

import consts.MyColors;
import protocol.events.NewBullet;

public class ProjectTile extends NewBullet {

	private int width, height;

	public ProjectTile(NewBullet newBullet) {
		this.x = newBullet.x;
		this.y = newBullet.y;
		this.dX = newBullet.dX;
		this.dY = newBullet.dY;
		this.id = newBullet.id;
		this.speed = newBullet.speed;
		this.team = newBullet.team;
		this.width = 6;
		this.height = 6;
	}

	public void update(float delta) {
		this.x += dX * speed * delta;
		this.y += dY * speed * delta;
	}

	public void render(ShapeRenderer shape) {
		shape.setColor((this.team == AtiwWatch.team) ? MyColors.CYAN : MyColors.RED);
		shape.rect(this.x - this.width / 2, this.y - this.height / 2, this.width, this.height);
	}

}
