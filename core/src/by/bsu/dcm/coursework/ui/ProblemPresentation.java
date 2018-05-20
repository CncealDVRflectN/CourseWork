package by.bsu.dcm.coursework.ui;

import by.bsu.dcm.coursework.ResourceManager;
import by.bsu.dcm.coursework.math.fluid.RelaxationParams;
import by.bsu.dcm.coursework.math.fluid.TargetBondException;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;

import static by.bsu.dcm.coursework.ResourceManager.UILanguage;
import static by.bsu.dcm.coursework.ui.PresentationWidget.Slide;
import static com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class ProblemPresentation extends Table implements Disposable {
    private static final float DEFAULT_BUTTON_WIDTH = 250.0f;

    private PresentationWidget presentation;

    private TextButton axisymmetricButton;
    private TextButton plainButton;
    private TextButton heightCoefButton;

    private UILanguage currentUILanguage;

    public ProblemPresentation(Skin skin) {
        presentation = new PresentationWidget();

        axisymmetricButton = new TextButton("", skin.get("presentation-default", TextButtonStyle.class));
        plainButton = new TextButton("", skin.get("presentation-default", TextButtonStyle.class));
        heightCoefButton = new TextButton("", skin.get("presentation-default", TextButtonStyle.class));

        setBackground(skin.getDrawable("presentation-background"));

        build();
    }

    private void build() {
        defaults().width(DEFAULT_BUTTON_WIDTH);
        add(axisymmetricButton);
        add(plainButton);
        add(heightCoefButton).row();
        add(presentation).width(0.0f).colspan(3).grow();

        axisymmetricButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                presentation.setCurrentSlide(Slide.Axisymmetric);
                axisymmetricButton.setStyle(getSkin().get("presentation-active", TextButtonStyle.class));
                plainButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
                heightCoefButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
            }
        });

        plainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                presentation.setCurrentSlide(Slide.Plain);
                axisymmetricButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
                plainButton.setStyle(getSkin().get("presentation-active", TextButtonStyle.class));
                heightCoefButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
            }
        });

        heightCoefButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                presentation.setCurrentSlide(Slide.HeightCoef);
                axisymmetricButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
                plainButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
                heightCoefButton.setStyle(getSkin().get("presentation-active", TextButtonStyle.class));
            }
        });
    }

    public void generatePresentation(RelaxationParams params) throws TargetBondException {
        presentation.generatePresentation(params);
    }

    public void setGraphsNum(int num) {
        presentation.setGraphsNum(num);
    }

    public void setEqualAxisScaleMarks(boolean equal) {
        presentation.setEqualAxisScaleMarks(equal);
    }

    public void setVolumeNondim(boolean nondim) {
        presentation.setVolumeNondim(nondim);
    }

    public void setGenerateButton(TextButton button) {
        presentation.setGenerateButton(button);
    }

    public void setLanguage(UILanguage language) {
        if (currentUILanguage != language) {
            axisymmetricButton.setText(ResourceManager.getBundle(language).get("axisymmetric"));
            plainButton.setText(ResourceManager.getBundle(language).get("plain"));
            heightCoefButton.setText(ResourceManager.getBundle(language).get("compressionRatiosButton"));

            presentation.setLanguage(language);

            currentUILanguage = language;
        }
    }

    public boolean isEqualAxisScaleMarks() {
        return presentation.isEqualAxisScaleMarks();
    }

    public boolean isVolumeNondim() {
        return presentation.isVolumeNondim();
    }

    @Override
    public void setSkin(Skin skin) {
        super.setSkin(skin);

        switch (presentation.getCurrentSlide()) {
            case Axisymmetric:
                axisymmetricButton.setStyle(getSkin().get("presentation-active", TextButtonStyle.class));
                plainButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
                heightCoefButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
                break;
            case Plain:
                axisymmetricButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
                plainButton.setStyle(getSkin().get("presentation-active", TextButtonStyle.class));
                heightCoefButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
                break;
            default:
                axisymmetricButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
                plainButton.setStyle(getSkin().get("presentation-default", TextButtonStyle.class));
                heightCoefButton.setStyle(getSkin().get("presentation-active", TextButtonStyle.class));
        }
    }

    public void resize(float widthMul) {
        getCell(axisymmetricButton).width(widthMul * DEFAULT_BUTTON_WIDTH);
        getCell(plainButton).width(widthMul * DEFAULT_BUTTON_WIDTH);
        getCell(heightCoefButton).width(widthMul * DEFAULT_BUTTON_WIDTH);

        presentation.resize();
    }

    @Override
    public void dispose() {
        presentation.dispose();
    }
}
