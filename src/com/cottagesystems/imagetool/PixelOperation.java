/*
 * PixelOperation.java
 *
 * Created on December 13, 2006, 12:57 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jeremy
 */
public interface PixelOperation {
    void init( BufferedImage r, Color foreground, Color background );
    Color applyAt( BufferedImage r, int i, int j, Color foreground, Color background );
    void finished( BufferedImage r );
}
