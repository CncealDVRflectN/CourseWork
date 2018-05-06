package by.bsu.dcm.coursework.ui;

import by.bsu.dcm.coursework.ResourceManager;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;

public class ProblemUI extends Table implements Disposable {
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 720;
    private static final int DEFAULT_FONT_SIZE = 15;
    private static final float PRESENTATION_WEIGHT = 0.75f;
    private static final float SETTINGS_WEIGHT = 0.25f;

    private ProblemPresentation presentation;
    private ProblemSettings problemSettings;
    private FreeTypeFontParameter fontParam;

    public ProblemUI() {
        fontParam = new FreeTypeFontParameter();
        fontParam.size = DEFAULT_FONT_SIZE;

        presentation = new ProblemPresentation(ResourceManager.getSkinUI(fontParam));
        problemSettings = new ProblemSettings(presentation, ResourceManager.getSkinUI(fontParam));

        add(presentation).fill();
        add(problemSettings).fill();
    }

    public void resize(int width, int height) {
        setSize(width, height);
        getCell(presentation).width(width * (PRESENTATION_WEIGHT / (PRESENTATION_WEIGHT + SETTINGS_WEIGHT))).height(height);
        getCell(problemSettings).width(width * (SETTINGS_WEIGHT / (PRESENTATION_WEIGHT + SETTINGS_WEIGHT))).height(height);

        fontParam.size = Math.round(DEFAULT_FONT_SIZE * ((float) height / (float) DEFAULT_HEIGHT));
        problemSettings.setSkin(ResourceManager.getSkinUI(fontParam));
        problemSettings.resize();
        presentation.setSkin(ResourceManager.getSkinUI(fontParam));
        presentation.resize();
    }

    @Override
    public void dispose() {
        presentation.dispose();
    }
}
