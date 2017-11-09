/*
 * ColorIndicatorKeyListener.java
 *
 * Created on January 4, 2007, 1:47 PM
 *
 *
 */

package com.cottagesystems.imagetool;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jeremy
 */
public class ColorIndicatorKeyListener implements KeyListener {
    
    private ColorIndicator colorIndicator;
    
    ColorIndicatorKeyListener( ColorIndicator colorIndicator ) {
        this.colorIndicator= colorIndicator;
    }
    
    public void keyTyped(KeyEvent e) {
    }
    
    public void keyReleased(KeyEvent e) {
        if ( e.getKeyCode() == KeyEvent.VK_C && e.isControlDown() ) {
            Clipboard clipBoard= Toolkit.getDefaultToolkit().getSystemClipboard();
            Color c=null;
            c= colorIndicator.getSelectedColor();
            String colorString= "0x"+ Integer.toHexString( c.getRGB() ).substring(2).toUpperCase();
            clipBoard.setContents( new StringSelection(colorString), new ClipboardOwner() {
                public void lostOwnership(Clipboard clipboard, Transferable contents) {
                }
            } );
            e.consume();
        } else if ( e.getKeyCode() == KeyEvent.VK_V && e.isControlDown() ) {
            Clipboard clipBoard= Toolkit.getDefaultToolkit().getSystemClipboard();
            if ( clipBoard.isDataFlavorAvailable( DataFlavor.stringFlavor ) ) {
                try {
                    String s= (String) clipBoard.getData( DataFlavor.stringFlavor );
                    Color c= Color.decode(s);
                    colorIndicator.setSelectedColor(c);
                } catch (UnsupportedFlavorException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                
            }
            e.consume();
        } else if ( e.getKeyCode()== KeyEvent.VK_BACK_SPACE && e.isShiftDown() ) {
            colorIndicator.setSelectedColor( new Color( 255, 255, 255, 0 ) );
        }
    }
    
    public void keyPressed(KeyEvent e) {
        e.consume();
    }
    
}
