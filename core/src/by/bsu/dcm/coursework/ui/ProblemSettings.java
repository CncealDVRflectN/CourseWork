package by.bsu.dcm.coursework.ui;

import by.bsu.dcm.coursework.ResourceManager;
import by.bsu.dcm.coursework.math.fluid.RelaxationParams;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Scaling;

import static com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import static com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import static com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import static com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

public class ProblemSettings extends Table {
    private final ProblemPresentation presentation;

    private final CheckBox equalAxisScalesCheckBox;
    private final CheckBox volumeNondimCheckBox;
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

    public ProblemSettings(ProblemPresentation presentation, Skin skin) {
        super(skin);

        this.presentation = presentation;

        equalAxisScalesCheckBox = new CheckBox("Enable equal axis scales", skin);
        volumeNondimCheckBox = new CheckBox("Nondimentionalaze by volume", skin);
        alphaLabel = new Label("Contact angle:", skin);
        bondTargetLabel = new Label("Target Bond number:", skin);
        bondStepLabel = new Label("Relaxation Bond number step:", skin);
        relaxationCoefLabel = new Label("Minimal relaxation coeficient:", skin);
        epsilonLabel = new Label("Accuracy:", skin);
        splitNumLabel = new Label("Number of splits:", skin);
        maxIterNumLabel = new Label("Maximum number of iterations", skin);
        alphaField = new TextField("", skin);
        bondTargetField = new TextField("", skin);
        bondStepField = new TextField("", skin);
        relaxationCoefField = new TextField("", skin);
        epsilonField = new TextField("", skin);
        splitNumField = new TextField("", skin);
        maxIterNumField = new TextField("", skin);
        generateButton = new TextButton("Generate", skin);

        setBackground(new NinePatchDrawable(skin.getPatch("settings-background")));

        presentation.setGenerateButton(generateButton);

        build();
    }

    private void build() {
        GlyphLayout layout = new GlyphLayout();

        layout.setText(generateButton.getStyle().font, generateButton.getText());

        equalAxisScalesCheckBox.getImage().setScaling(Scaling.fit);
        volumeNondimCheckBox.getImage().setScaling(Scaling.fit);

        alphaField.setMessageText("Enter contact angle");
        bondTargetField.setMessageText("Enter target Bond number");
        bondStepField.setMessageText("Enter relaxation Bond number step");
        relaxationCoefField.setMessageText("Enter minimal relaxation coeficient");
        epsilonField.setMessageText("Enter accuracy");
        splitNumField.setMessageText("Enter number of splits");
        maxIterNumField.setMessageText("Enter maximum number of iterations");

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

        defaults().pad(5.0f, 5.0f, 0.0f, 5.0f).expandX().fill();
        add(equalAxisScalesCheckBox).size(ResourceManager.getCurrentUIFontParam().size, ResourceManager.getCurrentUIFontParam().size)
                .left().row();
        add(volumeNondimCheckBox).size(ResourceManager.getCurrentUIFontParam().size, ResourceManager.getCurrentUIFontParam().size).left().row();
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
        add(generateButton).center().pad(5.0f, 0.0f, 5.0f, 0.0f).width(2.0f * layout.width);

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

                try {
                    relaxationParams.alpha = Double.valueOf(alphaField.getText());
                    relaxationParams.targetBond = Double.valueOf(bondTargetField.getText());
                    relaxationParams.bondStep = Double.valueOf(bondStepField.getText());
                    relaxationParams.relaxationCoefMin = Double.valueOf(relaxationCoefField.getText());
                    relaxationParams.epsilon = Double.valueOf(epsilonField.getText());
                    relaxationParams.splitNum = Integer.valueOf(splitNumField.getText());

                    presentation.generatePresentation(relaxationParams);
                } catch (NumberFormatException e) {
                    errorDialog = ResourceManager.getErrorDialog();
                    getStage().addActor(errorDialog);
                    errorDialog.setMessage("Incorrect input");
                    errorDialog.toFront();
                    errorDialog.setVisible(true);
                } catch (Exception e) {
                    errorDialog = ResourceManager.getErrorDialog();
                    getStage().addActor(errorDialog);
                    errorDialog.setMessage(e.toString());
                    errorDialog.toFront();
                    errorDialog.setVisible(true);

                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void setSkin(Skin skin) {
        CheckBoxStyle checkBoxStyle;
        LabelStyle labelStyle;
        TextFieldStyle fieldStyle;
        TextButtonStyle buttonStyle;

        super.setSkin(skin);

        checkBoxStyle = skin.get(CheckBoxStyle.class);
        labelStyle = skin.get(LabelStyle.class);
        fieldStyle = skin.get(TextFieldStyle.class);
        buttonStyle = skin.get(TextButtonStyle.class);

        equalAxisScalesCheckBox.setStyle(checkBoxStyle);
        volumeNondimCheckBox.setStyle(checkBoxStyle);
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

    public void resize() {
        GlyphLayout layout = new GlyphLayout();

        layout.setText(generateButton.getStyle().font, generateButton.getText());

        getCell(generateButton).width(2.0f * layout.width);
        getCell(equalAxisScalesCheckBox).width(ResourceManager.getCurrentUIFontParam().size).height(ResourceManager.getCurrentUIFontParam().size);
        getCell(volumeNondimCheckBox).width(ResourceManager.getCurrentUIFontParam().size).height(ResourceManager.getCurrentUIFontParam().size);
        equalAxisScalesCheckBox.getImageCell().size(ResourceManager.getCurrentUIFontParam().size, ResourceManager.getCurrentUIFontParam().size);
        volumeNondimCheckBox.getImageCell().size(ResourceManager.getCurrentUIFontParam().size).height(ResourceManager.getCurrentUIFontParam().size);
    }
}
