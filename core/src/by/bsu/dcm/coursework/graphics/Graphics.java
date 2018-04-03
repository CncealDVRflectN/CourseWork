package by.bsu.dcm.coursework.graphics;

import by.bsu.dcm.coursework.AssetsManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;

public class Graphics {
    public enum AntiAliasing {
        noAA(), SSAA4(), FXAA(), SSAA4_FXAA()
    }

    private static final float FXAA_SPAN_MAX = 8.0f;
    private static final float FXAA_REDUCE_MUL = 1.0f / 8.0f;
    private static final float FXAA_REDUCE_MIN = 1.0f / 128.0f;
    private static final SpriteBatch BATCH = new SpriteBatch();

    public static TextureRegion calcDownsample4(TextureRegion texture) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, texture.getRegionWidth() / 2, texture.getRegionHeight() / 2, false);
        TextureRegion result;

        BATCH.setShader(AssetsManager.getGraphDownsample4Shader());
        BATCH.setProjectionMatrix(new Matrix4().setToOrtho2D(0.0f, 0.0f, texture.getRegionWidth(), texture.getRegionHeight()));
        BATCH.disableBlending();

        fbo.begin();

        Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);

        BATCH.begin();

        AssetsManager.getGraphDownsample4Shader().setUniformf("u_texSize", texture.getRegionWidth() / 2, texture.getRegionHeight() / 2);

        BATCH.draw(texture, 0.0f, 0.0f);

        BATCH.end();
        fbo.end();

        BATCH.enableBlending();

        result = new TextureRegion(fbo.getColorBufferTexture());
        result.flip(false, true);

        return result;
    }

    public static TextureRegion calcFXAA(TextureRegion texture) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, texture.getRegionWidth(), texture.getRegionHeight(), false);
        TextureRegion result;

        BATCH.setShader(AssetsManager.getGraphFXAAShader());
        BATCH.setProjectionMatrix(new Matrix4().setToOrtho2D(0.0f, 0.0f, texture.getRegionWidth(), texture.getRegionHeight()));
        BATCH.disableBlending();

        fbo.begin();

        Gdx.gl30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl30.glClear(GL30.GL_COLOR_BUFFER_BIT);

        BATCH.begin();

        AssetsManager.getGraphFXAAShader().setUniformf("u_texSize", texture.getRegionWidth(), texture.getRegionHeight());
        AssetsManager.getGraphFXAAShader().setUniformf("u_spanMax", FXAA_SPAN_MAX);
        AssetsManager.getGraphFXAAShader().setUniformf("u_reduceMul", FXAA_REDUCE_MUL);
        AssetsManager.getGraphFXAAShader().setUniformf("u_reduceMin", FXAA_REDUCE_MIN);

        BATCH.draw(texture, 0.0f, 0.0f);

        BATCH.end();
        BATCH.enableBlending();

        fbo.end();

        result = new TextureRegion(fbo.getColorBufferTexture());
        result.flip(false, true);

        return result;
    }
}
