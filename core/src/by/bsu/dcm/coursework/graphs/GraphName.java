package by.bsu.dcm.coursework.graphs;

import by.bsu.dcm.coursework.ResourceManager;
import by.bsu.dcm.coursework.graphs.Graph.NameAlign;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

class GraphName implements Disposable {
    private String name;
    private FreeTypeFontParameter fontParam;
    private BitmapFont font;
    private Color backgroundColor;
    private Color borderLineColor;
    private float borderLineWidth;

    private float textPaddingTop;
    private float textPaddingRight;
    private float textPaddingBottom;
    private float textPaddingLeft;

    private float padding;

    GraphName() {
        fontParam = new FreeTypeFontParameter();
        fontParam.size = 18;
        fontParam.color = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        fontParam.characters = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRST\n" +
                "UVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        font = ResourceManager.getFont(fontParam);

        backgroundColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        borderLineColor = new Color(0.75f, 0.75f, 0.75f, 1.0f);
        borderLineWidth = 2.0f;

        textPaddingTop = 4.0f;
        textPaddingRight = 4.0f;
        textPaddingBottom = 4.0f;
        textPaddingLeft = 4.0f;

        padding = 10.0f;
    }

    public void draw(Batch batch, ShapeRenderer renderer, NameAlign align, int width, int height) {
        GlyphLayout layout = new GlyphLayout();
        Vector2 bottomLeft = new Vector2();
        Vector2 topRight = new Vector2();
        Vector2 rectSize = new Vector2();
        float linePadding = borderLineWidth / 2.0f;

        if (isEmpty()) {
            return;
        }

        renderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0.0f, 0.0f, width, height));

        layout.setText(font, name);

        rectSize.set(textPaddingLeft + layout.width + textPaddingRight, textPaddingBottom + layout.height + textPaddingTop);
        switch (align) {
            case Bottom:
                bottomLeft.set((width - rectSize.x) / 2.0f, padding + borderLineWidth);
                break;
            default:
                bottomLeft.set((width - rectSize.x) / 2.0f, height - padding - borderLineWidth - rectSize.y);
        }
        topRight.set(bottomLeft.x + rectSize.x, bottomLeft.y + rectSize.y);

        Gdx.gl30.glEnable(GL30.GL_BLEND);
        Gdx.gl30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        renderer.begin(ShapeType.Filled);
        renderer.setColor(backgroundColor);

        renderer.rect(bottomLeft.x, bottomLeft.y, rectSize.x, rectSize.y);

        renderer.end();

        batch.begin();

        font.draw(batch, name, bottomLeft.x + textPaddingLeft, bottomLeft.y + textPaddingBottom + layout.height);

        batch.end();

        Gdx.gl30.glLineWidth(borderLineWidth);

        renderer.begin(ShapeType.Line);
        renderer.setColor(borderLineColor);

        renderer.line(bottomLeft.x - linePadding, bottomLeft.y - linePadding,
                bottomLeft.x - linePadding, topRight.y + borderLineWidth);
        renderer.line(bottomLeft.x - borderLineWidth, topRight.y + linePadding,
                topRight.x + borderLineWidth, topRight.y + linePadding);
        renderer.line(topRight.x + linePadding, topRight.y + borderLineWidth,
                topRight.x + linePadding, bottomLeft.y - borderLineWidth);
        renderer.line(bottomLeft.x - borderLineWidth, bottomLeft.y - linePadding,
                topRight.x + borderLineWidth, bottomLeft.y - linePadding);

        renderer.end();

        Gdx.gl30.glLineWidth(1.0f);
        Gdx.gl30.glDisable(GL30.GL_BLEND);
    }

    private boolean isEmpty() {
        return name == null || name.isEmpty();
    }

    public void setName(String name) {
        this.name = name;
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

    public void setTextPadding(float top, float right, float bottom, float left) {
        textPaddingTop = top;
        textPaddingRight = right;
        textPaddingBottom = bottom;
        textPaddingLeft = left;
    }

    public void setPadding(float padding) {
        this.padding = padding;
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

    @Override
    public void dispose() {
        font.dispose();
    }
}
