package by.bsu.dcm.coursework.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import by.bsu.dcm.coursework.CourseWork;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Corse Work";
		config.width = 1280;
		config.height = 720;
		config.backgroundFPS = 30;
		config.foregroundFPS = 60;
		config.vSyncEnabled = false;
		config.useGL30 = true;

		new LwjglApplication(new CourseWork(), config);
	}
}
