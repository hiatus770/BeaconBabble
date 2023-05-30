package com.beacon;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

public class WrapEditorKit extends StyledEditorKit{
    ViewFactory defaultFactory = new WrapColumnFactory();
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }
}
