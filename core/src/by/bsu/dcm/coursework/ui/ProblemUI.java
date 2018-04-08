package by.bsu.dcm.coursework.ui;

import by.bsu.dcm.coursework.AssetsManager;
import by.bsu.dcm.coursework.math.Axisymmetric;
import by.bsu.dcm.coursework.math.Flat;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ProblemUI extends Table {
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 720;
    private static final int DEFAULT_FONT_SIZE = 15;
    private static final float GRAPH_WEIGHT = 7.75f;
    private static final float SETTINGS_WEIGHT = 2.25f;

    private ProblemGraph problemGraph;
    private ProblemSettings problemSettings;
    private FreeTypeFontParameter fontParam;

    public ProblemUI(Axisymmetric axisymmetric, Flat flat) {
        super();

        fontParam = new FreeTypeFontParameter();
        fontParam.size = DEFAULT_FONT_SIZE;

        problemGraph = new ProblemGraph(axisymmetric, flat);
        problemSettings = new ProblemSettings(problemGraph, AssetsManager.getSkinUI(fontParam));

        add(problemGraph).fill();
        add(problemSettings).fill();
    }

    public void resize(int width, int height) {
        setSize(width, height);
        getCell(problemGraph).width(width * (GRAPH_WEIGHT / (GRAPH_WEIGHT + SETTINGS_WEIGHT))).height(height);
        getCell(problemSettings).width(width * (SETTINGS_WEIGHT / (GRAPH_WEIGHT + SETTINGS_WEIGHT))).height(height);

        fontParam.size = Math.round(DEFAULT_FONT_SIZE * ((float) height / (float) DEFAULT_HEIGHT));
        problemSettings.setSkin(AssetsManager.getSkinUI(fontParam));
    }
}
