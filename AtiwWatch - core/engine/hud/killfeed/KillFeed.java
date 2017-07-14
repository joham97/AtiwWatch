package hud.killfeed;

import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import consts.MyColors;

public class KillFeed {

	public List<KillFeedMessage> messages;
	private BitmapFont font;
	
	public KillFeed() {
		this.messages = new LinkedList<>();
		this.font = new BitmapFont();
	}
	
	public void add(String message) {
		synchronized(this.messages){
			this.messages.add(0, new KillFeedMessage(message, this.font));
		}
	}
	
	public void update(final float delta) {
		synchronized(this.messages){
			for(int i = 0; i < this.messages.size(); i++){
				this.messages.get(i).update(delta);
				if(this.messages.get(i).toDispose()){
					this.messages.remove(i);
					i--;
				}
			}
		}
	}
	
	public void render(ShapeRenderer shape, SpriteBatch batch, BitmapFont font) {
		synchronized(this.messages){
			shape.begin(ShapeType.Filled);
			shape.setColor(MyColors.BLACK);
			for(int i = 0; i < this.messages.size(); i++){
				shape.rect(730-this.messages.get(i).getWidth()-20, 730-this.messages.get(i).getHeight()-20-50*i, this.messages.get(i).getWidth()+20, this.messages.get(i).getHeight()+20);
			}
			shape.end();
			
			batch.begin();
			for(int i = 0; i < this.messages.size(); i++){
				font.draw(batch, this.messages.get(i).getMessage(), 720-this.messages.get(i).getWidth(), 730-50*i-this.messages.get(i).getHeight());
			}
			batch.end();
		}
	}
	
}
