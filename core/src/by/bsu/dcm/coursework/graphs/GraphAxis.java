package by.bsu.dcm.coursework.graphs;

import by.bsu.dcm.coursework.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

class GraphAxis implements Disposable {
    private FreeTypeFontParameter fontParam;
    private BitmapFont font;
    private DecimalFormat decimalFormat;
    private String xAxisName;
    private String yAxisName;

    private Color axisColor;
    private float axisLineWidth;
    private float axisNamesPadding;

    private float scaleMarkLineTopLength;
    private float scaleMarkLineBottomLength;
    private float scaleMarkLineLeftLength;
    private float scaleMarkLineRightLength;
    private boolean equalAxisScaleMarks;

    private Vector2 horizontalScaleMarkOffset;
    private Vector2 verticalScaleMarkOffset;

    GraphAxis() {
        fontParam = new FreeTypeFontParameter();
        decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
        axisColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        axisLineWidth = 1.0f;

        fontParam.size = 14;
        fontParam.color = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        fontParam.characters = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRST\n" +
                "UVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        font = ResourceManager.getFont(fontParam);

        scaleMarkLineTopLength = 4.0f;
        scaleMarkLineBottomLength = 4.0f;
        scaleMarkLineLeftLength = 4.0f;
        scaleMarkLineRightLength = 4.0f;
        equalAxisScaleMarks = false;

        horizontalScaleMarkOffset = new Vector2(0.0f, 10.0f);
        verticalScaleMarkOffset = new Vector2(10.0f, 0.0f);

        xAxisName = "x";
        yAxisName = "y";
        axisNamesPadding = 5.0f;
    }

    private void drawAxis(ShapeRenderer renderer, Vector2 centerAxisNorm, float[] scalesXNorm, float[] scalesYNorm, int width, int height) {
        float scaleLineOffset = axisLineWidth / 2.0f;

        Gdx.gl20.glLineWidth(axisLineWidth);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(axisColor);

        renderer.line(0.0f, centerAxisNorm.y * height, width, centerAxisNorm.y * height);
        renderer.line(centerAxisNorm.x * width, 0.0f, centerAxisNorm.x * width, height);

        for (int i = 0; i < scalesXNorm.length; i++) {
            renderer.setColor(axisColor);
            renderer.line(scalesXNorm[i] * width, centerAxisNorm.y * height - (scaleLineOffset + scaleMarkLineBottomLength),
                    scalesXNorm[i] * width, centerAxisNorm.y * height + (scaleLineOffset + scaleMarkLineTopLength));
        }

        for (int i = 0; i < scalesYNorm.length; i++) {
            renderer.line(centerAxisNorm.x * width - (scaleLineOffset + scaleMarkLineLeftLength), scalesYNorm[i] * height,
                    centerAxisNorm.x * width + (scaleLineOffset + scaleMarkLineRightLength), scalesYNorm[i] * height);
        }

        renderer.end();

        Gdx.gl20.glLineWidth(1.0f);
    }

    private String calcScaleMarkFormat(int stepPow) {
        StringBuilder format = new StringBuilder("#0");

        if (stepPow < 0) {
            format.append('.');
            for (int i = 0; i > stepPow; i--) {
                format.append('0');
            }
        }

        return format.toString();
    }

