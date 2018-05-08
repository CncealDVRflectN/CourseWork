package by.bsu.dcm.coursework.ui;

import by.bsu.dcm.coursework.ResourceManager;
import by.bsu.dcm.coursework.math.fluid.RelaxationParams;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Scaling;

import static by.bsu.dcm.coursework.ResourceManager.*;
import static com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import static com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import static com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import static com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import static com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

public class ProblemSettings extends Table {
    private static final float DEFAULT_BUTTON_WIDTH = 150.0f;
    private static final float DEFAULT_PARAMS_OFFSET = 100.0f;

    private final ProblemPresentation presentation;

    private final SelectBox<String> languageSelectBox;
    private final CheckBox equalAxisScalesCheckBox;
    private final CheckBox volumeNondimCheckBox;
    private final Label languageLabel;
    private final Label alphaLabel;
    private final Label bondTargetLabel;
    private final Label bondStepLabel;
    private final Label relaxationCoefLabel;
    private final Label epsilonLabel;
    private final Label splitNumLabel;
    private final Label maxIterNumLabel;
    private final TextField alphaField;
    private final TextField bondTargetField;
    private final TextField bondStepField;
    private final TextField relaxationCoefField;
    private final TextField epsilonField;
    private final TextField splitNumField;
    private final TextField maxIterNumField;
    private final TextButton generateButton;

    private UILanguage currentUILanguage;

    public ProblemSettings(ProblemPresentation presentation, Skin skin) {
        super(skin);

        this.presentation = presentation;

        languageSelectBox = new SelectBox<>(skin);
        equalAxisScalesCheckBox = new CheckBox("", skin);
        volumeNondimCheckBox = new CheckBox("", skin);
        languageLabel = new Label("", skin);
        alphaLabel = new Label("", skin);
        bondTargetLabel = new Label("", skin);
        bondStepLabel = new Label("", skin);
        relaxationCoefLabel = new Label("", skin);
        epsilonLabel = new Label("", skin);
        splitNumLabel = new Label("", skin);
        maxIterNumLabel = new Label("", skin);
        alphaField = new TextField("", skin);
        bondTargetField = new TextField("", skin);
        bondStepField = new TextField("", skin);
        relaxationCoefField = new TextField("", skin);
        epsilonField = new TextField("", skin);
        splitNumField = new TextField("", skin);
        maxIterNumField = new TextField("", skin);
        generateButton = new TextButton("", skin);

        setLanguage(UILanguage.English);

        setBackground(new NinePatchDrawable(skin.getPatch("settings-background")));

        presentation.setGenerateButton(generateButton);

        build();
    }

