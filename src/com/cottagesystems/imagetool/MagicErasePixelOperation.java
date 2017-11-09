/*
 * MagicErasePixelOperation.java
 *
 * Created on December 13, 2006, 2:57 PM
 *
 *
 */

package com.cottagesystems.imagetool;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * This magic eraser calculates the alpha used with foreground when painted
 * on top of background to get the color at r[i,j].  This is done with R,G,B
 * components and a consistent number is returned.
 *
 * @author Jeremy
 */
public class MagicErasePixelOperation implements PixelOperation {
    public void init(BufferedImage r, Color foreground, Color background) {
    }

    public Color applyAt(BufferedImage r, int i, int j, Color foreground, Color background) {
        int i2= r.getRGB( i, j );
        
        int a2= i2 >> 24 & 0x000000FF;
        
        if ( a2<255 ) {
            return new Color( r.getRGB( i, j ), true );
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
        
        int ra, ga, ba;
        int rw, gw, bw;
        
        ra= (r1-r0)==0 ? -999 : ( r2-r0 ) * 255 / ( r1-r0 );
        rw= (r1-r0)*(r1-r0);
        
        ga= (g1-g0)==0 ? -999 : ( g2-g0 ) * 255 / ( g1-g0 );
        gw= (g1-g0)*(g1-g0);
        
        ba= (b1-b0)==0 ? -999 : ( b2-b0 ) * 255 / ( b1-b0 );
        bw= (b1-b0)*(b1-b0);
        
        int alpha= ( ra*rw + ga*gw + ba*bw ) / ( rw + gw + bw );
        alpha= Math.min( Math.max( alpha, 0 ), 255 );
        
        return new Color( r1, g1, b1, alpha );
        
    }

    public void finished(BufferedImage r) {
    }
    
    public String toString() {
        return "Magic Erase";
    }
    
}
