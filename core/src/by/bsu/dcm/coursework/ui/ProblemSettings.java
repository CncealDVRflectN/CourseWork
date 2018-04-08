package by.bsu.dcm.coursework.ui;

import by.bsu.dcm.coursework.AssetsManager;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import static by.bsu.dcm.coursework.graphics.Graphics.AntiAliasing;

public class ProblemSettings extends Table {
    private final String[] antialiasings = {"No AA", "FXAA", "SSAA4", "SSAA4 + FXAA"};
    private final String[] problems = {"Axisymmetric", "Flat", "Axisymmetric + Flat"};

    private final ProblemGraph problemGraph;

    private final Label problemLabel;
    private final Label antialiasingLabel;
    private final Label alphaLabel;
    private final Label bondLabel;
    private final Label epsilonLabel;
    private final Label splitNumLabel;
    private final TextField alphaField;
    private final TextField bondField;
    private final TextField epsilonField;
    private final TextField splitNumField;
    private final TextButton generateButton;
    private final SelectBox<String> problemSelect;
    private final SelectBox<String> antialiasingSelect;

    public ProblemSettings(ProblemGraph problemGraph, Skin skin) {
        super(skin);

        this.problemGraph = problemGraph;

        problemLabel = new Label("Select problem type:", skin);
        antialiasingLabel = new Label("Select antialiasing:", skin);
        alphaLabel = new Label("Contact angle:", skin);
        bondLabel = new Label("Bond number:", skin);
        epsilonLabel = new Label("Accuracy:", skin);
        splitNumLabel = new Label("Number of splits:", skin);
        alphaField = new TextField("", skin);
        bondField = new TextField("", skin);
        epsilonField = new TextField("", skin);
        splitNumField = new TextField("", skin);
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
        bondField.setMessageText("Enter Bond number");
        epsilonField.setMessageText("Enter accuracy");
        splitNumField.setMessageText("Enter number of splits");

        alphaField.setTextFieldFilter(new FloatFieldFilter(false));
        bondField.setTextFieldFilter(new FloatFieldFilter(false));
        epsilonField.setTextFieldFilter(new FloatFieldFilter(true));
        splitNumField.setTextFieldFilter(new IntegerFieldFilter(true));

        defaults().pad(5.0f, 5.0f, 0.0f, 5.0f).expandX().fill();
        add(antialiasingLabel).row();
        add(antialiasingSelect).row();
        add(problemLabel).row();
        add(problemSelect).row();
        add(alphaLabel).row();
        add(alphaField).row();
        add(bondLabel).row();
        add(bondField).row();
        add(epsilonLabel).row();
        add(epsilonField).row();
        add(splitNumLabel).row();
        add(splitNumField).row();
        add(generateButton).center().pad(5.0f, 0.0f, 5.0f, 0.0f).width(2.0f * layout.width);

        antialiasingSelect.setItems(antialiasings);
        problemSelect.setItems(problems);

        antialiasingSelect.setSelected("No AA");
        problemSelect.setSelected("Axisymmetric + Flat");

        antialiasingSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!event.isHandled()) {
                    switch (antialiasingSelect.getSelected()) {
                        case "FXAA":
                            problemGraph.setAntialiasing(AntiAliasing.FXAA);
                            break;
                        case "SSAA4":
                            problemGraph.setAntialiasing(AntiAliasing.SSAA4);
                            break;
                        case "SSAA4 + FXAA":
                            problemGraph.setAntialiasing(AntiAliasing.SSAA4_FXAA);
                            break;
                        default:
                            problemGraph.setAntialiasing(AntiAliasing.noAA);
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
                            problemGraph.setVisible(true, false);
                            break;
                        case "Flat":
                            problemGraph.setVisible(false, true);
                            break;
                        default:
                            problemGraph.setVisible(true, true);
                    }
                }
            }
        });

        generateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ErrorDialog errorDialog;
                double alpha;
                double bond;
                double epsilon;
                int splitNum;

                try {
                    alpha = Double.valueOf(alphaField.getText());
                    bond = Double.valueOf(bondField.getText());
                    epsilon = Double.valueOf(epsilonField.getText());
                    splitNum = Integer.valueOf(splitNumField.getText());

                    problemGraph.generateGraph(alpha, bond, epsilon, splitNum);
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
                }
            }
        });
    }
}
