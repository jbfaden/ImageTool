package com.cottagesystems.imagetool;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Action which shows ImageEditor component.
 */
public class ImageEditorAction extends AbstractAction {
    
    public ImageEditorAction() {
        super(NbBundle.getMessage(ImageEditorAction.class, "CTL_ImageEditorAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(ImageEditorTopComponent.ICON_PATH, true)));
    }
    
    public void actionPerformed(ActionEvent evt) {
        TopComponent win = ImageEditorTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
}
