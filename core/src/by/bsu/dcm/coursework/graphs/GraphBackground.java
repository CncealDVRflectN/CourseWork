package by.bsu.dcm.coursework.graphs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

class GraphBackground {
    private Color backgroundColor;
    private Color markupColor;
    private float markupLineWidth;

    GraphBackground() {
        backgroundColor = new Color();
        markupColor = new Color();
        markupLineWidth = 0.0f;
    }

    GraphBackground(Color backgroundColor, Color markupColor, float markupLineWidth) {
        this.backgroundColor = new Color(backgroundColor);
        this.markupColor = new Color(markupColor);
        this.markupLineWidth = markupLineWidth;
    }

    public void draw(ShapeRenderer renderer, float[] scalesXNorm, float[] scalesYNorm, int width, int height) {
        float tmp;

        Gdx.gl30.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, 1.0f);
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);
        Gdx.gl30.glEnable(GL30.GL_BLEND);
        Gdx.gl30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl30.glLineWidth(markupLineWidth);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(markupColor);

        for (float scale : scalesXNorm) {
            tmp = scale * width;
            renderer.line(tmp, 0.0f, tmp, height);
        }

        for (float scale : scalesYNorm) {
            tmp = scale * height;
            renderer.line(0.0f, tmp, width, tmp);
        }

        renderer.end();

        Gdx.gl30.glLineWidth(1.0f);
        Gdx.gl30.glDisable(GL30.GL_BLEND);
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

    public void setMarkupLineWidth(float lineWidth) {
        markupLineWidth = lineWidth;
    }
}
