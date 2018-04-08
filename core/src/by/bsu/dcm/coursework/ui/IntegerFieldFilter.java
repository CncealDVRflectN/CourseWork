package by.bsu.dcm.coursework.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;

public class IntegerFieldFilter implements TextFieldFilter {
    private boolean positiveOnly;

    public IntegerFieldFilter(boolean positiveOnly) {
        this.positiveOnly = positiveOnly;
    }

    @Override
    public boolean acceptChar(TextField textField, char c) {
        return (c >= '0' && c <= '9') || (!positiveOnly && (textField.getText().length() == 0 && c == '-'));
    }
}
