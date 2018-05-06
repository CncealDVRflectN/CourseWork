package by.bsu.dcm.coursework.graphs;

import by.bsu.dcm.coursework.ResourceManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
        fontParam.size = 15;
        fontParam.color = new Color(0.0f, 0.0f, 0.0f, 1.0f);

        font = ResourceManager.getFont(fontParam);

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

    private void drawBackground(ShapeRenderer renderer, List<GraphPoints> graphs, Vector2 bottomLeft, Vector2 rectSize, float lineHeight) {
        Vector2 currentLine = new Vector2();

        renderer.begin(ShapeType.Filled);
        renderer.setColor(backgroundColor);

        renderer.rect(bottomLeft.x, bottomLeft.y, rectSize.x, rectSize.y);

        currentLine.set(bottomLeft.x + paddingLeft, bottomLeft.y + paddingBottom);
        for (GraphPoints graph : graphs) {
            renderer.setColor(graph.pointColor);
            renderer.circle(currentLine.x + lineHeight / 2.0f, currentLine.y + lineHeight / 2.0f, lineHeight / 2.0f);
            currentLine.y += lineHeight + spacingVertical;
        }

        renderer.end();
    }

    private void drawText(Batch batch, List<GraphPoints> graphs, Vector2 bottomLeft, float lineHeight) {
        Vector2 currentLine = new Vector2();

        batch.begin();

        currentLine.set(bottomLeft.x + paddingLeft + lineHeight + spacingHorizontal, bottomLeft.y + paddingBottom);
        for (GraphPoints graph : graphs) {
            font.draw(batch, graph.desription, currentLine.x, currentLine.y + lineHeight);
            currentLine.y += lineHeight + spacingVertical;
        }

        batch.end();
    }

    private void drawBorder(ShapeRenderer renderer, Vector2 bottomLeft, Vector2 rectSize) {
        Vector2 topRight = new Vector2(bottomLeft.x + rectSize.x, bottomLeft.y + rectSize.y);
        float linePadding = borderLineWidth / 2.0f;

        Gdx.gl30.glLineWidth(borderLineWidth);

        renderer.begin(ShapeType.Line);
        renderer.setColor(borderLineColor);

        renderer.line(bottomLeft.x - linePadding, bottomLeft.y - linePadding,
                bottomLeft.x - linePadding, topRight.y + borderLineWidth);
        renderer.line(bottomLeft.x - borderLineWidth, topRight.y + linePadding,
                topRight.x + borderLineWidth, topRight.y + linePadding);
        renderer.line(topRight.x + linePadding, bottomLeft.y - borderLineWidth,
                topRight.x + linePadding, topRight.y + borderLineWidth);
        renderer.line(bottomLeft.x - borderLineWidth, bottomLeft.y - linePadding,
                topRight.x + borderLineWidth, bottomLeft.y - linePadding);

        renderer.end();

        Gdx.gl30.glLineWidth(1.0f);
    }

    public void draw(Batch batch, ShapeRenderer renderer, DescriptionAlign align, List<GraphPoints> graphs, int width, int height) {
        GlyphLayout layout = new GlyphLayout();
        Vector2 bottomLeft = new Vector2();
        Vector2 rectSize = new Vector2();
        float maxWidth = Float.NEGATIVE_INFINITY;
        float lineHeight = Float.NEGATIVE_INFINITY;

        if (isEmpty(graphs)) {
            return;
        }

        for (GraphPoints graph : graphs) {
            layout.setText(font, graph.desription);
            maxWidth = (layout.width > maxWidth) ? layout.width : maxWidth;
            lineHeight = (layout.height > lineHeight) ? layout.height : lineHeight;
        }

        rectSize.set(paddingLeft + lineHeight + spacingHorizontal + maxWidth + paddingRight,
                paddingBottom + (graphs.size() - 1) * spacingVertical + graphs.size() * lineHeight + paddingTop);
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

        Gdx.gl30.glEnable(GL30.GL_BLEND);
        Gdx.gl30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        drawBackground(renderer, graphs, bottomLeft, rectSize, lineHeight);
        drawText(batch, graphs, bottomLeft, lineHeight);
        drawBorder(renderer, bottomLeft, rectSize);

        Gdx.gl30.glDisable(GL30.GL_BLEND);
    }

    private boolean isEmpty(List<GraphPoints> graphs) {
        for (GraphPoints graph : graphs) {
            if (graph.desription != null || !graph.desription.isEmpty()) {
                return false;
            }
        }
        return true;
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
