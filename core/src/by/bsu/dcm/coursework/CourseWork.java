package by.bsu.dcm.coursework;

import by.bsu.dcm.coursework.graphics.Graphics.AntiAliasing;
import by.bsu.dcm.coursework.graphs.Graph;
import by.bsu.dcm.coursework.graphs.GraphPoints;
import by.bsu.dcm.coursework.math.Axisymmetric;
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

    @Override
    public void create() {
        GraphPoints axisymmetricPoints;
        batch = new SpriteBatch();
        graph = new Graph();
        axisymmetric = new Axisymmetric();

        axisymmetric.setAlpha(Math.PI / 4.0);
        axisymmetric.setBond(0.0);
        axisymmetric.setEpsilon(0.00001);
        axisymmetric.setNodesNum(101);
        axisymmetric.calcResult();

        axisymmetricPoints = axisymmetric.getGraphPoints();
        axisymmetricPoints.pointSize = 2.0f;
        axisymmetricPoints.pointColor.set(1.0f, 0.0f, 0.0f, 0.75f);
        axisymmetricPoints.lineWidth = 2.0f;
        axisymmetricPoints.lineColor.set(1.0f, 0.0f, 0.0f, 0.75f);

        graph.addGraph(axisymmetric.getGraphPoints());

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
