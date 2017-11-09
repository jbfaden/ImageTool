/*
 * ToolSupport.java
 *
 * Created on December 18, 2006, 4:33 PM
 *
 *
 */

package com.cottagesystems.imagetool;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 *
 * @author Jeremy
 */
public class ToolSupport {
    
    static BufferedImage getWholeScreenShot() throws AWTException {
        Robot robot;
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        
        robot = new Robot(gs[0]);
        Dimension dim= Toolkit.getDefaultToolkit().getScreenSize();
        BufferedImage screen= robot.createScreenCapture( new Rectangle( 0, 0, dim.width, dim.height ) );
        return screen;
    }
    
    static BufferedImage getPartialScreenShot( final Window parent ) throws AWTException {
        
        ScreenSelectorWindow window= new ScreenSelectorWindow(parent );
        BufferedImage image= window.getSelection();
        
        return image;
        
    }
    
    static BufferedImage resizeImage( BufferedImage image, Dimension d ) {
        BufferedImage result= new BufferedImage( d.width, d.height, BufferedImage.TYPE_INT_ARGB );
        AffineTransform at= new AffineTransform( );
        at.scale( 1.0 * d.width / image.getWidth(), 1.0 * d.height / image.getHeight() );
        Graphics2D g= (Graphics2D) result.getGraphics();
        g.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g.drawImage( image, at, null );
        return result;
    }
    
    /**
     * loads the URL onto a BufferedImage with Alpha channel.
     */
    public static BufferedImage loadURL( URL url ) throws IOException {
        BufferedImage image;
        image = ImageIO.read(url);
        if ( image==null ) throw new IOException("unable to read URL: "+url );
        BufferedImage im1= new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB );
        im1.getGraphics().drawImage( image, 0, 0, null );
        image= im1;
        return image;
    }
    
    /**
     * return the mime type by extension, or null if not found.
     */
    public static String getMimeType( String name )  {
        int i= name.lastIndexOf( '.' );
        String ext= name.substring( i+1 ).toLowerCase();
        if ( ext.equals("png") ) {
            return "image/png";
        } else if ( ext.equals("gif") ) {
            return "image/gif";
        } else if ( ext.equals("jpg" ) ) {
            return "image/jpg";
        } else {
            return null;
        }
    }
    
}
