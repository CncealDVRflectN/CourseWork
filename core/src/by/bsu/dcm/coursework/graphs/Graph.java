package by.bsu.dcm.coursework.graphs;

import by.bsu.dcm.coursework.AssetsManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public enum AntiAliasing {
        noAA(), SSAA4(), FXAA(), SSAA4_FXAA()
    }

    private static final short DEFAULT_WIDTH = 1280;
    private static final short DEFAULT_HEIGHT = 720;
    private static final byte DEFAULT_CELL_NUM_X = 16;
    private static final byte DEFAULT_CELL_NUM_Y = 9;
    private static final float[] DIMS = { 0.1f, 0.2f, 0.25f, 0.5f, 1.0f };

    private static final float FXAA_SPAN_MAX = 8.0f;
    private static final float FXAA_REDUCE_MUL = 1.0f / 8.0f;
    private static final float FXAA_REDUCE_MIN = 1.0f / 128.0f;

    private AntiAliasing graphsAA;

    private Color backgroundColor;
    private Color markupColor;
    private Color axisColor;
    private FreeTypeFontParameter fontParam;
    private BitmapFont font;
    private float markupLineWidth;
    private float axisLineWidth;

    private Vector2 centerAxis;
    private Vector2 centerAxisNorm;
    private Vector2 dimStep;
    private float[] dimsX;
    private float[] dimsY;
    private float[] dimsXNorm;
    private float[] dimsYNorm;

    private List<GraphPoints> graphs;
    private List<GraphPoints> graphsNorm;
    private Vector2 graphsMax;
    private Vector2 graphsMin;

    private SpriteBatch batch;
    private ShapeRenderer renderer;

    public Graph() {
        fontParam = new FreeTypeFontParameter();
        backgroundColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        markupColor = new Color(0.75f, 0.75f, 0.75f, 1.0f);
        axisColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        markupLineWidth = 1.0f;
        axisLineWidth = 1.0f;
        fontParam.size = 12;
        fontParam.color = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        font = AssetsManager.getFont(fontParam);

        graphsAA = AntiAliasing.noAA;

        dimStep = new Vector2();

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

    private float calcDimStep(float dif, int cellNum) {
        float result;
        int pow = 0;
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

        for (float dim : DIMS) {
            if (result <= dim) {
                result = dim;
                break;
            }
        }

        for (byte i = 0; i < pow; i++) {
            result *= (sign > 0.0f) ? 10.0f : 0.1f;
        }

        return result;
    }

    private float[] calcDim(float min, float dimStep, int cellNum) {
        float[] result;
        float minMul = min / dimStep;
        int sign = (minMul >= 0.0f) ? 1 : -1;
        result = new float[cellNum + 3];

        minMul *= sign;
        if (minMul < 1.0f) {
            minMul = 1.0f;
        } else {
            minMul += (minMul % 10.0f != 0.0f) ? 1.0f - (minMul - Math.round(minMul)) : 0.0f;
        }
        minMul *= sign;

        result[0] = (minMul - 1.0f) * dimStep;
        for (int i = 1; i < result.length; i++) {
            result[i] = (minMul + (i - 1)) * dimStep;
        }

        return result;
    }

    private void center(float[] dims, float nodeMin, float nodeMax, float dimStep) {
        float minDif = nodeMin - dims[1];
        float maxDif = dims[dims.length - 2] - nodeMax;
        float minOffset = minDif / dimStep;
        float maxOffset = maxDif / dimStep;
        float availableOffset = maxOffset - minOffset;
        int offset = Math.round(availableOffset / 2.0f);

        for (int i = 0; i < dims.length; i++) {
            dims[i] -= offset * dimStep;
        }
    }

    private Vector2 calcCenterAxis(float[] dimsX, float[] dimsY) {
        Vector2 result = new Vector2();

        if (dimsX[1] <= 0.0f && dimsX[dimsX.length - 2] >= 0.0f) {
            result.x = 0.0f;
        } else if (dimsX[1] < 0.0f && dimsX[dimsX.length - 2] < 0.0f) {
            result.x = dimsX[dimsX.length - 2];
        } else {
            result.x = dimsX[1];
        }

        if (dimsY[1] <= 0.0f && dimsY[dimsY.length - 2] >= 0.0f) {
            result.y = 0.0f;
        } else if (dimsY[1] < 0.0f && dimsY[dimsY.length - 2] < 0.0f) {
            result.y = dimsY[dimsY.length - 2];
        } else {
            result.y = dimsY[1];
        }

        return result;
    }

    private float[] normalize(float[] arr, float min, float max) {
        float[] result = new float[arr.length];

        for (int i = 0; i < arr.length; i++) {
            result[i] = (arr[i] - min) / (max - min);
        }

        return result;
    }

    private Vector2[] normalize(Vector2[] arr, Vector2 min, Vector2 max) {
        Vector2[] result = new Vector2[arr.length];

        for (int i = 0; i < arr.length; i++) {
            result[i] = new Vector2((arr[i].x - min.x) / (max.x - min.x), (arr[i].y - min.y) / (max.y - min.y));
        }

        return result;
    }

    private Vector2 normalize(Vector2 vect, Vector2 min, Vector2 max) {
        return new Vector2((vect.x - min.x) / (max.x - min.x), (vect.y - min.y) / (max.y - min.y));
    }

    private void normalize() {
        Vector2 min = new Vector2((dimsX[0] + dimsX[1]) / 2.0f, (dimsY[0] + dimsY[1]) / 2.0f);
        Vector2 max = new Vector2((dimsX[dimsX.length - 2] + dimsX[dimsX.length - 1]) / 2.0f,
                (dimsY[dimsY.length - 2] + dimsY[dimsY.length - 1]) / 2.0f);
        graphsNorm.clear();

        dimsXNorm = normalize(dimsX, min.x, max.x);
        dimsYNorm = normalize(dimsY, min.y, max.y);
        centerAxisNorm = normalize(centerAxis, min, max);
        graphs.forEach(graph -> {
            GraphPoints graphNorm = new GraphPoints();

            graphNorm.lineWidth = graph.lineWidth;
            graphNorm.lineColor.set(graph.lineColor);
            graphNorm.pointColor.set(graph.pointColor);
            graphNorm.pointSize = graph.pointSize;
            graphNorm.points = normalize(graph.points, min, max);

            graphsNorm.add(graphNorm);
        });
    }

    private void drawBackground(int width, int height) {
        Gdx.gl30.glLineWidth(markupLineWidth);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(markupColor);

        for (int i = 0; i < dimsXNorm.length; i++) {
            renderer.line(dimsXNorm[i] * width, 0.0f, dimsXNorm[i] * width, height);
        }

        for (int i = 0; i < dimsYNorm.length; i++) {
            renderer.line(0.0f, dimsYNorm[i] * height, width, dimsYNorm[i] * height);
        }

        renderer.end();

        Gdx.gl30.glLineWidth(1.0f);
    }

    private void drawAxis(int width, int height) {
        Gdx.gl30.glLineWidth(axisLineWidth);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(axisColor);

        renderer.line(0.0f, centerAxisNorm.y * height, width, centerAxisNorm.y * height);
        renderer.line(centerAxisNorm.x * width, 0.0f, centerAxisNorm.x * width, height);

        for (int i = 0; i < dimsXNorm.length; i++) {
            renderer.line(dimsXNorm[i] * width, centerAxisNorm.y * height - 5.0f,
                    dimsXNorm[i] * width, centerAxisNorm.y * height + 5.0f);
        }

        for (int i = 0; i < dimsYNorm.length; i++) {
            renderer.line(centerAxisNorm.x * width - 5.0f, dimsYNorm[i] * height,
                    centerAxisNorm.x * width + 5.0f, dimsYNorm[i] * height);
        }

        renderer.end();

        Gdx.gl30.glLineWidth(1.0f);
    }

    private void drawDims(int width, int height) {
        batch.setShader(SpriteBatch.createDefaultShader());

        batch.begin();

        for (int i = 0; i < dimsXNorm.length; i++) {
            if (dimsX[i] != centerAxis.x) {
                font.draw(batch, Float.toString(dimsX[i]), dimsXNorm[i] * width , centerAxisNorm.y * height - 10.0f);
            }
        }

        for (int i = 0; i < dimsYNorm.length; i++) {
            if (dimsY[i] != centerAxis.y) {
                font.draw(batch, Float.toString(dimsY[i]), centerAxisNorm.x * width + 10.0f, dimsYNorm[i] * height);
            }
        }

        batch.end();
    }

    private TextureRegion generateCoordsSystem(int width, int height) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        Matrix4 projMatrix = new Matrix4().setToOrtho2D(0.0f, 0.0f, width, height);
        TextureRegion result;

        renderer.setProjectionMatrix(projMatrix);
        batch.setProjectionMatrix(projMatrix);

        fbo.begin();

        Gdx.gl30.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1.0f);
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);
        Gdx.gl30.glEnable(GL30.GL_BLEND);
        Gdx.gl30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        drawBackground(width, height);
        drawAxis(width, height);
        drawDims(width, height);

        fbo.end();

        Gdx.gl30.glDisable(GL30.GL_BLEND);

        result = new TextureRegion(fbo.getColorBufferTexture());
        result.flip(false, true);

        return result;
    }

    private TextureRegion generateGraphsRaw(int width, int height, float scaleMul) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
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

        fbo.end();

        Gdx.gl30.glDisable(GL30.GL_BLEND);

        result = new TextureRegion(fbo.getColorBufferTexture());
        result.flip(false, true);

        return result;
    }

    private void calcParams(float scaleX, float scaleY) {
        int cellNumXScaled = Math.round(DEFAULT_CELL_NUM_X * scaleX);
        int cellNumYScaled = Math.round(DEFAULT_CELL_NUM_Y * scaleY);
        int minCellNum = Math.min(cellNumXScaled, cellNumYScaled);
        float dif;

        calcMinMax();
        dif = (cellNumXScaled > cellNumYScaled) ? graphsMax.y - graphsMin.y : graphsMax.x - graphsMin.x;
        dimStep.x = calcDimStep(dif, minCellNum);
        dimStep.y = calcDimStep(dif, minCellNum);
        dimsX = calcDim(graphsMin.x, dimStep.x, cellNumXScaled);
        dimsY = calcDim(graphsMin.y, dimStep.y, cellNumYScaled);
        center(dimsX, graphsMin.x, graphsMax.x, dimStep.x);
        center(dimsY, graphsMin.y, graphsMax.y, dimStep.y);
        centerAxis = calcCenterAxis(dimsX, dimsY);
        normalize();
    }

    private TextureRegion calcDownsample4(TextureRegion texture) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2, false);
        TextureRegion result;

        batch.setShader(AssetsManager.getGraphDownsample4Shader());
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0.0f, 0.0f, texture.getRegionWidth(), texture.getRegionHeight()));
        batch.disableBlending();

        fbo.begin();

        Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);

        batch.begin();

        AssetsManager.getGraphDownsample4Shader().setUniformf("u_texSize", texture.getRegionWidth() / 2, texture.getRegionHeight() / 2);

        batch.draw(texture, 0.0f, 0.0f);

        batch.end();
        fbo.end();

        batch.enableBlending();

        result = new TextureRegion(fbo.getColorBufferTexture());
        result.flip(false, true);

        return result;
    }

    private TextureRegion calcFXAA(TextureRegion texture, float spanMax, float reduceMul, float reduceMin) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, texture.getRegionWidth(), texture.getRegionHeight(), false);
        TextureRegion result;

        batch.setShader(AssetsManager.getGraphFXAAShader());
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0.0f, 0.0f, texture.getRegionWidth(), texture.getRegionHeight()));
        batch.disableBlending();

        fbo.begin();

        Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);

        batch.begin();

        AssetsManager.getGraphFXAAShader().setUniformf("u_texSize", texture.getRegionWidth(), texture.getRegionHeight());
        AssetsManager.getGraphFXAAShader().setUniformf("u_spanMax", spanMax);
        AssetsManager.getGraphFXAAShader().setUniformf("u_reduceMul", reduceMul);
        AssetsManager.getGraphFXAAShader().setUniformf("u_reduceMin", reduceMin);

        batch.draw(texture, 0.0f, 0.0f);

        batch.end();
        batch.enableBlending();

        fbo.end();

        result = new TextureRegion(fbo.getColorBufferTexture());
        result.flip(false, true);

        return result;
    }

    private TextureRegion generateGraphs(int width, int height) {
        switch (graphsAA) {
            case SSAA4:
                return calcDownsample4(generateGraphsRaw(2 * width, 2 * height, 2.0f));
            case FXAA:
                return calcFXAA(generateGraphsRaw(width, height, 1.0f), FXAA_SPAN_MAX, FXAA_REDUCE_MUL, FXAA_REDUCE_MIN);
            case SSAA4_FXAA:
                return calcDownsample4(calcFXAA(generateGraphsRaw(2 * width, 2 * height, 2.0f),
                        FXAA_SPAN_MAX, FXAA_REDUCE_MUL, FXAA_REDUCE_MIN));
            default:
                return generateGraphsRaw(width, height, 1.0f);
        }
    }

    public TextureRegion getGraph(int width, int height) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        TextureRegion result;
        TextureRegion coordsSystem;
        TextureRegion graphsTex;

        calcParams(width / DEFAULT_WIDTH, height / DEFAULT_HEIGHT);
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
        batch.enableBlending();

        fbo.end();

        result = new TextureRegion(fbo.getColorBufferTexture());
        result.flip(false, true);

        return result;
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
        backgroundColor.set(color);
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        backgroundColor.set(r, g, b, a);
    }

    public void setMarkupColor(Color color) {
        markupColor.set(color);
    }

    public void setMarkupColor(float r, float g, float b, float a) {
        markupColor.set(r, g, b, a);
    }

    public void setAxisColor(Color color) {
        axisColor.set(color);
    }

    public void setAxisColor(float r, float g, float b, float a) {
        axisColor.set(r, g, b, a);
    }

    public void setMarkupLineWidth(float lineWidth) {
        markupLineWidth = lineWidth;
    }

    public void setAxisLineWidth(float lineWidth) {
        axisLineWidth = lineWidth;
    }

    public void setFontSize(int size) {
        fontParam.size = size;
        font = AssetsManager.getFont(fontParam);
    }

    public void setFontColor(Color color) {
        fontParam.color.set(color);
        font = AssetsManager.getFont(fontParam);
    }

    public void setFontColor(float r, float g, float b, float a) {
        fontParam.color.set(r, g, b, a);
        font = AssetsManager.getFont(fontParam);
    }

    public void setAntialiasing(AntiAliasing antialiasing) {
        graphsAA = antialiasing;
    }
}