    private void drawScaleMarks(Batch batch, Vector2 centerAxis, Vector2 centerAxisNorm, float[] scalesX, float[] scalesY,
                                float[] scalesXNorm, float[] scalesYNorm, int scalesXPow, int scalesYPow, int width, int height) {
        GlyphLayout layout = new GlyphLayout();
        Vector2 curOffset = new Vector2();
        String centerXMark;
        float horScaleMarksOffsetSign;
        float vertScaleMarksOffsetSign;
        float additionalOffset = axisLineWidth / 2.0f;

        horScaleMarksOffsetSign = (centerAxisNorm.y <= 0.5f) ? -1.0f : 1.0f;
        vertScaleMarksOffsetSign = (centerAxisNorm.x >= 0.5f) ? 1.0f : -1.0f;

        batch.setShader(SpriteBatch.createDefaultShader());

        batch.begin();

        decimalFormat.applyPattern(calcScaleMarkFormat(scalesXPow));
        for (int i = 0; i < scalesXNorm.length; i++) {
            if (scalesX[i] != centerAxis.x) {
                layout.setText(font, decimalFormat.format(scalesX[i]));

                curOffset.y = additionalOffset;
                curOffset.y += (centerAxisNorm.y <= 0.5f) ? 0.0f : layout.height;
                curOffset.x = -layout.width / 2.0f;

                font.draw(batch, layout, scalesXNorm[i] * width + horizontalScaleMarkOffset.x + curOffset.x,
                        centerAxisNorm.y * height + horScaleMarksOffsetSign * (horizontalScaleMarkOffset.y + curOffset.y));
            }
        }
        layout.setText(font, xAxisName);
        curOffset.y = additionalOffset;
        curOffset.y += (centerAxisNorm.y <= 0.5f) ? 0.0f : layout.height;
        curOffset.x = -layout.width - axisNamesPadding;
        font.draw(batch, layout, width + curOffset.x,
                centerAxisNorm.y * height + horScaleMarksOffsetSign * (horizontalScaleMarkOffset.y + curOffset.y));

        decimalFormat.applyPattern(calcScaleMarkFormat(scalesYPow));
        for (int i = 0; i < scalesYNorm.length; i++) {
            if (scalesY[i] != centerAxis.y) {
                layout.setText(font, decimalFormat.format(scalesY[i]));

                curOffset.x = additionalOffset;
                curOffset.x += (centerAxisNorm.x >= 0.5f) ? 0.0f : layout.width;
                curOffset.y = layout.height / 2.0f;

                font.draw(batch, layout, centerAxisNorm.x * width +
                                vertScaleMarksOffsetSign * (verticalScaleMarkOffset.x + curOffset.x),
                        scalesYNorm[i] * height + verticalScaleMarkOffset.y + curOffset.y);
            }
        }
        layout.setText(font, yAxisName);
        curOffset.x = additionalOffset;
        curOffset.x += (centerAxisNorm.x >= 0.5f) ? 0.0f : layout.width;
        curOffset.y = -axisNamesPadding;
        font.draw(batch, layout, centerAxisNorm.x * width +
                        vertScaleMarksOffsetSign * (verticalScaleMarkOffset.x + curOffset.x),
                height + curOffset.y);

        decimalFormat.applyPattern(calcScaleMarkFormat(scalesXPow));
        centerXMark = decimalFormat.format(centerAxis.x);
        if (centerAxis.x == centerAxis.y) {
            layout.setText(font, centerXMark);
        } else {
            decimalFormat.applyPattern(calcScaleMarkFormat(scalesYPow));
            layout.setText(font, centerXMark + ", " + decimalFormat.format(centerAxis.y));
        }

        curOffset.set(additionalOffset, additionalOffset);
        curOffset.x += (centerAxisNorm.x >= 0.5f) ? 0.0f : layout.width;
        curOffset.y += (centerAxisNorm.y <= 0.5f) ? 0.0f : layout.height;

        font.draw(batch, layout, centerAxisNorm.x * width +
                        vertScaleMarksOffsetSign * (verticalScaleMarkOffset.x + curOffset.x),
                centerAxisNorm.y * height + horScaleMarksOffsetSign * (horizontalScaleMarkOffset.y + curOffset.y));

        batch.end();
    }

    public void draw(Batch batch, ShapeRenderer renderer, Vector2 centerAxis, Vector2 centerAxisNorm, float[] scalesX, float[] scalesY,
                     float[] scalesXNorm, float[] scalesYNorm, int scalesXPow, int scalesYPow, int width, int height) {
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        drawAxis(renderer, centerAxisNorm, scalesXNorm, scalesYNorm, width, height);
        drawScaleMarks(batch, centerAxis, centerAxisNorm, scalesX, scalesY, scalesXNorm, scalesYNorm, scalesXPow, scalesYPow, width, height);

        Gdx.gl20.glDisable(GL20.GL_BLEND);
    }

    public void setAxisColor(Color color) {
        axisColor.set(color);
    }

    public void setAxisColor(float r, float g, float b, float a) {
        axisColor.set(r, g, b, a);
    }

    public void setAxisLineWidth(float lineWidth) {
        axisLineWidth = lineWidth;
    }

    public void setAxisScaleMarkLinesLength(float top, float bottom, float left, float right) {
        scaleMarkLineTopLength = top;
        scaleMarkLineBottomLength = bottom;
        scaleMarkLineLeftLength = left;
        scaleMarkLineRightLength = right;
    }

    public void setHorizontalScaleMarkOffset(Vector2 offset) {
        horizontalScaleMarkOffset.set(offset);
    }

    public void setHorizontalScaleMarkOffset(float x, float y) {
        horizontalScaleMarkOffset.set(x, y);
    }

    public void setVerticalScaleMarkOffset(Vector2 offset) {
        verticalScaleMarkOffset.set(offset);
    }

    public void setVerticalScaleMarkOffset(float x, float y) {
        verticalScaleMarkOffset.set(x, y);
    }

    public void setFontSize(int size) {
        font.dispose();
        fontParam.size = size;
        font = ResourceManager.getFont(fontParam);
    }

    public void setFontColor(Color color) {
        font.dispose();
        fontParam.color.set(color);
        font = ResourceManager.getFont(fontParam);
    }

    public void setFontColor(float r, float g, float b, float a) {
        font.dispose();
        fontParam.color.set(r, g, b, a);
        font = ResourceManager.getFont(fontParam);
    }

    public void setAxisNames(String xAxisName, String yAxisName) {
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
    }

    public void setAxisNamesPadding(float padding) {
        axisNamesPadding = padding;
    }

    public void setEqualAxisScaleMarks(boolean equal) {
        equalAxisScaleMarks = equal;
    }

    public boolean isEqualAxisScaleMarks() {
        return equalAxisScaleMarks;
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
