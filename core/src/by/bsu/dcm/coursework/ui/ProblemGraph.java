package by.bsu.dcm.coursework.ui;

import by.bsu.dcm.coursework.graphics.Graphics.AntiAliasing;
import by.bsu.dcm.coursework.graphs.Graph;
import by.bsu.dcm.coursework.math.Axisymmetric;
import by.bsu.dcm.coursework.math.EquilibriumFluid;
import by.bsu.dcm.coursework.math.Flat;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ProblemGraph extends Actor {
    private Graph graph;
    private EquilibriumFluid axisymmetric;
    private EquilibriumFluid flat;

    private TextureRegion graphResult;

    public ProblemGraph(Axisymmetric axisymmetric, Flat flat) {
        this.axisymmetric = axisymmetric;
        this.flat = flat;

        graph = new Graph();

        graph.addGraph(axisymmetric.getGraphPoints());
        graph.addGraph(flat.getGraphPoints());

        axisymmetric.getGraphPoints().pointSize = 2.0f;
        axisymmetric.getGraphPoints().pointColor.set(1.0f, 0.0f, 0.0f, 0.75f);
        axisymmetric.getGraphPoints().lineWidth = 2.0f;
        axisymmetric.getGraphPoints().lineColor.set(1.0f, 0.0f, 0.0f, 0.75f);

        flat.getGraphPoints().pointSize = 2.0f;
        flat.getGraphPoints().pointColor.set(0.0f, 0.0f, 1.0f, 0.75f);
        flat.getGraphPoints().lineWidth = 2.0f;
        flat.getGraphPoints().lineColor.set(0.0f, 0.0f, 1.0f, 0.75f);
    }

    public void generateGraph(double alpha, double bond, double epsilon, int splitNum) {
        axisymmetric.setParams(alpha, bond, epsilon, splitNum);
        flat.setParams(alpha, bond, epsilon, splitNum);

        axisymmetric.calcResult();
        flat.calcResult();

        graphResult = graph.getGraph(Math.round(getWidth()), Math.round(getHeight()));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (graphResult != null) {
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            batch.draw(graphResult, getX(), getY());
        }
    }

    public void setAntialiasing(AntiAliasing antialiasing) {
        graph.setAntialiasing(antialiasing);
    }

    public void setVisible(boolean axisymmetric, boolean flat) {
        graph.clear();
        if (axisymmetric) {
            graph.addGraph(this.axisymmetric.getGraphPoints());
        }
        if (flat) {
            graph.addGraph(this.flat.getGraphPoints());
        }
    }

    public void resize(int width, int height) {
        if (graphResult != null) {
            graphResult = graph.getGraph(width, height);
        }
    }
}
