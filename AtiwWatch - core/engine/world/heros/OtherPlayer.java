package world.heros;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import consts.Hero;
import consts.MyColors;
import protocol.worlddata.PlayerData;
import world.Player;
import world.heros.specialattack.Aura;

public class OtherPlayer extends Player {

	private Aura aura;
	private boolean marked;
	
	public OtherPlayer(PlayerData player) {
		super(null);
		if(player != null){
			this.id = player.id;
			this.x = player.x;
			this.y = player.y;
			if(player.name!=null){
				this.name = player.name;
			}
			this.width = player.width;
			this.height = player.height;
			this.team = player.team;
			this.shotFreq = 0.2f;
			this.heroType = player.heroType;
			this.health = player.health;
			this.maxHealth = player.maxHealth;
			this.setKills(player.kills);
			this.setDeaths(player.deaths);
			if(heroType == Hero.MEDIC){
				aura = new Aura();
				aura.setPlayer(this);
				aura.setAuraType(player.extra);
			}
		}
	}

	@Override
	public void renderFirst(ShapeRenderer shape){
		if(aura!=null){
			aura.render(shape);
		}
	}
	
	@Override
	public void render(ShapeRenderer shape) {
		super.render(shape);
		shape.begin(ShapeType.Filled);
		if(marked){
			marked = false;
			shape.setColor(MyColors.YELLOW);
			shape.rect(this.x - this.width / 2 + 5, this.y - 5, this.width-10, 10);
			shape.rect(this.x - 5, this.y - this.height / 2+5 , 10, this.height-10);
		}
		shape.end();
	}
	
	public Aura getAura() {
		return aura;
	}

	public void mark() {
		marked = true;
	}	
}
