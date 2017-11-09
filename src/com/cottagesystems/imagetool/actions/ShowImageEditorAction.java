package com.cottagesystems.imagetool.actions;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.TopComponent;
import com.cottagesystems.imagetool.ImageEditorTopComponent;

public final class ShowImageEditorAction extends CallableSystemAction {
    
    public void performAction() {
        TopComponent win = ImageEditorTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowImageEditorAction.class, "CTL_ShowImageEditorAction");
    }
    
    protected String iconResource() {
        return "com/cottagesystems/imagetool/actions/brush.png";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
