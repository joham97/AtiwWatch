package hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.socketserver.hebe.AtiwWatch;

import consts.MyColors;
import protocol.worlddata.PointData;

public class InGameHud {

	public static byte pointTeam = 0;
	public static float capturePercent;
	public static float team1Percent;
	public static float team2Percent;
	public static float timeTillCapturable;
	public static boolean contested;
	public static int guysOnPoint;
	public static byte winnerTeam;

	public static int serverMS;
	public static int serverUPS;

	public static boolean gotMessage;

	public static int fps;

	public static int health;
	public static int maxHealth = 200;

	public static boolean stunned;
	public static boolean showStunned = true;

	private static float cooldown;
	public static float cooldownMax;
	public static int charges = 0;
	public static String ability = "";
	public static String key = "";

	public static String brain = "";

	public static void render(SpriteBatch batch, BitmapFont font, ShapeRenderer shape) {
		if (winnerTeam == 0) {
			shape.begin(ShapeType.Filled);
			shape.setColor(MyColors.GRAY);
			shape.rect(250, 728, 250, 7);
			if (capturePercent < 0) {
				shape.setColor(MyColors.CYAN);
				shape.rect(250 + 125 * (1 + capturePercent), 728, 125 * (-capturePercent), 7);
			} else if (capturePercent > 0) {
				shape.setColor(MyColors.RED);
				shape.rect(375, 728, 125 * capturePercent, 7);
			}

			shape.setColor(MyColors.CYAN);
			shape.rect(210, 710, 45, 25);
			shape.setColor(MyColors.RED);
			shape.rect(500, 710, 45, 25);

			shape.setColor(MyColors.GRAY);
			shape.rect(10, 10, 150, 20);
			shape.setColor(MyColors.BLUE);
			shape.rect(10, 10, 150 * ((float) health / (float) maxHealth), 20);

			shape.setColor(MyColors.GRAY);
			shape.rect(10, 40, 50, 50);
			shape.setColor(MyColors.BLUE);
			shape.rect(10, 40, 50, 50 * (1 - (cooldown / cooldownMax)));

			shape.end();

			shape.begin(ShapeType.Line);
			if (gotMessage) {
				shape.setColor(MyColors.RED);
				shape.polygon(new float[] { 700, 715, 700, 740, 720, 730, 740, 740, 700, 740, 740, 740, 740, 715});
			}
			shape.end();

			batch.begin();
			font.setColor(MyColors.WHITE);
			font.getData().scaleX = 1;
			font.getData().scaleY = 1;
			if (team1Percent < 0.1f) {
				font.draw(batch, (int) (team1Percent * 100) + "%", 227, 728);
			} else if (team1Percent < 1f) {
				font.draw(batch, (int) (team1Percent * 100) + "%", 221, 728);
			} else {
				font.draw(batch, (int) (team1Percent * 100) + "%", 215, 728);
			}
			if (team2Percent < 0.1f) {
				font.draw(batch, (int) (team2Percent * 100) + "%", 517, 728);
			} else if (team2Percent < 1f) {
				font.draw(batch, (int) (team2Percent * 100) + "%", 511, 728);
			} else {
				font.draw(batch, (int) (team2Percent * 100) + "%", 505, 728);
			}
			if (timeTillCapturable > 0) {
				font.draw(batch, "Point activates in " + ((int) timeTillCapturable + 1) + " sec", 300, 700);
			}

			font.draw(batch, health + "/" + maxHealth, 10, 25);
			font.draw(batch, serverMS + "ms", 0, 750);
			font.draw(batch, serverUPS + "ups", 0, 730);
			font.draw(batch, fps + "fps", 0, 710);

			font.draw(batch, brain, 100, 750);
			
			font.draw(batch, "" + (int) (cooldown + 0.9f), 31, 70);
			font.draw(batch, ability, 10, 105);
			font.draw(batch, key, 10, 120);
			if (charges > 0) {
				font.draw(batch, charges + "", 12, 54);
			}
			if (stunned && showStunned) {
				font.setColor(Color.YELLOW);
				font.getData().scaleX = 5;
				font.getData().scaleY = 5;
				font.draw(batch, "Stunned", 250, 650);
				font.setColor(Color.WHITE);
				font.getData().scaleX = 1;
				font.getData().scaleY = 1;
			}
			if(gotMessage){
				font.setColor(MyColors.RED);
				font.draw(batch, "F9", 711, 728);
			}
			batch.end();
		} else {
			if (winnerTeam == AtiwWatch.team) {
				batch.begin();
				font.setColor(MyColors.YELLOW);
				font.getData().scaleX = 5;
				font.getData().scaleY = 5;
				font.draw(batch, "Victory", 535, 650);
				font.getData().scaleX = 1;
				font.getData().scaleY = 1;
				batch.end();
			} else {
				batch.begin();
				font.setColor(MyColors.RED);
				font.getData().scaleX = 5;
				font.getData().scaleY = 5;
				font.draw(batch, "Defeat", 275, 650);
				font.getData().scaleX = 1;
				font.getData().scaleY = 1;
				batch.end();
			}
		}
	}

	public static void setPointData(PointData pointData) {
		pointTeam = pointData.pointTeam;
		if (AtiwWatch.team == 1) {
			capturePercent = pointData.capturePercent;
			team1Percent = pointData.team1Percent;
			team2Percent = pointData.team2Percent;
		} else if (AtiwWatch.team == 2) {
			capturePercent = -pointData.capturePercent;
			team1Percent = pointData.team2Percent;
			team2Percent = pointData.team1Percent;
		}
		contested = pointData.contested;
		guysOnPoint = pointData.guysOnPoint;
		timeTillCapturable = pointData.timeTillCapturable;
		winnerTeam = pointData.winnerTeam;
	}

	public static void setCooldown(float sprintCooldown) {
		cooldown = Math.max(sprintCooldown, 0);
	}

	public static void reset() {
		stunned = false;
		cooldown = 0;
		charges = -1;
	}
}
