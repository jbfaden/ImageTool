/*
 * RoundBrushDescriptor.java
 *
 * Created on December 13, 2006, 1:13 PM
 *
 *
 */

package com.cottagesystems.imagetool;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 *
 * @author Jeremy
 */
public class RoundBrushDescriptor implements BrushDescriptor {

    private double radius;

    public RoundBrushDescriptor( double radius ) {
        this.radius= (int)radius;
    }
    
    public void init(Point center) {   
    }

    public double getWeight(Point center, Point p) {
        if ( ( Math.pow( p.getX() - center.getX(), 2 ) + Math.pow(  p.getY() - center.getY(), 2 ) ) <= ( radius*radius ) ) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    public void finished(Point center) {
    }

    public Rectangle getBounds() {
        return new Rectangle( (int)(-1*radius), (int)(-1*radius), (int)(2*radius)+1, (int)(2*radius)+1 );
    }
    
    public String toString() {
        return "Round "+(radius*2+1);
    }
    
}
