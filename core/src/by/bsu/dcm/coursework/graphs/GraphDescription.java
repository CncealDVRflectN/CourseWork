package by.bsu.dcm.coursework.graphs;

import by.bsu.dcm.coursework.AssetsManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import java.util.List;

import static by.bsu.dcm.coursework.graphs.Graph.DescriptionAlign;
import static com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

class GraphDescription implements Disposable {
    private FreeTypeFontParameter fontParam;
    private BitmapFont font;
    private Color backgroundColor;
    private Color borderLineColor;
    private float borderLineWidth;

    private float paddingTop;
    private float paddingRight;
    private float paddingBottom;
    private float paddingLeft;
    private float spacingHorizontal;
    private float spacingVertical;

    GraphDescription() {
        fontParam = new FreeTypeFontParameter();
        fontParam.size = 12;
        fontParam.color = new Color(0.0f, 0.0f, 0.0f, 1.0f);

        font = AssetsManager.getFont(fontParam);

        backgroundColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        borderLineColor = new Color(0.75f, 0.75f, 0.75f, 1.0f);
        borderLineWidth = 2.0f;

        paddingTop = 4.0f;
        paddingRight = 4.0f;
        paddingBottom = 4.0f;
        paddingLeft = 4.0f;
        spacingHorizontal = 4.0f;
        spacingVertical = 4.0f;
    }

    private void drawBackground(ShapeRenderer renderer, List<GraphPoints> graphs, Vector2 bottomLeft, Vector2 rectSize) {
        Vector2 currentLine = new Vector2();

        renderer.begin(ShapeType.Filled);
        renderer.setColor(backgroundColor);

        renderer.rect(bottomLeft.x, bottomLeft.y, rectSize.x, rectSize.y);

        currentLine.set(bottomLeft.x + paddingLeft, bottomLeft.y + paddingBottom);
        for (GraphPoints graph : graphs) {
            renderer.setColor(graph.pointColor);
            renderer.circle(currentLine.x + fontParam.size / 2.0f, currentLine.y + fontParam.size / 2.0f, fontParam.size / 2.0f);
            currentLine.y += fontParam.size + spacingVertical;
        }

        renderer.end();
    }

    private void drawText(Batch batch, List<GraphPoints> graphs, Vector2 bottomLeft) {
        Vector2 currentLine = new Vector2();

        batch.begin();

        currentLine.set(bottomLeft.x + paddingLeft + fontParam.size + spacingHorizontal, bottomLeft.y + paddingBottom);
        for (GraphPoints graph : graphs) {
            font.draw(batch, graph.desription, currentLine.x, currentLine.y + 3.0f * fontParam.size / 4.0f);
            currentLine.y += fontParam.size + spacingVertical;
        }

        batch.end();
    }

    private void drawBorder(ShapeRenderer renderer, Vector2 bottomLeft, Vector2 rectSize) {
        Vector2 topRight = new Vector2(bottomLeft.x + rectSize.x, bottomLeft.y + rectSize.y);
        float linePadding = borderLineWidth / 2.0f;

        Gdx.gl30.glLineWidth(borderLineWidth);

        renderer.begin(ShapeType.Line);
        renderer.setColor(borderLineColor);

        renderer.line(bottomLeft.x - linePadding, bottomLeft.y - linePadding, bottomLeft.x - linePadding, topRight.y + linePadding);
        renderer.line(bottomLeft.x - linePadding, topRight.y + linePadding, topRight.x + linePadding, topRight.y + linePadding);
        renderer.line(topRight.x + linePadding, bottomLeft.y - linePadding, topRight.x + linePadding, topRight.y + linePadding);
        renderer.line(bottomLeft.x - linePadding, bottomLeft.y - linePadding, topRight.x + linePadding, bottomLeft.y - linePadding);

        renderer.end();

        Gdx.gl30.glLineWidth(1.0f);
    }

    public void draw(Batch batch, ShapeRenderer renderer, DescriptionAlign align, List<GraphPoints> graphs, int width, int height) {
        GlyphLayout layout = new GlyphLayout();
        Vector2 bottomLeft = new Vector2();
        Vector2 rectSize = new Vector2();
        float maxWidth = Float.NEGATIVE_INFINITY;

        renderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0.0f, 0.0f, width, height));

        for (GraphPoints graph : graphs) {
            layout.setText(font, graph.desription);
            maxWidth = (layout.width > maxWidth) ? layout.width : maxWidth;
        }

        rectSize.set(paddingLeft + fontParam.size + spacingHorizontal + maxWidth + paddingRight,
                paddingBottom + (graphs.size() - 1) * spacingVertical + graphs.size() * fontParam.size + paddingTop);
        switch (align) {
            case TopLeft:
                bottomLeft.set(borderLineWidth, height - borderLineWidth - rectSize.y);
                break;
            case TopRight:
                bottomLeft.set(width - borderLineWidth - rectSize.x, height - borderLineWidth - rectSize.y);
                break;
            case BottomRight:
                bottomLeft.set(width - borderLineWidth - rectSize.x, borderLineWidth);
                break;
            default:
                bottomLeft.set(borderLineWidth, borderLineWidth);
        }

        drawBackground(renderer, graphs, bottomLeft, rectSize);
        drawText(batch, graphs, bottomLeft);
        drawBorder(renderer, bottomLeft, rectSize);
    }

    public void setBackgroundColor(Color color) {
        backgroundColor.set(color);
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        backgroundColor.set(r, g, b, a);
    }

    public void setBorderLineColor(Color color) {
        borderLineColor.set(color);
    }

    public void setBorderLineColor(float r, float g, float b, float a) {
        borderLineColor.set(r, g, b, a);
    }

    public void setBorderLineWidth(float lineWidth) {
        borderLineWidth = lineWidth;
    }

    public void setFontSize(int size) {
        font.dispose();
        fontParam.size = size;
        font = AssetsManager.getFont(fontParam);
    }

    public void setFontColor(Color color) {
        font.dispose();
        fontParam.color.set(color);
        font = AssetsManager.getFont(fontParam);
    }

    public void setFontColor(float r, float g, float b, float a) {
        font.dispose();
        fontParam.color.set(r, g, b, a);
        font = AssetsManager.getFont(fontParam);
    }

    public void setPadding(float top, float right, float bottom, float left) {
        paddingTop = top;
        paddingRight = right;
        paddingBottom = bottom;
        paddingLeft = left;
    }

    public void setSpacing(float horizontal, float vertical) {
        spacingHorizontal = horizontal;
        spacingVertical = vertical;
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