    private void build() {
        languageSelectBox.setItems("English", "Русский");
        languageSelectBox.setSelected("English");

        equalAxisScalesCheckBox.getImage().setScaling(Scaling.fit);
        volumeNondimCheckBox.getImage().setScaling(Scaling.fit);

        alphaField.setTextFieldFilter(new FloatFieldFilter(false));
        bondTargetField.setTextFieldFilter(new FloatFieldFilter(true));
        bondStepField.setTextFieldFilter(new FloatFieldFilter(true));
        relaxationCoefField.setTextFieldFilter(new FloatFieldFilter(true));
        epsilonField.setTextFieldFilter(new FloatFieldFilter(true));
        splitNumField.setTextFieldFilter(new IntegerFieldFilter(true));
        maxIterNumField.setTextFieldFilter(new IntegerFieldFilter(true));

        equalAxisScalesCheckBox.getImageCell().spaceRight(5.0f);
        equalAxisScalesCheckBox.left();
        volumeNondimCheckBox.getImageCell().spaceRight(5.0f);
        volumeNondimCheckBox.left();

        top();

        defaults().pad(5.0f, 5.0f, 0.0f, 5.0f).center().expandX().fill();
        add(languageLabel).top().row();
        add(languageSelectBox).padBottom(DEFAULT_PARAMS_OFFSET).top().row();
        add(equalAxisScalesCheckBox).size(getCurrentUIFontParam().size, getCurrentUIFontParam().size)
                .left().row();
        add(volumeNondimCheckBox).size(getCurrentUIFontParam().size, getCurrentUIFontParam().size).left().row();
        add(alphaLabel).row();
        add(alphaField).row();
        add(bondTargetLabel).row();
        add(bondTargetField).row();
        add(bondStepLabel).row();
        add(bondStepField).row();
        add(relaxationCoefLabel).row();
        add(relaxationCoefField).row();
        add(epsilonLabel).row();
        add(epsilonField).row();
        add(splitNumLabel).row();
        add(splitNumField).row();
        add(maxIterNumLabel).row();
        add(maxIterNumField).row();
        add(generateButton).center().pad(5.0f, 0.0f, 5.0f, 0.0f).width(DEFAULT_BUTTON_WIDTH);

        languageSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!event.isHandled()) {
                    switch (languageSelectBox.getSelected()) {
                        case "Русский":
                            setLanguage(UILanguage.Russian);
                            break;
                        default:
                            setLanguage(UILanguage.English);
                    }
                }
            }
        });

        equalAxisScalesCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!event.isHandled()) {
                    presentation.setEqualAxisScaleMarks(!presentation.isEqualAxisScaleMarks());
                }
            }
        });

        volumeNondimCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!event.isHandled()) {
                    presentation.setVolumeNondim(!presentation.isVolumeNondim());
                }
            }
        });

        generateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ErrorDialog errorDialog;
                RelaxationParams relaxationParams = new RelaxationParams();

                if (!generateButton.isDisabled()) {
                    try {
                        relaxationParams.alpha = Math.PI * Double.valueOf(alphaField.getText()) / 180.0;
                        relaxationParams.targetBond = Double.valueOf(bondTargetField.getText());
                        relaxationParams.bondStep = Double.valueOf(bondStepField.getText());
                        relaxationParams.relaxationCoefMin = Double.valueOf(relaxationCoefField.getText());
                        relaxationParams.epsilon = Double.valueOf(epsilonField.getText());
                        relaxationParams.splitNum = Integer.valueOf(splitNumField.getText());

                        presentation.generatePresentation(relaxationParams);
                    } catch (NumberFormatException e) {
                        errorDialog = getErrorDialog();
                        getStage().addActor(errorDialog);
                        errorDialog.setMessage(ResourceManager.getBundle(currentUILanguage).get("incorrectInputMessage"));
                        errorDialog.toFront();
                        errorDialog.setVisible(true);
                    } catch (Exception e) {
                        errorDialog = getErrorDialog();
                        getStage().addActor(errorDialog);
                        errorDialog.setMessage(e.toString());
                        errorDialog.toFront();
                        errorDialog.setVisible(true);

                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setLanguage(UILanguage language) {
        if (currentUILanguage != language) {
            equalAxisScalesCheckBox.setText(ResourceManager.getBundle(language).get("equalAxisScalesCheckBox"));
            volumeNondimCheckBox.setText(ResourceManager.getBundle(language).get("volumeNondimCheckBox"));

            languageLabel.setText(ResourceManager.getBundle(language).get("languageLabel"));
            alphaLabel.setText(ResourceManager.getBundle(language).get("alphaLabel"));
            bondTargetLabel.setText(ResourceManager.getBundle(language).get("bondTargetLabel"));
            bondStepLabel.setText(ResourceManager.getBundle(language).get("bondStepLabel"));
            relaxationCoefLabel.setText(ResourceManager.getBundle(language).get("relaxationCoefLabel"));
            epsilonLabel.setText(ResourceManager.getBundle(language).get("epsilonLabel"));
            splitNumLabel.setText(ResourceManager.getBundle(language).get("splitNumLabel"));
            maxIterNumLabel.setText(ResourceManager.getBundle(language).get("maxIterNumLabel"));

            alphaField.setMessageText(ResourceManager.getBundle(language).get("alphaFieldMessage"));
            bondTargetField.setMessageText(ResourceManager.getBundle(language).get("bondTargetFieldMessage"));
            bondStepField.setMessageText(ResourceManager.getBundle(language).get("bondStepFieldMessage"));
            relaxationCoefField.setMessageText(ResourceManager.getBundle(language).get("relaxationCoefFieldMessage"));
            epsilonField.setMessageText(ResourceManager.getBundle(language).get("epsilonFieldMessage"));
            splitNumField.setMessageText(ResourceManager.getBundle(language).get("splitNumFieldMessage"));
            maxIterNumField.setMessageText(ResourceManager.getBundle(language).get("maxIterNumFieldMessage"));

            generateButton.setText(ResourceManager.getBundle(language).get("generateButton"));

            presentation.setLanguage(language);

            currentUILanguage = language;
        }
    }

    @Override
    public void setSkin(Skin skin) {
        SelectBoxStyle selectBoxStyle;
        CheckBoxStyle checkBoxStyle;
        LabelStyle labelStyle;
        TextFieldStyle fieldStyle;
        TextButtonStyle buttonStyle;

        super.setSkin(skin);

        selectBoxStyle = skin.get(SelectBoxStyle.class);
        checkBoxStyle = skin.get(CheckBoxStyle.class);
        labelStyle = skin.get(LabelStyle.class);
        fieldStyle = skin.get(TextFieldStyle.class);
        buttonStyle = skin.get(TextButtonStyle.class);

        languageSelectBox.setStyle(selectBoxStyle);
        equalAxisScalesCheckBox.setStyle(checkBoxStyle);
        volumeNondimCheckBox.setStyle(checkBoxStyle);
        languageLabel.setStyle(labelStyle);
        alphaLabel.setStyle(labelStyle);
        bondTargetLabel.setStyle(labelStyle);
        bondStepLabel.setStyle(labelStyle);
        relaxationCoefLabel.setStyle(labelStyle);
        epsilonLabel.setStyle(labelStyle);
        splitNumLabel.setStyle(labelStyle);
        maxIterNumLabel.setStyle(labelStyle);

        alphaField.setStyle(fieldStyle);
        bondTargetField.setStyle(fieldStyle);
        bondStepField.setStyle(fieldStyle);
        relaxationCoefField.setStyle(fieldStyle);
        epsilonField.setStyle(fieldStyle);
        splitNumField.setStyle(fieldStyle);
        maxIterNumField.setStyle(fieldStyle);

        generateButton.setStyle(buttonStyle);
    }

    public void resize(float widthMul, float heightMul) {
        getCell(languageSelectBox).padBottom(heightMul * DEFAULT_PARAMS_OFFSET);
        getCell(generateButton).width(widthMul * DEFAULT_BUTTON_WIDTH);
        getCell(equalAxisScalesCheckBox).width(getCurrentUIFontParam().size).height(getCurrentUIFontParam().size);
        getCell(volumeNondimCheckBox).width(getCurrentUIFontParam().size).height(getCurrentUIFontParam().size);
        equalAxisScalesCheckBox.getImageCell().size(getCurrentUIFontParam().size, getCurrentUIFontParam().size);
        volumeNondimCheckBox.getImageCell().size(getCurrentUIFontParam().size).height(getCurrentUIFontParam().size);
    }
}
