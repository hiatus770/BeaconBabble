/*
 * WrapEditorKit.java
 * Author: Oliver
 * Creates an editor kit for wrapping text in the text pane.
 */
package com.beacon.gui.wordwrap;
import javax.swing.text.StyledEditorKit; // Swing text imports
import javax.swing.text.ViewFactory;

/**
 * Editor kit for wrapping text in the text pane.
 * @see StyledEditorKit for more information on StyledEditorKit.
 * @author Oliver
 */
public class WrapEditorKit extends StyledEditorKit{
    ViewFactory defaultFactory = new WrapColumnFactory();
    public ViewFactory getViewFactory() {
        return defaultFactory;
    }
}
