package hud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.socketserver.hebe.AtiwWatch;

import consts.Hero;
import consts.MyColors;
import world.Player;
import world.World;

public class Scoreboard {

	private World world;
	private List<Player> players;

	public Scoreboard(World world) {
		this.world = world;
		this.players = new ArrayList<>();
	}

	public void render(ShapeRenderer shape, SpriteBatch batch, BitmapFont font) {
		this.players.clear();
		this.players.add(this.world.getMe());
		this.players.addAll(this.world.getOtherPlayers());
		this.players.sort(new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				return Float.compare(o2.getKD(), o1.getKD());
			}
		});

		shape.begin(ShapeType.Filled);

		shape.setColor(MyColors.BLACK);
		shape.rect(100, 100, 550, 550);

		shape.setColor(MyColors.GRAY);
		shape.rect(110, 603, 530, 20);
		int i = 1;
		for (Player player : this.players) {
			shape.setColor((player.getTeam() == AtiwWatch.team) ? MyColors.CYAN : MyColors.RED);
			shape.rect(110, 603 - 20 * i, 530, 20);
			i++;

		}

		shape.end();

		batch.begin();

		font.setColor(MyColors.WHITE);
		font.draw(batch, "Name (Hero)", 120, 618);
		font.draw(batch, "Kills", 270, 618);
		font.draw(batch, "Deaths", 330, 618);
		font.draw(batch, "K/D", 390, 618);
		i = 1;
		for (Player player : this.players) {
			font.draw(batch, player.name + " (" + Hero.getHeroName(player.heroType) + ")", 120, 618 - 20 * i);
			font.draw(batch, "" + player.getKills(), 270, 618 - 20 * i);
			font.draw(batch, "" + player.getDeaths(), 330, 618 - 20 * i);
			font.draw(batch, ("" + player.getKD()).substring(0, 3), 390, 618 - 20 * i);
			i++;
		}

		batch.end();
	}

}
