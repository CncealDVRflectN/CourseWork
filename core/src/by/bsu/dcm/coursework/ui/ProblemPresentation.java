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

import static by.bsu.dcm.coursework.graphics.Graphics.AntiAliasing;

public class ProblemPresentation extends Widget {
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

    public void setAntialiasing(AntiAliasing antialiasing) {
        graph.setAntialiasing(antialiasing);
    }

    public void generatePresentation(EquilibriumFluid fluid, RelaxationParams params, GraphPoints graphPoints) throws TargetBondException {
        lastPoints = graphPoints;
        result = EquilibriumFluid.calcRelaxation(fluid, params);
        lastPoints.points = result.points;

        graph.addGraph(lastPoints);
        resultGraph = graph.getGraph(Math.round(getWidth()), Math.round(getHeight()));
        graph.removeGraph(lastPoints);
    }

    public void resize(int width, int height) {
        if (result != null) {
            graph.addGraph(lastPoints);
            resultGraph = graph.getGraph(width, height);
            graph.removeGraph(lastPoints);
        }
    }
}
