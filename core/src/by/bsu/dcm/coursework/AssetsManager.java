package by.bsu.dcm.coursework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public final class AssetsManager {
    private static ShaderProgram graphBlendShader;
    private static ShaderProgram graphDownsample4Shader;
    private static ShaderProgram graphFXAAShader;
    private static FreeTypeFontGenerator fontGenerator;

    private AssetsManager() {}

    public static ShaderProgram getGraphBlendShader() {
        if (graphBlendShader == null) {
            graphBlendShader = new ShaderProgram(Gdx.files.internal("shaders/default.vert"),
                    Gdx.files.internal("shaders/blend-graph.frag"));

            if (!graphBlendShader.isCompiled()) {
                System.err.println("Graph blending shader");
                System.err.println(graphBlendShader.getLog());
            }
        }

        return graphBlendShader;
    }

    public static ShaderProgram getGraphDownsample4Shader() {
        if (graphDownsample4Shader == null) {
            graphDownsample4Shader = new ShaderProgram(Gdx.files.internal("shaders/default.vert"),
                    Gdx.files.internal("shaders/downsample4-graph.frag"));

            if (!graphDownsample4Shader.isCompiled()) {
                System.err.println("Graph downsampling4 shader");
                System.err.println(graphDownsample4Shader.getLog());
            }
        }

        return graphDownsample4Shader;
    }

    public static ShaderProgram getGraphFXAAShader() {
        if (graphFXAAShader == null) {
            graphFXAAShader = new ShaderProgram(Gdx.files.internal("shaders/default.vert"),
                    Gdx.files.internal("shaders/fxaa-alpha-graph.frag"));

            if (!graphFXAAShader.isCompiled()) {
                System.err.println("Graph FXAA alpha shader");
                System.err.println(graphFXAAShader.getLog());
            }
        }

        return graphFXAAShader;
    }

    public static BitmapFont getFont(FreeTypeFontParameter parameter) {
        if (fontGenerator == null) {
            fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/GentiumPlus-R.ttf"));
        }

        return fontGenerator.generateFont(parameter);
    }
}
