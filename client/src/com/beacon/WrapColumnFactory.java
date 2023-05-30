package com.beacon;
import javax.swing.text.*;

public class WrapColumnFactory implements ViewFactory {
    public View create(Element element) {
        String kind = element.getName();
        if (kind != null) {
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new WrapLabelView(element);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                return new ParagraphView(element);
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
                return new BoxView(element, View.Y_AXIS);
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
                return new ComponentView(element);
            } else if (kind.equals(StyleConstants.IconElementName)) {
                return new IconView(element);
            }
        }
 
        // default to text display
        return new LabelView(element);
    }
}
