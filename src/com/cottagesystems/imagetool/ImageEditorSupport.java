/*
 * ImageEditorSupport.java
 *
 * Created on December 13, 2006, 1:23 PM
 *
 *
 */

package com.cottagesystems.imagetool;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jeremy
 */
public class ImageEditorSupport {
    ImageEditorPanel editor;
    ImageEditorSupport( ImageEditorPanel editor ) {
        this.editor= editor;
    }
        
    static void applyBrushOperator(
            BufferedImage image,
            BrushDescriptor brush,
            PixelOperation op,
            ColorIndicator colorIndicator,
            Point p ) {
        
        Color foregroundColor= colorIndicator.getForegroundColor();
        Color backgroundColor= colorIndicator.getBackgroundColor();
        
        brush.init(p);
        
        Rectangle bounds= brush.getBounds();
        bounds.translate( p.x, p.y );
        
        int x0= Math.max( bounds.x, 0 );
        int x1= Math.min( bounds.x+bounds.width, image.getWidth() );
        int y0= Math.max( bounds.y, 0 );
        int y1= Math.min( bounds.y+bounds.height, image.getHeight() );
        
        if ( x1-x0 <= 0 ) return;
        if ( y1-y0 <= 0 ) return;
        
        int[] rgbArray= new int[ ( x1-x0 ) * ( y1-y0 ) ];
        
        int i=0;
        for ( int y= y0; y<y1; y++ ) {
            for ( int x=x0; x<x1; x++ ) {
                double w= brush.getWeight( p, new Point( x, y ) );
                if ( w>0. ) {
                    Color c= op.applyAt( image, x, y, foregroundColor, backgroundColor );
                    rgbArray[i++]= c.getRGB();
                } else {
                    rgbArray[i++]= image.getRGB( x, y );
                }
            }
        }
        
        image.setRGB( x0, y0, x1-x0, y1-y0, rgbArray, 0, x1-x0 );
        brush.finished( p );
        op.finished( image );
    }
    
    static class RepaintTimerRunnable implements Runnable {
        boolean keepGoing=true;
        Component c;
        int periodMillis;
        Area repaintShape;
        RepaintTimerRunnable( int periodMillis, Component c, Area repaintShape  ) {
            this.c= c;
            this.periodMillis= periodMillis;
            this.repaintShape= repaintShape;
        }
        public void run() {
            while ( keepGoing ) {
                //Rectangle r= repaintShape.getBounds();
                c.repaint( );
                try {
                    Thread.sleep(periodMillis);
                } catch (InterruptedException ex) {
                    keepGoing= false;
                }
            }
            
        }
        
    }
    
    
    public void selectAll() {
        Rectangle r= new Rectangle( 0, 0, editor.getImage().getWidth(), editor.getImage().getHeight() );
        editor.setSelection( new Area( r ) );
    }

    void crop() {
        if ( editor.getSelection()==null ) return;
        Rectangle r= editor.getSelection().getBounds();
        BufferedImage image= editor.getImage();
        image= image.getSubimage( r.x, r.y, r.width, r.height );
        editor.pushState();
        editor.setImage(image);
        editor.setSelection(null);
    }
}
