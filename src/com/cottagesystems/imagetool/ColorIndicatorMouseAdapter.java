/*
 * ColorIndicatorMouseAdapter.java
 *
 * Created on December 13, 2006, 10:30 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JColorChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author jbf
 */
public class ColorIndicatorMouseAdapter extends MouseInputAdapter {
    
    ColorIndicator colorIndicator;
    Point dragInit=null ;
    
    /** Creates a new instance of ColorIndicatorMouseAdapter */
    public ColorIndicatorMouseAdapter( ColorIndicator colorIndicator ) {
        this.colorIndicator= colorIndicator;
    }
        
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if ( dragInit!=null ) {
            dragInit=null;
            colorIndicator.setCursor(null);
        }
    }
    
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        colorIndicator.requestFocus();
        colorIndicator.setSelected( colorIndicator.getEditColor( e.getPoint() ) );
    }
    
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
    }
    
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
    }
    
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
    }
    
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if ( dragInit==null  ) {
            dragInit= e.getPoint();
            colorIndicator.setCursor( new Cursor( Cursor.CROSSHAIR_CURSOR ) );
        }
        if ( dragInit!=null ) {
            Point screenPoint= e.getPoint();
            SwingUtilities.convertPointToScreen( screenPoint, colorIndicator );
            Robot robot;
            try {
                robot = new Robot();
                Color c= robot.getPixelColor( screenPoint.x, screenPoint.y );
                colorIndicator.setSelectedColor( c );
            } catch (AWTException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if ( e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2 ) {
            Color c= colorIndicator.getSelectedColor();
            Color nc= JColorChooser.showDialog( colorIndicator, "select foreground", c );
            if ( nc!=null ) colorIndicator.setSelectedColor(nc);
            
            colorIndicator.repaint();
        }
    }
    
}
