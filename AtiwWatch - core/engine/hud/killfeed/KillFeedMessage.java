package hud.killfeed;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class KillFeedMessage {

	private float messageLasts;
	private String message;
	private GlyphLayout layout;
	private int width, height;
	
	public KillFeedMessage(String message, BitmapFont font) {
		this.message = message;
		this.messageLasts = 3;
		this.layout = new GlyphLayout();
		this.layout.setText(font, this.message);
		this.width = (int) this.layout.width;
		this.height = (int) this.layout.height;
	}
	
	public void update(float delta){
		this.messageLasts -= delta;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public boolean toDispose(){
		return this.messageLasts < 0;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
}
