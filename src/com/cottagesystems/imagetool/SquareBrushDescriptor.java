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
public class SquareBrushDescriptor implements BrushDescriptor {

    private double radius;

    public SquareBrushDescriptor( double radius ) {
        this.radius= radius;
    }
    
    public void init(Point center) {   
    }

    public double getWeight(Point center, Point p) {
        return 1.0;
    }

    public void finished(Point center) {
    }

    public Rectangle getBounds() {
        return new Rectangle( (int)(-1*radius), (int)(-1*radius), (int)(2*radius)+1, (int)(2*radius)+1 );
    }
    
    public String toString() {
        return "Square "+(radius*2+1);
    }
}
