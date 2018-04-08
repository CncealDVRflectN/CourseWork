package by.bsu.dcm.coursework;

import by.bsu.dcm.coursework.math.Axisymmetric;
import by.bsu.dcm.coursework.math.Flat;
import by.bsu.dcm.coursework.ui.ProblemUI;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CourseWork extends ApplicationAdapter {
    private Stage stage;
    private ProblemUI problemUI;

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        problemUI = new ProblemUI(new Axisymmetric(), new Flat());

        Gdx.input.setInputProcessor(stage);

        stage.addActor(problemUI);
    }

    @Override
    public void render() {
        Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        problemUI.resize(width, height);
    }

    @Override
    public void dispose() {
    }
}
