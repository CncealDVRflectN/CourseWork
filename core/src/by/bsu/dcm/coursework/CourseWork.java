package by.bsu.dcm.coursework;

import by.bsu.dcm.coursework.graphs.Graph;
import by.bsu.dcm.coursework.graphs.GraphPoints;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class CourseWork extends ApplicationAdapter {
	private SpriteBatch batch;
	private Graph graph;
	private TextureRegion graphResult;

	private Vector2[] exampleCircle() {
		Vector2[] result = new Vector2[37];
		double step = 2.0 * Math.PI / 36.0;
		double radius = 10.0;

		for (int i = 0; i < result.length - 1; i++) {
			result[i] = new Vector2((float)(radius * Math.cos(i * step)), (float)(radius * Math.sin(i * step)));
		}
		result[36] = result[0];

		return result;
	}

	private Vector2[] exampleSin() {
		Vector2[] result = new Vector2[49];
		float a = (float) -(Math.PI * 6);
		float b = (float) (Math.PI * 6);
		float step = (b - a) / 48.0f;

		for (int i = 0; i < result.length; i++) {
			result[i] = new Vector2(a + i * step, (float) Math.sin(a + i * step));
		}

		return result;
	}

	private Vector2[] exampleSquared() {
		Vector2[] result = new Vector2[31];
		float a = -5.0f;
		float b = 5.0f;
		float step = (b - a) / 30.0f;

		for (int i = 0; i < result.length; i++) {
			result[i] = new Vector2(a + i * step, (float) Math.pow(a + i * step, 2.0));
		}

		return result;
	}

	private void example() {
		GraphPoints circle = new GraphPoints();
		GraphPoints sin = new GraphPoints();
		GraphPoints squared = new GraphPoints();

		circle.points = exampleCircle();
		circle.pointSize = 3.0f;
		circle.pointColor.set(1.0f, 0.0f, 0.0f, 0.9f);
		circle.lineWidth = 2.0f;
		circle.lineColor.set(1.0f, 0.0f, 0.0f, 0.75f);

		sin.points = exampleSin();
		sin.pointSize = 3.0f;
		sin.pointColor.set(0.0f, 0.75f, 0.0f, 0.9f);
		sin.lineWidth = 2.0f;
		sin.lineColor.set(0.0f, 0.75f, 0.0f, 0.75f);

		squared.points = exampleSquared();
		squared.pointSize = 3.0f;
		squared.pointColor.set(0.0f, 0.0f, 1.0f, 0.9f);
		squared.lineWidth = 2.0f;
		squared.lineColor.set(0.0f, 0.0f, 1.0f, 0.75f);

		graph.addGraph(circle);
		graph.addGraph(sin);
		graph.addGraph(squared);
	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		graph = new Graph();

		example();

		graphResult = graph.getGraph(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void render () {
		Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.draw(graphResult, 0.0f, 0.0f);
		batch.end();
	}
	
	@Override
	public void dispose () {
	}
}
