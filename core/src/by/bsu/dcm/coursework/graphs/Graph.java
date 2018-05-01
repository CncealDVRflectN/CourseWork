package by.bsu.dcm.coursework.graphs;

import by.bsu.dcm.coursework.AssetsManager;
import by.bsu.dcm.coursework.graphics.Graphics;
import by.bsu.dcm.coursework.graphics.Graphics.AntiAliasing;
import by.bsu.dcm.coursework.math.Util;
import by.bsu.dcm.coursework.util.Pair;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class Graph implements Disposable {
    public enum DescriptionAlign {
        TopLeft, TopRight, BottomRight, BottomLeft
    }

    public enum NameAlign {
        Top, Bottom
    }

    private static final short DEFAULT_WIDTH = 1280;
    private static final short DEFAULT_HEIGHT = 720;
    private static final byte DEFAULT_CELL_NUM_X = 16;
    private static final byte DEFAULT_CELL_NUM_Y = 9;
    private static final float[] SCALES = {0.1f, 0.2f, 0.25f, 0.5f, 1.0f};
    private static final int[] DEFAULT_SCALE_POWS = {-1, -1, -2, -1, 0};

    private AntiAliasing graphsAA;

    private GraphBackground background;
    private GraphAxis axis;
    private GraphName name;
    private NameAlign nameAlign;
    private GraphDescription description;
    private DescriptionAlign descriptionAlign;

    private Vector2 centerAxis;
    private Vector2 centerAxisNorm;
    private Vector2 scaleStep;
    private float[] scalesX;
    private float[] scalesY;
    private float[] scalesXNorm;
    private float[] scalesYNorm;
    private int scalesXPow;
    private int scalesYPow;

    private List<GraphPoints> graphs;
    private List<GraphPoints> graphsNorm;
    private Vector2 graphsMax;
    private Vector2 graphsMin;

    private SpriteBatch batch;
    private ShapeRenderer renderer;

    public Graph() {
        background = new GraphBackground(new Color(1.0f, 1.0f, 1.0f, 1.0f), new Color(0.75f, 0.75f, 0.75f, 1.0f), 1.0f);
        axis = new GraphAxis();
        name = new GraphName();
        nameAlign = NameAlign.Top;
        description = new GraphDescription();
        descriptionAlign = DescriptionAlign.BottomLeft;

        graphsAA = AntiAliasing.noAA;

        scaleStep = new Vector2();

        graphs = new ArrayList<>();
        graphsNorm = new ArrayList<>();

        graphsMax = new Vector2();
        graphsMin = new Vector2();

        batch = new SpriteBatch();
        renderer = new ShapeRenderer();
    }

    private void calcMinMax() {
        graphsMax.set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        graphsMin.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

        graphs.forEach(graph -> {
            for (Vector2 node : graph.points) {
                graphsMax.x = (node.x > graphsMax.x) ? node.x : graphsMax.x;
                graphsMax.y = (node.y > graphsMax.y) ? node.y : graphsMax.y;

                graphsMin.x = (node.x < graphsMin.x) ? node.x : graphsMin.x;
                graphsMin.y = (node.y < graphsMin.y) ? node.y : graphsMin.y;
            }
        });
    }

    private Pair<Float, Integer> calcScaleStep(float dif, int cellNum) {
        float result;
        int pow = 0;
        int scaleIndex;
        byte sign = 1;

        result = dif / cellNum;

        if (result != 0.0f) {
            if (result < 0.1f) {
                sign = -1;
                while (result < 0.1f) {
                    result *= 10.0f;
                    pow++;
                }
            } else {
                sign = 1;
                while (result > 1.0f) {
                    result /= 10.0f;
                    pow++;
                }
            }
        }

        for (scaleIndex = 0; scaleIndex < SCALES.length; scaleIndex++) {
            if (result <= SCALES[scaleIndex]) {
                result = SCALES[scaleIndex];
                break;
            }
        }

        for (int i = 0; i < pow; i++) {
            result *= (sign > 0.0f) ? 10.0f : 0.1f;
        }

        return new Pair<>(result, sign * pow + DEFAULT_SCALE_POWS[scaleIndex]);
    }

    private float[] calcScales(float min, float scaleStep, int cellNum) {
        float[] result;
        float minMul = min / scaleStep;
        int sign = (minMul >= 0.0f) ? 1 : -1;
        result = new float[cellNum + 3];

        minMul *= sign;
        if (minMul < 1.0f) {
            minMul = 1.0f;
        } else {
            minMul += (minMul % 10.0f != 0.0f) ? 1.0f - (minMul - Math.round(minMul)) : 0.0f;
        }
        minMul *= sign;

        result[0] = (minMul - 1.0f) * scaleStep;
        for (int i = 1; i < result.length; i++) {
            result[i] = (minMul + (i - 1)) * scaleStep;
        }

        return result;
    }

    private void center(float[] scales, float nodeMin, float nodeMax, float scaleStep) {
        float minDif = nodeMin - scales[1];
        float maxDif = scales[scales.length - 2] - nodeMax;
        float minOffset = minDif / scaleStep;
        float maxOffset = maxDif / scaleStep;
        float availableOffset = maxOffset - minOffset;
        int offset = Math.round(availableOffset / 2.0f);

        for (int i = 0; i < scales.length; i++) {
            scales[i] -= offset * scaleStep;
        }
    }

    private Vector2 calcAxisCenter() {
        Vector2 result = new Vector2();

        if (scalesX[1] <= 0.0f && scalesX[scalesX.length - 2] >= 0.0f) {
            result.x = 0.0f;
        } else if (scalesX[1] < 0.0f && scalesX[scalesX.length - 2] < 0.0f) {
            result.x = scalesX[scalesX.length - 2];
        } else {
            result.x = scalesX[1];
        }

        if (scalesY[1] <= 0.0f && scalesY[scalesY.length - 2] >= 0.0f) {
            result.y = 0.0f;
        } else if (scalesY[1] < 0.0f && scalesY[scalesY.length - 2] < 0.0f) {
            result.y = scalesY[scalesY.length - 2];
        } else {
            result.y = scalesY[1];
        }

        return result;
    }

    private void normalize() {
        Vector2 min = new Vector2((scalesX[0] + scalesX[1]) / 2.0f, (scalesY[0] + scalesY[1]) / 2.0f);
        Vector2 max = new Vector2((scalesX[scalesX.length - 2] + scalesX[scalesX.length - 1]) / 2.0f,
                (scalesY[scalesY.length - 2] + scalesY[scalesY.length - 1]) / 2.0f);
        graphsNorm.clear();

        scalesXNorm = Util.normalize(scalesX, min.x, max.x);
        scalesYNorm = Util.normalize(scalesY, min.y, max.y);
        centerAxisNorm = Util.normalize(centerAxis, min, max);
        graphs.forEach(graph -> {
            GraphPoints graphNorm = new GraphPoints();

            graphNorm.lineWidth = graph.lineWidth;
            graphNorm.lineColor.set(graph.lineColor);
            graphNorm.pointColor.set(graph.pointColor);
            graphNorm.pointSize = graph.pointSize;
            graphNorm.points = Util.normalize(graph.points, min, max);

            graphsNorm.add(graphNorm);
        });
    }

    private void calcParams(float scaleX, float scaleY) {
        Vector2 dif = new Vector2();
        Vector2 ratio = new Vector2();
        Pair<Float, Integer> stepResult;
        int cellNumXScaled = Math.round(DEFAULT_CELL_NUM_X * scaleX);
        int cellNumYScaled = Math.round(DEFAULT_CELL_NUM_Y * scaleY);

        calcMinMax();
        dif.set(graphsMax.x - graphsMin.x, graphsMax.y - graphsMin.y);
        ratio.set(dif.x / cellNumXScaled, dif.y / cellNumYScaled);
        if (axis.isEqualAxisScaleMarks()) {
            stepResult = (ratio.x > ratio.y) ? calcScaleStep(dif.x, cellNumXScaled) : calcScaleStep(dif.y, cellNumYScaled);
            scaleStep.set(stepResult.first, stepResult.first);
            scalesXPow = stepResult.second;
            scalesYPow = stepResult.second;
        } else {
            stepResult = calcScaleStep(dif.x, cellNumXScaled);
            scaleStep.x = stepResult.first;
            scalesXPow = stepResult.second;
            stepResult = calcScaleStep(dif.y, cellNumYScaled);
            scaleStep.y = stepResult.first;
            scalesYPow = stepResult.second;
        }
        scalesX = calcScales(graphsMin.x, scaleStep.x, cellNumXScaled);
        scalesY = calcScales(graphsMin.y, scaleStep.y, cellNumYScaled);
        center(scalesX, graphsMin.x, graphsMax.x, scaleStep.x);
        center(scalesY, graphsMin.y, graphsMax.y, scaleStep.y);
        centerAxis = calcAxisCenter();
        normalize();
    }

    private TextureRegion generateCoordsSystem(int width, int height) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        Pixmap pixmap;
        Matrix4 projMatrix = new Matrix4().setToOrtho2D(0.0f, 0.0f, width, height);
        TextureRegion result;

        renderer.setProjectionMatrix(projMatrix);
        batch.setProjectionMatrix(projMatrix);

        fbo.begin();

        Gdx.gl30.glEnable(GL30.GL_BLEND);
        Gdx.gl30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        background.draw(renderer, scalesXNorm, scalesYNorm, width, height);
        axis.draw(batch, renderer, centerAxis, centerAxisNorm, scalesX, scalesY, scalesXNorm, scalesYNorm,
                scalesXPow, scalesYPow, width, height);

        pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, fbo.getWidth(), fbo.getHeight());

        fbo.end();

        Gdx.gl30.glDisable(GL30.GL_BLEND);

        result = new TextureRegion(new Texture(pixmap));
        result.flip(false, true);

        fbo.dispose();
        pixmap.dispose();

        return result;
    }

    private TextureRegion generateGraphsRaw(int width, int height, float scaleMul) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        Pixmap pixmap;
        TextureRegion result;

        renderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0.0f, 0.0f, width, height));

        fbo.begin();

        Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);
        Gdx.gl30.glEnable(GL30.GL_BLEND);
        Gdx.gl30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        graphsNorm.forEach(graphNorm -> {
            Gdx.gl30.glLineWidth(graphNorm.lineWidth * scaleMul);

            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.setColor(graphNorm.lineColor);

            for (int i = 1; i < graphNorm.points.length; i++) {
                renderer.line(graphNorm.points[i - 1].x * width, graphNorm.points[i - 1].y * height,
                        graphNorm.points[i].x * width, graphNorm.points[i].y * height);
            }

            renderer.end();

            Gdx.gl30.glLineWidth(scaleMul);

            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(graphNorm.pointColor);

            for (int i = 0; i < graphNorm.points.length; i++) {
                renderer.circle(graphNorm.points[i].x * width, graphNorm.points[i].y * height, graphNorm.pointSize * scaleMul);
            }

            renderer.end();
        });

        pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, fbo.getWidth(), fbo.getHeight());

        fbo.end();

        Gdx.gl30.glDisable(GL30.GL_BLEND);

        result = new TextureRegion(new Texture(pixmap));
        result.flip(false, true);

        fbo.dispose();
        pixmap.dispose();

        return result;
    }

    private TextureRegion generateGraphs(int width, int height) {
        TextureRegion result;
        TextureRegion buffer;

        switch (graphsAA) {
            case SSAA4:
                buffer = generateGraphsRaw(2 * width, 2 * height, 2.0f);
                result = Graphics.calcDownsample4(buffer);
                buffer.getTexture().dispose();
                return result;
            case FXAA:
                buffer = generateGraphsRaw(width, height, 1.0f);
                result = Graphics.calcFXAA(buffer);
                buffer.getTexture().dispose();
                return result;
            case SSAA4_FXAA:
                buffer = generateGraphsRaw(2 * width, 2 * height, 2.0f);
                result = Graphics.calcFXAA(buffer);
                buffer.getTexture().dispose();
                buffer = result;
                result = Graphics.calcDownsample4(buffer);
                buffer.getTexture().dispose();
                return result;
            default:
                return generateGraphsRaw(width, height, 1.0f);
        }
    }

    public TextureRegion getGraph(int width, int height) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        Pixmap pixmap;
        TextureRegion result;
        TextureRegion coordsSystem;
        TextureRegion graphsTex;

        calcParams((float) width / (float) DEFAULT_WIDTH, (float) height / (float) DEFAULT_HEIGHT);
        coordsSystem = generateCoordsSystem(width, height);
        graphsTex = generateGraphs(width, height);

        batch.setShader(AssetsManager.getGraphBlendShader());
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0.0f, 0.0f, width, height));
        batch.disableBlending();

        fbo.begin();

        Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);

        batch.begin();

        AssetsManager.getGraphBlendShader().setUniformi("u_background", 1);

        Gdx.gl30.glActiveTexture(GL30.GL_TEXTURE1);
        coordsSystem.getTexture().bind();

        Gdx.gl30.glActiveTexture(GL30.GL_TEXTURE0);
        graphsTex.getTexture().bind();


        batch.draw(coordsSystem, 0.0f, 0.0f);
        batch.draw(graphsTex, 0.0f, 0.0f);

        batch.end();

        name.draw(batch, renderer, nameAlign, width, height);
        description.draw(batch, renderer, descriptionAlign, graphs, width, height);

        batch.enableBlending();

        pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, fbo.getWidth(), fbo.getHeight());

        fbo.end();

        result = new TextureRegion(new Texture(pixmap));
        result.flip(false, true);

        fbo.dispose();
        pixmap.dispose();
        coordsSystem.getTexture().dispose();
        graphsTex.getTexture().dispose();

        return result;
    }

    public void clear() {
        graphs.clear();
    }

    public void addGraph(GraphPoints graph) {
        graphs.add(graph);
    }

    public void removeGraph(int index) {
        graphs.remove(index);
    }

    public void removeGraph(GraphPoints graph) {
        graphs.remove(graph);
    }

    public void setBackgroundColor(Color color) {
        background.setBackgroundColor(color);
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        background.setBackgroundColor(r, g, b, a);
    }

    public void setMarkupColor(Color color) {
        background.setMarkupColor(color);
    }

    public void setMarkupColor(float r, float g, float b, float a) {
        background.setMarkupColor(r, g, b, a);
    }

    public void setMarkupLineWidth(float lineWidth) {
        background.setMarkupLineWidth(lineWidth);
    }

    public void setAxisColor(Color color) {
        axis.setAxisColor(color);
    }

    public void setAxisColor(float r, float g, float b, float a) {
        axis.setAxisColor(r, g, b, a);
    }

    public void setAxisLineWidth(float lineWidth) {
        axis.setAxisLineWidth(lineWidth);
    }

    public void setAxisScaleMarkLinesLength(float top, float bottom, float left, float right) {
        axis.setAxisScaleMarkLinesLength(top, bottom, left, right);
    }

    public void setHorizontalScaleMarkOffset(Vector2 offset) {
        axis.setHorizontalScaleMarkOffset(offset);
    }

    public void setHorizontalScaleMarkOffset(float x, float y) {
        axis.setHorizontalScaleMarkOffset(x, y);
    }

    public void setVerticalScaleMarkOffset(Vector2 offset) {
        axis.setVerticalScaleMarkOffset(offset);
    }

    public void setVerticalScaleMarkOffset(float x, float y) {
        axis.setVerticalScaleMarkOffset(x, y);
    }

    public void setAxisFontSize(int size) {
        axis.setFontSize(size);
    }

    public void setAxisFontColor(Color color) {
        axis.setFontColor(color);
    }

    public void setAxisFontColor(float r, float g, float b, float a) {
        axis.setFontColor(r, g, b, a);
    }

    public void setDescriptionAlign(DescriptionAlign align) {
        descriptionAlign = align;
    }

    public void setDescriptionBackgroundColor(Color color) {
        description.setBackgroundColor(color);
    }

    public void setDescriptionBackgroundColor(float r, float g, float b, float a) {
        description.setBackgroundColor(r, g, b, a);
    }

    public void setDescriptionBorderLineColor(Color color) {
        description.setBorderLineColor(color);
    }

    public void setDescriptionBorderLineColor(float r, float g, float b, float a) {
        description.setBorderLineColor(r, g, b, a);
    }

    public void setDescriptionBorderLineWidth(float lineWidth) {
        description.setBorderLineWidth(lineWidth);
    }

    public void setDescriptionFontSize(int size) {
        description.setFontSize(size);
    }

    public void setDescriptionFontColor(Color color) {
        description.setFontColor(color);
    }

    public void setDescriptionFontColor(float r, float g, float b, float a) {
        description.setFontColor(r, g, b, a);
    }

    public void setDescriptionPadding(float top, float right, float bottom, float left) {
        description.setPadding(top, right, bottom, left);
    }

    public void setDescriptionSpacing(float horizontal, float vertical) {
        description.setSpacing(horizontal, vertical);
    }

    public void setName(String name) {
        this.name.setName(name);
    }

    public void setNameAlign(NameAlign align) {
        nameAlign = align;
    }

    public void setNameBackgroundColor(Color color) {
        name.setBackgroundColor(color);
    }

    public void setNameBackgroundColor(float r, float g, float b, float a) {
        name.setBackgroundColor(r, g, b, a);
    }

    public void setNameBorderLineColor(Color color) {
        name.setBorderLineColor(color);
    }

    public void setNameBorderLineColor(float r, float g, float b, float a) {
        name.setBorderLineColor(r, g, b, a);
    }

    public void setNameBorderLineWidth(float width) {
        name.setBorderLineWidth(width);
    }

    public void setNameTextPadding(float top, float right, float bottom, float left) {
        name.setTextPadding(top, right, bottom, left);
    }

    public void setNamePadding(float padding) {
        name.setPadding(padding);
    }

    public void setNameFontSize(int size) {
        name.setFontSize(size);
    }

    public void setNameFontColor(Color color) {
        name.setFontColor(color);
    }

    public void setNameFontColor(float r, float g, float b, float a) {
        name.setFontColor(r, g, b, a);
    }

    public void setEqualAxisScaleMarks(boolean equal) {
        axis.setEqualAxisScaleMarks(equal);
    }

    public boolean isEqualAxisScaleMarks() {
        return axis.isEqualAxisScaleMarks();
    }

    public void setAntialiasing(AntiAliasing antialiasing) {
        graphsAA = antialiasing;
    }

    @Override
    public void dispose() {
        axis.dispose();
        name.dispose();
        description.dispose();
        renderer.dispose();
        batch.dispose();
    }
}
