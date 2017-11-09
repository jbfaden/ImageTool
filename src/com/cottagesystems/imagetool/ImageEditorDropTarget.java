/*
 * ImageEditorDropTarget.java
 *
 * Created on January 1, 2007, 2:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;

/**
 *
 * @author jbf
 */
public class ImageEditorDropTarget extends DropTarget {
    
    DropTargetDropEvent current;
    Map<String,DataFlavorAction> actionsMap;
    ImageEditorPanel editor;
    
    ImageEditorTopComponent tc; // yuck!
    
    interface DataFlavorAction {
        void doAction( DataFlavor e , Transferable t);
    }
    
    
    /** Creates a new instance of ImageEditorDropTarget */
    public ImageEditorDropTarget( final ImageEditorPanel editor ) {
        this.editor= editor;
        actionsMap= new HashMap<String,DataFlavorAction>();
        actionsMap.put( "x-java-openide-nodednd", new DataFlavorAction() {
            public void doAction( DataFlavor e, Transferable t ) {
                try {
                    System.err.println( t.getTransferData(e) );
                    DataNode o= (DataNode) t.getTransferData(e);
                    FileObject fo= o.getDataObject().getPrimaryFile();
                    if ( fo.getMIMEType().startsWith("image") ) {
                        File file= FileUtil.toFile( fo );
                        if ( file==null ) {
                            editor.setImage( ToolSupport.loadURL( fo.getURL() ) );
                        } else {
                            tc.loadFile( file );
                        }
                    }
                } catch (UnsupportedFlavorException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        actionsMap.put( "x-java-file-list", new DataFlavorAction() {
            public void doAction( DataFlavor e, Transferable t ) {
                try {
                    System.err.println(e.getRepresentationClass());
                    List l= (List) t.getTransferData(e);
                    System.err.println(l.get(0));
                    File f= (File) l.get(0);
                    if ( ToolSupport.getMimeType( f.toString() ).startsWith("image") ) {
                        editor.setImage( ToolSupport.loadURL( f.toURI().toURL() ) );
                    }
                } catch (UnsupportedFlavorException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    public void setImageEditorTopComponent( ImageEditorTopComponent tc ) {
        this.tc= tc;
    }
    
    public void drop(DropTargetDropEvent dtde) {
        
            DataFlavor df=null;
            for ( DataFlavor i: dtde.getCurrentDataFlavorsAsList() ) {
                if ( actionsMap.containsKey(i.getSubType() ) ) {
                    df= i;
                    dtde.acceptDrop( DnDConstants.ACTION_COPY );
                    DataFlavorAction dfa= (DataFlavorAction) actionsMap.get(i.getSubType());
                    dfa.doAction( df, dtde.getTransferable() );
                    return;
                }
            }
           
        
        
    }
    
    public void dragEnter(DropTargetDragEvent dtde) {
        int count=0;
        for ( DataFlavor i: dtde.getCurrentDataFlavorsAsList() ) {
            System.err.println(""+(count++)+" "+i.getRepresentationClass()+" "+i.getSubType());
            if ( actionsMap.containsKey(i.getSubType() ) ) {
                dtde.acceptDrag( DnDConstants.ACTION_COPY );
            }
        }
        super.dragEnter(dtde);
    }
    
}
