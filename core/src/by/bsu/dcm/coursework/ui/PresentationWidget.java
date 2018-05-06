package by.bsu.dcm.coursework.ui;

import by.bsu.dcm.coursework.ResourceManager;
import by.bsu.dcm.coursework.graphs.Graph;
import by.bsu.dcm.coursework.graphs.GraphPoints;
import by.bsu.dcm.coursework.math.fluid.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class PresentationWidget extends Widget implements Disposable {
    public enum Slide {
        Axisymmetric, Plain, HeightCoef
    }

    private static final String AXISYMMETRIC_GRAPH_NAME = "Axisymmetric solution";
    private static final String PLAIN_GRAPH_NAME = "Plain solution";
    private static final String HEIGHT_COEF_GRAPH_NAME = "Height coeficients";
    private static final String AXISYM_X_AXIS_NAME = "r";
    private static final String AXISYM_Y_AXIS_NAME = "z";
    private static final String PLAIN_X_AXIS_NAME = "x";
    private static final String PLAIN_Y_AXIS_NAME = "y";
    private static final String HEIGHT_COEF_X_AXIS_NAME = "Bo";
    private static final String HEIGHT_COEF_Y_AXIS_NAME = "h";

    private Slide currentSlide;
    private TextButton generateButton;

    private EquilibriumFluid axisymmetricFluid;
    private EquilibriumFluid plainFluid;
    private RelaxationParams lastParams;

    private Graph graph;
    private ReentrantLock graphLock;
    private GraphHolder axisymmetricGraph;
    private GraphHolder plainGraph;
    private GraphHolder heightCoefGraph;

    private GraphPoints[] axisymmetricGraphParams;
    private GraphPoints[] plainGraphParams;
    private GraphPoints axisymCoefGraphParams;
    private GraphPoints plainCoefGraphParams;

    private float graphPointSize;
    private float graphLineWidth;
    private int graphsNum;

    private boolean equalAxisScaleMarks;
    private boolean volumeNondim;

    public PresentationWidget() {
        graph = new Graph();
        graphLock = new ReentrantLock();

        axisymmetricFluid = new Axisymmetric();
        plainFluid = new Plain();

        axisymmetricGraph = new GraphHolder();
        plainGraph = new GraphHolder();
        heightCoefGraph = new GraphHolder();

        graphPointSize = 2.5f;
        graphLineWidth = 2.0f;
        graphsNum = 5;
        equalAxisScaleMarks = false;
        volumeNondim = false;

        generateGraphsParams();
        setCurrentSlide(Slide.HeightCoef);
    }

    public void generatePresentation(RelaxationParams params) {
        EquilibriumRunnable axisymmetric = new EquilibriumRunnable();
        EquilibriumRunnable plain = new EquilibriumRunnable();
        HeightCoefRunnable heightCoefs = new HeightCoefRunnable();
        CyclicBarrier fluidsBarrier = new CyclicBarrier(2, heightCoefs);

        generateButton.setDisabled(true);

        params.resultsNum = graphsNum;
        params.volumeNondim = volumeNondim;

        heightCoefs.setGraphHolder(heightCoefGraph);
        heightCoefs.setName(HEIGHT_COEF_GRAPH_NAME);

        axisymmetric.setFluid(axisymmetricFluid);
        axisymmetric.setFluidGraphParams(axisymmetricGraphParams);
        axisymmetric.setGraphHolder(axisymmetricGraph);
        axisymmetric.setParams(params);
        axisymmetric.setProblemName(AXISYMMETRIC_GRAPH_NAME);
        axisymmetric.setAxisNames(AXISYM_X_AXIS_NAME, AXISYM_Y_AXIS_NAME);
        axisymmetric.setFluidsBarrier(fluidsBarrier);
        axisymmetric.setHeightCoefRunnable(heightCoefs);
        axisymmetric.setHeightGraphParam(axisymCoefGraphParams);

        plain.setFluid(plainFluid);
        plain.setFluidGraphParams(plainGraphParams);
        plain.setGraphHolder(plainGraph);
        plain.setParams(params);
        plain.setProblemName(PLAIN_GRAPH_NAME);
        plain.setAxisNames(PLAIN_X_AXIS_NAME, PLAIN_Y_AXIS_NAME);
        plain.setFluidsBarrier(fluidsBarrier);
        plain.setHeightCoefRunnable(heightCoefs);
        plain.setHeightGraphParam(plainCoefGraphParams);

        lastParams = params;

        ResourceManager.THREAD_POOL.execute(axisymmetric);
        ResourceManager.THREAD_POOL.execute(plain);
    }

    private void generateGraphsParams() {
        Color axisymColor = new Color(1.0f, 0.0f, 0.0f, 0.75f);
        Color plainColor = new Color(0.0f, 0.0f, 1.0f, 0.75f);
        float colorStep = (graphsNum == 1) ? 0.0f : 0.75f / (graphsNum - 1);

        axisymmetricGraphParams = new GraphPoints[graphsNum];
        plainGraphParams = new GraphPoints[graphsNum];
        axisymCoefGraphParams = new GraphPoints();
        plainCoefGraphParams = new GraphPoints();

        axisymCoefGraphParams = new GraphPoints();
        axisymCoefGraphParams.pointSize = graphPointSize;
        axisymCoefGraphParams.pointColor.set(axisymColor);
        axisymCoefGraphParams.lineWidth = graphLineWidth;
        axisymCoefGraphParams.lineColor.set(axisymColor);
        axisymCoefGraphParams.desription = "Axisymmetric height coeficient";

        plainCoefGraphParams = new GraphPoints();
        plainCoefGraphParams.pointSize = graphPointSize;
        plainCoefGraphParams.pointColor.set(plainColor);
        plainCoefGraphParams.lineWidth = graphLineWidth;
        plainCoefGraphParams.lineColor.set(plainColor);
        plainCoefGraphParams.desription = "Plain height coeficient";

        for (int i = 0; i < graphsNum; i++) {
            axisymmetricGraphParams[i] = new GraphPoints();
            axisymmetricGraphParams[i].pointSize = graphPointSize;
            axisymmetricGraphParams[i].pointColor.set(axisymColor);
            axisymmetricGraphParams[i].lineWidth = graphLineWidth;
            axisymmetricGraphParams[i].lineColor.set(axisymColor);

            plainGraphParams[i] = new GraphPoints();
            plainGraphParams[i].pointSize = graphPointSize;
            plainGraphParams[i].pointColor.set(plainColor);
            plainGraphParams[i].lineWidth = graphLineWidth;
            plainGraphParams[i].lineColor.set(plainColor);

            axisymColor.r -= colorStep;
            axisymColor.g += colorStep;
            plainColor.b -= colorStep;
            plainColor.g += colorStep;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        switch (currentSlide) {
            case Axisymmetric:
                if (!axisymmetricGraph.isEmpty()) {
                    batch.draw(axisymmetricGraph.getGraph(), getX(), getY(), getOriginX(), getOriginY(),
                            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
                }
                break;
            case Plain:
                if (!plainGraph.isEmpty()) {
                    batch.draw(plainGraph.getGraph(), getX(), getY(), getOriginX(), getOriginY(),
                            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
                }
                break;
            default:
                if (!heightCoefGraph.isEmpty()) {
                    batch.draw(heightCoefGraph.getGraph(), getX(), getY(), getOriginX(), getOriginY(),
                            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
                }
        }
    }

    public void setCurrentSlide(Slide slide) {
        currentSlide = slide;
    }

    public void setGraphsNum(int num) {
        graphsNum = num;
        generateGraphsParams();
    }

    public void setGenerateButton(TextButton button) {
        generateButton = button;
    }

    public void setEqualAxisScaleMarks(boolean equal) {
        equalAxisScaleMarks = equal;
    }

    public void setVolumeNondim(boolean nondim) {
        volumeNondim = nondim;
    }

    public boolean isEqualAxisScaleMarks() {
        return equalAxisScaleMarks;
    }

    public boolean isVolumeNondim() {
        return volumeNondim;
    }

    public Slide getCurrentSlide() {
        return currentSlide;
    }

    public void resize() {
        if (lastParams != null) {
            generatePresentation(lastParams);
        }
    }

    @Override
    public void dispose() {
        graph.dispose();
        axisymmetricGraph.dispose();
        plainGraph.dispose();
        heightCoefGraph.dispose();
    }

    private class GraphHolder implements Disposable {
        private TextureRegion graph;

        void setGraph(TextureRegion graph) {
            dispose();
            this.graph = graph;
        }

        TextureRegion getGraph() {
            return graph;
        }

        public boolean isEmpty() {
            return graph == null;
        }

        @Override
        public void dispose() {
            if (graph != null) {
                graph.getTexture().dispose();
            }
        }
    }

    private class EquilibriumRunnable implements Runnable {
        private String problemName;
        private String xAxisName;
        private String yAxisName;
        private EquilibriumFluid fluid;
        private GraphPoints[] fluidGraphParams;
        private GraphHolder graphHolder;
        private RelaxationParams params;
        private CyclicBarrier fluidsBarrier;
        private HeightCoefRunnable heightCoefRunnable;
        private GraphPoints heightGraphParam;

        @Override
        public void run() {
            ProblemResult[] result;
            List<Vector2> heightCoefsPoints = new ArrayList<>();

            try {
                result = fluid.calcRelaxation(params, heightCoefsPoints);

                for (int i = 0; i < result.length; i++) {
                    fluidGraphParams[i].points = result[i].points;
                    fluidGraphParams[i].desription = String.format(Locale.ENGLISH, "Bond number = %f", result[i].bond);
                }

                Gdx.app.postRunnable(new DrawRunnable(problemName, xAxisName, yAxisName, graphHolder, fluidGraphParams, equalAxisScaleMarks));

                heightGraphParam.points = listToArray(heightCoefsPoints);
                heightCoefRunnable.addCoefGraph(heightGraphParam);

                fluidsBarrier.await(5000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                ErrorDialog errorDialog = ResourceManager.getErrorDialog();
                getStage().addActor(errorDialog);
                errorDialog.setMessage(problemName + ": " + e.getMessage());
                errorDialog.toFront();
                errorDialog.setVisible(true);

                e.printStackTrace();

                if (generateButton != null) {
                    generateButton.setDisabled(false);
                }
            }
        }

        private Vector2[] listToArray(List<Vector2> list) {
            Vector2[] result = new Vector2[list.size()];

            for (int i = 0; i < result.length; i++) {
                result[i] = list.get(i);
            }

            return result;
        }

        public void setProblemName(String problemName) {
            this.problemName = problemName;
        }

        public void setAxisNames(String xAxisName, String yAxisName) {
            this.xAxisName = xAxisName;
            this.yAxisName = yAxisName;
        }

        public void setFluid(EquilibriumFluid fluid) {
            this.fluid = fluid;
        }

        public void setFluidGraphParams(GraphPoints[] params) {
            fluidGraphParams = params;
        }

        public void setGraphHolder(GraphHolder graphHolder) {
            this.graphHolder = graphHolder;
        }

        public void setParams(RelaxationParams params) {
            this.params = params;
        }

        public void setFluidsBarrier(CyclicBarrier fluidsBarrier) {
            this.fluidsBarrier = fluidsBarrier;
        }

        public void setHeightCoefRunnable(HeightCoefRunnable runnable) {
            heightCoefRunnable = runnable;
        }

        public void setHeightGraphParam(GraphPoints param) {
            heightGraphParam = param;
        }
    }

    private class HeightCoefRunnable implements Runnable {
        private String name;
        private List<GraphPoints> coefGraphs;
        private ReentrantLock listLock;
        private GraphHolder graphHolder;

        HeightCoefRunnable() {
            coefGraphs = new LinkedList<>();
            listLock = new ReentrantLock();
        }

        @Override
        public void run() {
            GraphPoints[] graphs = new GraphPoints[coefGraphs.size()];

            try {
                Gdx.app.postRunnable(new DrawRunnable(name, HEIGHT_COEF_X_AXIS_NAME, HEIGHT_COEF_Y_AXIS_NAME,
                        graphHolder, coefGraphs.toArray(graphs), false));
            } catch (Exception e) {
                ErrorDialog errorDialog = ResourceManager.getErrorDialog();
                getStage().addActor(errorDialog);
                errorDialog.setMessage(name + ": " + e.getMessage());
                errorDialog.toFront();
                errorDialog.setVisible(true);

                e.printStackTrace();
            } finally {
                if (generateButton != null) {
                    generateButton.setDisabled(false);
                }
            }
        }

        public void setName(String name) {
            this.name = name;
        }

        public void addCoefGraph(GraphPoints graphParams) {
            try {
                listLock.lock();
                coefGraphs.add(graphParams);
            } finally {
                listLock.unlock();
            }
        }

        public void setGraphHolder(GraphHolder graphHolder) {
            this.graphHolder = graphHolder;
        }
    }

    private class DrawRunnable implements Runnable {
        private String graphName;
        private String xAxisName;
        private String yAxisName;
        private GraphHolder graphHolder;
        private GraphPoints[] graphParams;
        private boolean equalAxisScaleMarks;

        DrawRunnable(String graphName, String xAxisName, String yAxisName, GraphHolder graphHolder,
                     GraphPoints[] graphParams, boolean equalAxisScaleMarks) {
            this.graphName = graphName;
            this.xAxisName = xAxisName;
            this.yAxisName = yAxisName;
            this.graphHolder = graphHolder;
            this.graphParams = graphParams;
            this.equalAxisScaleMarks = equalAxisScaleMarks;
        }

        @Override
        public void run() {
            try {
                graphLock.lock();

                graph.clear();
                graph.setEqualAxisScaleMarks(equalAxisScaleMarks);
                graph.setName(graphName);
                graph.setAxisNames(xAxisName, yAxisName);

                for (GraphPoints param : graphParams) {
                    graph.addGraph(param);
                }

                graphHolder.setGraph(graph.getGraph(Math.round(PresentationWidget.this.getWidth()), Math.round(PresentationWidget.this.getHeight())));
            } catch (Exception e) {
                ErrorDialog errorDialog = ResourceManager.getErrorDialog();
                getStage().addActor(errorDialog);
                errorDialog.setMessage("Draw: " + e.getMessage());
                errorDialog.toFront();
                errorDialog.setVisible(true);

                e.printStackTrace();
            } finally {
                graphLock.unlock();
            }
        }
    }
}
