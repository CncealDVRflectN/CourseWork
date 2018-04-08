package by.bsu.dcm.coursework;

import by.bsu.dcm.coursework.ui.ErrorDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public final class AssetsManager {
    private static ShaderProgram graphBlendShader;
    private static ShaderProgram graphDownsample4Shader;
    private static ShaderProgram graphFXAAShader;
    private static FreeTypeFontGenerator fontGenerator;
    private static FreeTypeFontParameter currentUIFontParam;
    private static Skin skinUI;
    private static ErrorDialog errorDialog;

    private AssetsManager() {
    }

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

    private static boolean compareFontParams(FreeTypeFontParameter current, FreeTypeFontParameter next) {
        return current.size == next.size && current.color.equals(next.color) && current.borderStraight == next.borderStraight &&
                current.flip == next.flip && current.genMipMaps == next.genMipMaps && current.incremental == next.incremental &&
                current.kerning == next.kerning && current.mono == next.mono && current.borderColor.equals(next.borderColor) &&
                current.borderGamma == next.borderGamma && current.borderWidth == next.borderWidth && current.characters.equals(next.characters) &&
                current.gamma == next.gamma && current.magFilter.equals(next.magFilter) && current.minFilter.equals(next.minFilter) &&
                current.hinting.equals(next.hinting) &&
                ((current.packer == null && next.packer == null) || (current.packer != null && current.packer.equals(next.packer))) &&
                current.renderCount == next.renderCount && current.shadowColor.equals(next.shadowColor) &&
                current.shadowOffsetX == next.shadowOffsetX && current.shadowOffsetY == next.shadowOffsetY &&
                current.spaceX == next.spaceX && current.spaceY == next.spaceY;
    }

    private static FreeTypeFontParameter copyOfFontParams(FreeTypeFontParameter params) {
        FreeTypeFontParameter copy = new FreeTypeFontParameter();

        copy.size = params.size;
        copy.color.set(params.color);
        copy.spaceX = params.spaceX;
        copy.spaceY = params.spaceY;
        copy.shadowOffsetX = params.shadowOffsetX;
        copy.shadowOffsetY = params.shadowOffsetY;
        copy.shadowColor.set(params.shadowColor);
        copy.renderCount = params.renderCount;
        copy.packer = params.packer;
        copy.hinting = params.hinting;
        copy.minFilter = params.minFilter;
        copy.magFilter = params.magFilter;
        copy.gamma = params.gamma;
        copy.characters = params.characters;
        copy.borderWidth = params.borderWidth;
        copy.borderGamma = params.borderGamma;
        copy.borderColor.set(params.borderColor);
        copy.mono = params.mono;
        copy.kerning = params.kerning;
        copy.incremental = params.incremental;
        copy.genMipMaps = params.genMipMaps;
        copy.flip = params.flip;
        copy.borderStraight = params.borderStraight;

        return copy;
    }

    public static Skin getSkinUI(FreeTypeFontParameter fontParameter) {
        if (skinUI == null) {
            skinUI = new Skin();
            currentUIFontParam = copyOfFontParams(fontParameter);
            skinUI.add("font", getFont(currentUIFontParam), BitmapFont.class);
            skinUI.addRegions(new TextureAtlas(Gdx.files.internal("uiskin/uiskin.atlas")));
            skinUI.load(Gdx.files.internal("uiskin/uiskin.json"));
        }
        if (!compareFontParams(currentUIFontParam, fontParameter)) {
            currentUIFontParam = copyOfFontParams(fontParameter);
            skinUI.add("font", getFont(currentUIFontParam), BitmapFont.class);
        }

        return skinUI;
    }

    public static ErrorDialog getErrorDialog() {
        if (errorDialog == null) {
            errorDialog = new ErrorDialog(getSkinUI(currentUIFontParam));
        }

        return errorDialog;
    }
}
