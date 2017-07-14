package helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class InputHandler {

	private static boolean activeTouch = true;

	private static boolean somethingPressed = false;
	private static OrthographicCamera cam;
	private static Viewport viewport;

	public static boolean allowGameInputs = true;

	public static void setCam(OrthographicCamera cam) {
		InputHandler.cam = cam;
	}

	public static void setViewport(Viewport viewport) {
		InputHandler.viewport = viewport;
	}

	public static Viewport getViewport() {
		return viewport;
	}

	public static OrthographicCamera getCamera() {
		return cam;
	}

	public static boolean gameKeyJustPressed(int key) {
		return allowGameInputs && Gdx.input.isKeyJustPressed(key);
	}

	public static boolean gameKeyPressed(int key) {
		return allowGameInputs && Gdx.input.isKeyPressed(key);
	}

	public static int getUnprojectedX() {
		return (int) viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).x;
	}

	public static int getUnprojectedX(int i) {
		return (int) viewport.unproject(new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0)).x;
	}

	public static int getUnprojectedY() {
		return (int) viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)).y;
	}

	public static int getUnprojectedY(int i) {
		return (int) viewport.unproject(new Vector3(Gdx.input.getX(i), Gdx.input.getY(i), 0)).y;
	}

}
