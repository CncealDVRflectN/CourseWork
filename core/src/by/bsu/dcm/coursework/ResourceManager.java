package by.bsu.dcm.coursework;

import by.bsu.dcm.coursework.ui.ErrorDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class ResourceManager {
    public enum UILanguage {
        English, Russian
    }

    public static final Executor THREAD_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static FreeTypeFontGenerator fontGenerator;
    private static FreeTypeFontParameter currentUIFontParam;
    private static Skin skinUI;
    private static ErrorDialog errorDialog;
    private static UILanguage currentUILang;
    private static I18NBundle currentBundle;

    private ResourceManager() {
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
                current.mono == next.mono && current.borderColor.equals(next.borderColor) &&
                current.borderGamma == next.borderGamma && current.borderWidth == next.borderWidth &&
                current.gamma == next.gamma && current.magFilter.equals(next.magFilter) && current.minFilter.equals(next.minFilter) &&
                current.hinting.equals(next.hinting) &&
                ((current.packer == null && next.packer == null) || (current.packer != null && current.packer.equals(next.packer))) &&
                current.renderCount == next.renderCount && current.shadowColor.equals(next.shadowColor) &&
                current.shadowOffsetX == next.shadowOffsetX && current.shadowOffsetY == next.shadowOffsetY &&
                current.spaceX == next.spaceX && current.spaceY == next.spaceY;
    }

    private static void copyFontParams(FreeTypeFontParameter params) {
        currentUIFontParam.size = params.size;
        currentUIFontParam.color.set(params.color);
        currentUIFontParam.spaceX = params.spaceX;
        currentUIFontParam.spaceY = params.spaceY;
        currentUIFontParam.shadowOffsetX = params.shadowOffsetX;
        currentUIFontParam.shadowOffsetY = params.shadowOffsetY;
        currentUIFontParam.shadowColor.set(params.shadowColor);
        currentUIFontParam.renderCount = params.renderCount;
        currentUIFontParam.packer = params.packer;
        currentUIFontParam.hinting = params.hinting;
        currentUIFontParam.minFilter = params.minFilter;
        currentUIFontParam.magFilter = params.magFilter;
        currentUIFontParam.gamma = params.gamma;
        currentUIFontParam.borderWidth = params.borderWidth;
        currentUIFontParam.borderGamma = params.borderGamma;
        currentUIFontParam.borderColor.set(params.borderColor);
        currentUIFontParam.mono = params.mono;
        currentUIFontParam.kerning = params.kerning;
        currentUIFontParam.incremental = params.incremental;
        currentUIFontParam.genMipMaps = params.genMipMaps;
        currentUIFontParam.flip = params.flip;
        currentUIFontParam.borderStraight = params.borderStraight;
    }

    public static Skin getSkinUI(FreeTypeFontParameter fontParameter) {
        if (skinUI == null || !compareFontParams(currentUIFontParam, fontParameter)) {
            if (skinUI != null) {
                skinUI.dispose();
            }
            skinUI = new Skin();

            if (currentUIFontParam == null) {
                currentUIFontParam = new FreeTypeFontParameter();
            }

            copyFontParams(fontParameter);
            currentUIFontParam.characters = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRST\n" +
                    "UVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~ АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";

            skinUI.add("font", getFont(currentUIFontParam), BitmapFont.class);
            skinUI.addRegions(new TextureAtlas(Gdx.files.internal("uiskin/uiskin.atlas")));
            skinUI.load(Gdx.files.internal("uiskin/uiskin.json"));
        }

        return skinUI;
    }

    public static FreeTypeFontParameter getCurrentUIFontParam() {
        return currentUIFontParam;
    }

    public static I18NBundle getBundle(UILanguage language) {
        if (language != null && currentUILang != language) {
            switch (language) {
                case Russian:
                    currentBundle = I18NBundle.createBundle(Gdx.files.internal("i18n/Bundle"), new Locale("ru", "", ""));
                    break;
                default:
                    currentBundle = I18NBundle.createBundle(Gdx.files.internal("i18n/Bundle"), new Locale("", "", ""));
            }

            currentUILang = language;
        }

        return currentBundle;
    }

    public static synchronized ErrorDialog getErrorDialog() {
        if (errorDialog == null) {
            errorDialog = new ErrorDialog(getSkinUI(currentUIFontParam));
        }

        return errorDialog;
    }
}
