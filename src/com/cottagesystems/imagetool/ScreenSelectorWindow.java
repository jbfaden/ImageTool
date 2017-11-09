/*
 * ScreenSelectorWindow.java
 *
 * Created on December 18, 2006, 9:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author jbf
 */
public class ScreenSelectorWindow extends JWindow {
    
    GraphicsDevice gd;
    BufferedImage screen;
    Window owner;
    ContentPanel contentPanel;
    
    public ScreenSelectorWindow( Window owner ) {
        super();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        this.gd= gd[0];
        this.owner= owner;
        contentPanel= new ContentPanel();
        this.setContentPane( contentPanel );
    }
    
    /**
     * blocks until selection is made.
     */
    public BufferedImage getSelection() throws AWTException {
        Robot robot= new Robot(gd);
        
        Dimension dim= Toolkit.getDefaultToolkit().getScreenSize();
        screen= robot.createScreenCapture( new Rectangle( 0, 0, dim.width, dim.height ) );
        
       // this.owner.setVisible(false);
        
        gd.setFullScreenWindow( this );
        
        contentPanel.working= true;
        p2=null;
        p1=null;
        while ( contentPanel.working ) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
        this.dispose();
        
        if ( p1==null || p2==null ) {
            throw new IllegalStateException("p1 or p2 is null");
        }
        
        Rectangle result= new Rectangle( p1 );
        result.add(p2);
        
        BufferedImage resultImage= screen.getSubimage( result.x, result.y, result.width, result.height );
        
        return resultImage;
    }
    
    Point p1;
    Point p2;
    
    private class ContentPanel extends JPanel {
        boolean working;
        ContentPanel() {
            MouseInputAdapter mia= getMIA();
            addMouseListener( mia );
            addMouseMotionListener( mia );
        }
        
        public void paintComponent( Graphics g ) {
            if ( screen!=null ) {
                g.drawImage( screen, 0, 0, this );
                Graphics2D g2= (Graphics2D)g.create();
                g2.setColor( new Color( 255, 255, 255, 100 ) );
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            } else {
                g.drawString( "no image", 30, 30 );
            }
            
            if ( p2!=null ) {
                g.setColor( Color.BLUE );
                Rectangle r= new Rectangle( p1 );
                r.add(p2);
                g.drawRect( r.x, r.y, r.width, r.height );
            }
        }
        
        public MouseInputAdapter getMIA() {
            return new MouseInputAdapter() {
            public void mousePressed( MouseEvent e ) {
                p1= e.getPoint();
                System.err.println("got p1: "+p1);
                repaint();
            }
            public void mouseDragged( MouseEvent e ) {
                p2= e.getPoint();
                System.err.println("got p2: "+p2);
                repaint();
            }
            public void mouseReleased( MouseEvent e ) {
                p2= e.getPoint();
                System.err.println("release: "+p2);
                working= false;
                repaint();
            }
        };
    }
}
}