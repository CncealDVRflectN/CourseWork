package by.bsu.dcm.coursework.ui;

import by.bsu.dcm.coursework.graphs.Graph;
import by.bsu.dcm.coursework.graphs.GraphPoints;
import by.bsu.dcm.coursework.math.fluid.EquilibriumFluid;
import by.bsu.dcm.coursework.math.fluid.ProblemResult;
import by.bsu.dcm.coursework.math.fluid.RelaxationParams;
import by.bsu.dcm.coursework.math.fluid.TargetBondException;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;

import java.util.Locale;

import static by.bsu.dcm.coursework.graphics.Graphics.AntiAliasing;

public class ProblemPresentation extends Widget implements Disposable {
    private Graph graph;
    private TextureRegion resultGraph;
    private GraphPoints lastPoints;

    private ProblemResult result;

    public ProblemPresentation() {
        graph = new Graph();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (resultGraph != null) {
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            batch.draw(resultGraph, getX(), getY());
        }
    }

    public void generatePresentation(EquilibriumFluid fluid, RelaxationParams params, GraphPoints graphPoints) throws TargetBondException {
        lastPoints = graphPoints;
        result = EquilibriumFluid.calcRelaxation(fluid, params);
        lastPoints.points = result.points;
        lastPoints.desription = String.format(Locale.ENGLISH, "Bond number = %f", result.bond);

        if (resultGraph != null) {
            resultGraph.getTexture().dispose();
        }

        graph.addGraph(lastPoints);
        resultGraph = graph.getGraph(Math.round(getWidth()), Math.round(getHeight()));
        graph.removeGraph(lastPoints);
    }

    public void setAntialiasing(AntiAliasing antialiasing) {
        graph.setAntialiasing(antialiasing);
    }

    public void setEqualAxisScaleMarks(boolean equal) {
        graph.setEqualAxisScaleMarks(equal);
    }

    public boolean isEqualAxisScaleMarks() {
        return graph.isEqualAxisScaleMarks();
    }

    public void resize(int width, int height) {
        if (resultGraph != null) {
            resultGraph.getTexture().dispose();
        }
        if (result != null) {
            graph.addGraph(lastPoints);
            resultGraph = graph.getGraph(width, height);
            graph.removeGraph(lastPoints);
        }
    }

    @Override
    public void dispose() {
        graph.dispose();
        if (resultGraph != null) {
            resultGraph.getTexture().dispose();
        }
    }
}
