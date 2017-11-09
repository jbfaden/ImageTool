/*
 * MagicPaintPixelOperation.java
 *
 * Created on December 14, 2006, 9:27 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Magic Paint replaces the component of the color that is the background color with the foreground color.
 * @author jbf
 */
public class MagicPaintPixelOperation implements PixelOperation {
    
    /** Creates a new instance of MagicPaintPixelOperation */
    public MagicPaintPixelOperation() {
    }
    
    public void init(BufferedImage r, Color foreground, Color background) {
    }
    
    public Color applyAt(BufferedImage image, int i, int j, Color foreground, Color background) {
        int i2= image.getRGB( i, j );
        
        int a2= i2 >> 24 & 0x000000FF;
        
        if ( a2==255 || ( a2>0 && (  ( i2 & 0x00FFFFFF ) != ( foreground.getRGB() & 0x00FFFFFF ) ) ) ) {
            return new Color( i2, true );
        }
        
        int r0= background.getRed();
        int r1= foreground.getRed();
        int r2= i2 >> 16 & 0x000000FF;
        
        int g0= background.getGreen();
        int g1= foreground.getGreen();
        int g2= i2 >> 8 & 0x0000000FF;
        
        int b0= background.getBlue();
        int b1= foreground.getBlue();
        int b2= i2 & 0x00000FF;
        
        int alpha=255;
        int r= ( a2 * r1 + ( 255-a2 ) * r0 ) / 255;
        int g= ( a2 * g1 + ( 255-a2 ) * g0 ) / 255;
        int b= ( a2 * b1 + ( 255-a2 ) * b0 ) / 255;
        
        return new Color( r, g, b, alpha );
        
    }
    
    public void finished(BufferedImage r) {
    }
    
    public String toString() {
        return "Magic Paint";
    }
    
}
