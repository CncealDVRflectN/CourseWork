package by.bsu.dcm.coursework.ui;

import by.bsu.dcm.coursework.AssetsManager;
import by.bsu.dcm.coursework.graphs.GraphPoints;
import by.bsu.dcm.coursework.math.fluid.Axisymmetric;
import by.bsu.dcm.coursework.math.fluid.EquilibriumFluid;
import by.bsu.dcm.coursework.math.fluid.Plain;
import by.bsu.dcm.coursework.math.fluid.RelaxationParams;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import static by.bsu.dcm.coursework.graphics.Graphics.AntiAliasing;
import static com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import static com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import static com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import static com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

public class ProblemSettings extends Table {
    private final String[] antialiasings = {"No AA", "FXAA", "SSAA4", "SSAA4 + FXAA"};
    private final String[] problems = {"Axisymmetric", "Plain"};

    private final ProblemPresentation presentation;

    private final Label problemLabel;
    private final Label antialiasingLabel;
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
    private final SelectBox<String> problemSelect;
    private final SelectBox<String> antialiasingSelect;

    private EquilibriumFluid axisymmetric;
    private EquilibriumFluid plain;
    private EquilibriumFluid currentProblem;

    private GraphPoints axisymmetricPoints;
    private GraphPoints plainPoints;
    private GraphPoints currentPoints;

    public ProblemSettings(ProblemPresentation presentation, Skin skin) {
        super(skin);

        this.presentation = presentation;

        axisymmetric = new Axisymmetric();
        plain = new Plain();

        axisymmetricPoints = new GraphPoints();
        plainPoints = new GraphPoints();

        axisymmetricPoints.pointSize = 2.0f;
        axisymmetricPoints.pointColor.set(1.0f, 0.0f, 0.0f, 0.75f);
        axisymmetricPoints.lineWidth = 2.0f;
        axisymmetricPoints.lineColor.set(1.0f, 0.0f, 0.0f, 0.75f);

        plainPoints.pointSize = 2.0f;
        plainPoints.pointColor.set(0.0f, 0.0f, 1.0f, 0.75f);
        plainPoints.lineWidth = 2.0f;
        plainPoints.lineColor.set(0.0f, 0.0f, 1.0f, 0.75f);

        problemLabel = new Label("Select problem type:", skin);
        antialiasingLabel = new Label("Select antialiasing:", skin);
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
        problemSelect = new SelectBox<>(skin);
        antialiasingSelect = new SelectBox<>(skin);

        setBackground(new NinePatchDrawable(skin.getPatch("settings-background")));

        build();
    }

    private void build() {
        GlyphLayout layout = new GlyphLayout();

        layout.setText(generateButton.getStyle().font, generateButton.getText());

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

        defaults().pad(5.0f, 5.0f, 0.0f, 5.0f).expandX().fill();
        add(antialiasingLabel).row();
        add(antialiasingSelect).row();
        add(problemLabel).row();
        add(problemSelect).row();
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

        antialiasingSelect.setItems(antialiasings);
        problemSelect.setItems(problems);

        antialiasingSelect.setSelected("No AA");
        problemSelect.setSelected("Axisymmetric");
        currentProblem = axisymmetric;
        currentPoints = axisymmetricPoints;

        antialiasingSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!event.isHandled()) {
                    switch (antialiasingSelect.getSelected()) {
                        case "FXAA":
                            presentation.setAntialiasing(AntiAliasing.FXAA);
                            break;
                        case "SSAA4":
                            presentation.setAntialiasing(AntiAliasing.SSAA4);
                            break;
                        case "SSAA4 + FXAA":
                            presentation.setAntialiasing(AntiAliasing.SSAA4_FXAA);
                            break;
                        default:
                            presentation.setAntialiasing(AntiAliasing.noAA);
                    }
                }
            }
        });

        problemSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!event.isHandled()) {
                    switch (problemSelect.getSelected()) {
                        case "Axisymmetric":
                            currentProblem = axisymmetric;
                            currentPoints = axisymmetricPoints;
                            break;
                        case "Plain":
                            currentProblem = plain;
                            currentPoints = plainPoints;
                            break;
                        default:
                            currentProblem = axisymmetric;
                            currentPoints = axisymmetricPoints;
                    }
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
                    relaxationParams.bondTarget = Double.valueOf(bondTargetField.getText());
                    relaxationParams.bondStep = Double.valueOf(bondStepField.getText());
                    relaxationParams.relaxationCoefMin = Double.valueOf(relaxationCoefField.getText());
                    relaxationParams.epsilon = Double.valueOf(epsilonField.getText());
                    relaxationParams.splitNum = Integer.valueOf(splitNumField.getText());
                    currentProblem.setIterationsLimit(Integer.valueOf(maxIterNumField.getText()));

                    presentation.generatePresentation(currentProblem, relaxationParams, currentPoints);
                } catch (NumberFormatException e) {
                    errorDialog = AssetsManager.getErrorDialog();
                    getStage().addActor(errorDialog);
                    errorDialog.setMessage("Incorrect input");
                    errorDialog.toFront();
                    errorDialog.setVisible(true);
                } catch (Exception e) {
                    errorDialog = AssetsManager.getErrorDialog();
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
        LabelStyle labelStyle;
        TextFieldStyle fieldStyle;
        SelectBoxStyle selectStyle;
        TextButtonStyle buttonStyle;

        super.setSkin(skin);

        labelStyle = skin.get(LabelStyle.class);
        fieldStyle = skin.get(TextFieldStyle.class);
        selectStyle = skin.get(SelectBoxStyle.class);
        buttonStyle = skin.get(TextButtonStyle.class);

        problemLabel.setStyle(labelStyle);
        antialiasingLabel.setStyle(labelStyle);
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

        problemSelect.setStyle(selectStyle);
        antialiasingSelect.setStyle(selectStyle);

        generateButton.setStyle(buttonStyle);
    }

    public void resize() {
        GlyphLayout layout = new GlyphLayout();

        layout.setText(generateButton.getStyle().font, generateButton.getText());

        getCell(generateButton).width(2.0f * layout.width);
    }
}
