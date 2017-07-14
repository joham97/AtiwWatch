package world;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.socketserver.hebe.AtiwWatch;

import collision.MyLine;
import consts.MyColors;
import protocol.worlddata.ShieldData;

public class Shield extends ShieldData {

	public Shield(ShieldData shieldData) {
		if (shieldData != null) {
			this.x = shieldData.x;
			this.y = shieldData.y;
			this.width = shieldData.width;
			this.angle = shieldData.angle;
			this.team = shieldData.team;
			this.active = shieldData.active;
		}
	}

	public void render(ShapeRenderer shape) {
		if(active){
			shape.begin(ShapeType.Line);
			shape.setColor((this.team == AtiwWatch.team) ? MyColors.CYAN : MyColors.RED);
			MyLine myLine = new MyLine(calcX1(), calcY1(), calcX2(), calcY2());
			shape.rectLine(myLine.x1, myLine.y1, myLine.x2, myLine.y2, 10);
			shape.end();
		}
	}

	public void set(int x, int y, float angle) {
		this.x = x;
		this.y = y;
		this.angle = angle;
	}

	public ShieldData getShieldData() {
		ShieldData shieldData = new ShieldData();
		shieldData.x = this.x;
		shieldData.y = this.y;
		shieldData.width = this.width;
		shieldData.angle = this.angle;
		shieldData.team = this.team;
		shieldData.active = this.active;
		return shieldData;
	}
}
