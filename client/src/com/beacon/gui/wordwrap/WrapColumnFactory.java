package com.beacon.gui.wordwrap;
import javax.swing.text.*; // Swing text imports

/**
 * Factory for creating text wrapping for the text pane.
 * @see ViewFactory for more information on ViewFactory.
 * @author Oliver
 */
public class WrapColumnFactory implements ViewFactory {
    public View create(Element element) { // Creates a view for the text pane
        String kind = element.getName(); // Gets the name of the element
        if (kind != null) { // If the element is not null
            if (kind.equals(AbstractDocument.ContentElementName)) { // If the element is a content element
                return new WrapLabelView(element); // Return a new wrap label view
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) { // If the element is a paragraph element
                return new ParagraphView(element); // Return a new paragraph view
            } else if (kind.equals(AbstractDocument.SectionElementName)) { // If the element is a section element
                return new BoxView(element, View.Y_AXIS); // Return a new box view
            } else if (kind.equals(StyleConstants.ComponentElementName)) { // If the element is a component element
                return new ComponentView(element); // Return a new component view
            } else if (kind.equals(StyleConstants.IconElementName)) { // If the element is an icon element
                return new IconView(element); // Return a new icon view
            }
        }
 
        // Default to text display
        return new LabelView(element);
    }
}
