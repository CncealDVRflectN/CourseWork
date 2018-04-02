package by.bsu.dcm.coursework;

import by.bsu.dcm.coursework.graphics.Graphics.AntiAliasing;
import by.bsu.dcm.coursework.graphs.Graph;
import by.bsu.dcm.coursework.graphs.GraphPoints;
import by.bsu.dcm.coursework.math.Axisymmetric;
import by.bsu.dcm.coursework.math.Flat;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CourseWork extends ApplicationAdapter {
    private SpriteBatch batch;
    private Graph graph;
    private TextureRegion graphResult;
    private Axisymmetric axisymmetric;
    private Flat flat;

    @Override
    public void create() {
        GraphPoints axisymmetricPoints;
        GraphPoints flatPoints;
        batch = new SpriteBatch();
        graph = new Graph();
        axisymmetric = new Axisymmetric(Math.PI / 4.0, 0.0, 0.00001, 100);
        flat = new Flat(Math.PI / 4.0, 0.0, 0.00001, 100);

        graph.setAntialiasing(AntiAliasing.SSAA4_FXAA);

        axisymmetric.calcResult();
        axisymmetricPoints = axisymmetric.getGraphPoints();
        axisymmetricPoints.pointSize = 2.0f;
        axisymmetricPoints.pointColor.set(1.0f, 0.0f, 0.0f, 0.75f);
        axisymmetricPoints.lineWidth = 2.0f;
        axisymmetricPoints.lineColor.set(1.0f, 0.0f, 0.0f, 0.75f);
        graph.addGraph(axisymmetricPoints);

        flat.calcResult();
        flatPoints = flat.getGraphPoints();
        flatPoints.pointSize = 2.0f;
        flatPoints.pointColor.set(0.0f, 0.0f, 1.0f, 0.75f);
        flatPoints.lineWidth = 2.0f;
        flatPoints.lineColor.set(0.0f, 0.0f, 1.0f, 0.75f);
        graph.addGraph(flatPoints);

        graphResult = graph.getGraph(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render() {
        Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(graphResult, 0.0f, 0.0f);
        batch.end();
    }

    @Override
    public void dispose() {
    }
}
