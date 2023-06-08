package com.beacon.gui;
import javax.swing.text.*;

public class WrapLabelView extends LabelView { 
    public WrapLabelView(Element element) { 
        super(element); 
    } 
 
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
