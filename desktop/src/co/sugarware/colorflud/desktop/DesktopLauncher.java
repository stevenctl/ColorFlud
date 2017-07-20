package co.sugarware.colorflud.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import co.sugarware.colorflud.GdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 2 *  720 / 3;
		config.height = 2 * 1280 / 3;
		new LwjglApplication(new GdxGame(), config);
	}
}
