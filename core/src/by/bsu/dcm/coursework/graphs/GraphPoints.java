package by.bsu.dcm.coursework.graphs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class GraphPoints {
    public Vector2[] points;
    public String desription;
    public Color lineColor;
    public Color pointColor;
    public float lineWidth;
    public float pointSize;

    public GraphPoints() {
        lineColor = new Color(0.0f, 0.0f, 0.0f, 1.0f);
        pointColor = new Color(0.0f, 0.0f, 0.0f, 0.0f);
        lineWidth = 2.0f;
        pointSize = 2.0f;
    }
}
