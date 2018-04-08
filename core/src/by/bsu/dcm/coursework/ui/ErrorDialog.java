package by.bsu.dcm.coursework.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class ErrorDialog extends Table {
    private final Label message;
    private final TextButton button;

    public ErrorDialog(Skin skin) {
        super(skin);

        message = new Label("", skin);
        button = new TextButton("OK", skin);

        setBackground(new NinePatchDrawable(skin.getPatch("error-background")));

        build();
    }

    private void build() {
        add(message).pad(5.0f, 5.0f, 0.0f, 5.0f).fill().row();
        add(button).center().pad(5.0f);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ErrorDialog.this.setVisible(false);
            }
        });
    }

    public void setMessage(CharSequence message) {
        GlyphLayout layout = new GlyphLayout();

        layout.setText(this.message.getStyle().font, message);
        this.message.setText(message);

        setSize(layout.width + 10.0f, 2.0f * (layout.height + 20.0f));
        setPosition((Gdx.graphics.getWidth() - getWidth()) / 2.0f, (Gdx.graphics.getHeight() - getHeight()) / 2.0f);

        layout.setText(button.getStyle().font, button.getText());
        getCell(button).width(2.0f * layout.width);
    }
}
