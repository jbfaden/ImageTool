/*
 * Util.java
 *
 * Created on June 6, 2007, 11:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

/**
 *
 * @author jbf
 */
public class Util {
    
    public static TexturePaint getBgPaint() {
        BufferedImage underImage= new BufferedImage( 60, 60, BufferedImage.TYPE_INT_ARGB );
        int grey1= new Color( 200,200,200 ).getRGB();
        int grey2= new Color( 255,255,255 ).getRGB();
        
        for ( int i=0; i<60; i++ ) {
            boolean row= ( i / 6 ) % 2 == 0;
            for ( int j=0; j<60; j++ ) {
                boolean col= ( j / 6 ) % 2 == 0;
                if ( row==col ) underImage.setRGB( i,j,grey1 ); else underImage.setRGB( i,j,grey2 );
            }
        }
        
        TexturePaint bgPaint= new TexturePaint( underImage, new Rectangle( 0,0, 60, 60 ) );
        return bgPaint;
    }
}
