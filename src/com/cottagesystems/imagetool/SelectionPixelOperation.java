/*
 * SelectionPixelOperation.java
 *
 * Created on December 20, 2006, 7:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

/**
 *
 * @author jbf
 */
public class SelectionPixelOperation implements PixelOperation {
    
    GeneralPath shape;
    Area area;

    private ImageEditorPanel editor;
    
    /** Creates a new instance of SelectionPixelOperation */
    public SelectionPixelOperation( ImageEditorPanel editor ) {
        this.editor= editor;
    }

    public void init(BufferedImage r, Color foreground, Color background) {
        shape=null;
        area=null;
    }

    public Color applyAt(BufferedImage r, int i, int j, Color foreground, Color background) {
        if ( shape==null ) {
            shape= new GeneralPath( new Rectangle( i, j, 1, 1 ) );
            area= new Area( new Rectangle( i, j, 1, 1 ) );
        } else {
            shape.append( new Rectangle( i, j, 1, 1 ), false );
            area.add( new Area( new Rectangle( i, j, 1, 1 ) ) );
        }
        return new Color( r.getRGB( i,j ) );
    }

    public void finished(BufferedImage r) {
        editor.setSelection( area );
    }
    
    public String toString() {
        return "Selection";
    }
}
