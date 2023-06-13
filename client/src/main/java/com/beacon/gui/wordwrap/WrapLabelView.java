/* 
 * WrapLabelView.java
 * Author: Oliver
 * Creates a label view for wrapping text in the text pane.
*/
package com.beacon.gui.wordwrap;
import javax.swing.text.*; // Swing text imports

/**
 * LabelView for wrapping text in the text pane.
 * @see LabelView for more information on LabelView.
 * @author Oliver
 */
public class WrapLabelView extends LabelView { 
    /**
     * Constructor for the wrap label view.
     * @param element
     */
    public WrapLabelView(Element element) { 
        super(element); 
    } 
    
    /**
     * Gets the minimum span of the view.
     * @param axis the axis to get the minimum span of
     * @return the minimum span of the view
     * @throws IllegalArgumentException if the axis is invalid
     * @author Oliver
     */
    public float getMinimumSpan(int axis) { 
        switch (axis) { 
            case View.X_AXIS: 
                return 0; 
            case View.Y_AXIS: 
                return super.getMinimumSpan(axis); 
            default: 
                throw new IllegalArgumentException("Invalid axis: " + axis); 
        } 
    }
}
