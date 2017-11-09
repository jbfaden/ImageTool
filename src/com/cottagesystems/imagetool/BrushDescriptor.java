/*
 * BrushDescriptor.java
 *
 * Created on December 13, 2006, 1:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Describes a brush, such as round or guassian.
 * @author Jeremy
 */
public interface BrushDescriptor {
    public void init( Point center );
    public Rectangle getBounds( );
    /**
     * return the brush weight at point p, given the
     * center point.  1.0 indicates the brush covers
     * completely, 0.0 means the brush has no effect.
     */    
    public double getWeight( Point center, Point p );
    public void finished( Point center );
}
