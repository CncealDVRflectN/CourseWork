package by.bsu.dcm.coursework.desktop;

import by.bsu.dcm.coursework.CourseWork;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String... args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Course Work";
		config.width = 1280;
		config.height = 720;
		config.backgroundFPS = 30;
		config.foregroundFPS = 60;
		config.vSyncEnabled = false;
		config.addIcon("icons/ic_launcher.png", Files.FileType.Internal);

        new LwjglApplication(new CourseWork(), config);
    }
}
