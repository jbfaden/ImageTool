/*
 * PaintPixelOperation.java
 *
 * Created on December 13, 2006, 1:05 PM
 *
 *
 */

package com.cottagesystems.imagetool;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jeremy
 */
public class PaintPixelOperation implements PixelOperation {
   
    public void init( BufferedImage r, Color foreground, Color background ) {
    }

    public Color applyAt( BufferedImage r, int i, int j, Color foreground, Color background ) {
        return foreground;
    }

    public void finished(BufferedImage r) {
    }
    
    public String toString() {
        return "Paint";
    }
}
