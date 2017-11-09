/*
 * ImageEditorMouseAdapter.java
 *
 * Created on December 13, 2006, 9:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author jbf
 */
public class ImageEditorMouseAdapter extends MouseInputAdapter implements MouseWheelListener, KeyListener {
    
    private ImageEditorPanel editor;
    
    public ImageEditorMouseAdapter( ImageEditorPanel editor ) {
        this.editor= editor;
    }
    
    /**
     * convenient reference for all tools.
     */
    Point pressLocation;
    
    protected void paint( Graphics2D g ) {
        if ( tool!=null ) {
            tool.paint(g);
        }
    }
    
    private interface ToolDelegate {
        void mousePressed(MouseEvent e);
        void mouseDragged(MouseEvent e);
        void mouseReleased(MouseEvent e);
        void mouseMoved( MouseEvent e ) ;
        void mouseWheelMoved( MouseWheelEvent e );
        void paint( Graphics2D g );
        Cursor getCursor();
    }
    
    ToolDelegate tool;
    int toolButton;
    
    class PanDelegate implements ToolDelegate {
        private Point dragFrom=null;
        
        private void doMousePan(final MouseEvent e) {
            int dx= e.getPoint().x - dragFrom.x;
            int dy= e.getPoint().y - dragFrom.y;
            
            Rectangle rc = editor.getVisibleRect();
            rc.x -= dx;
            rc.y -= dy;
            editor.scrollRectToVisible(rc);
            
        }
        
        public void mousePressed(MouseEvent e) {
            dragFrom= e.getPoint();
            editor.setCursor( new Cursor( Cursor.MOVE_CURSOR ) );
        }
        
        public void mouseDragged(MouseEvent e) {
            if ( dragFrom!=null ) {
                doMousePan(e);
            }
        }
        
        public void mouseReleased(MouseEvent e) {
            if ( dragFrom!=null ) {
                doMousePan(e);
                
                dragFrom= null;
                editor.setCursor( null );
            }
        }
        
        public void mouseWheelMoved(MouseWheelEvent e) {
        }
        
        public void paint(Graphics2D g) {
        }
        
        public Cursor getCursor() {
            return new Cursor( Cursor.HAND_CURSOR );
        }
        
        public void mouseMoved(MouseEvent e) {
        }
        
    }
    
    class RectangleDelegate implements ToolDelegate {
        Point startPoint;
        
        private void doSelection( MouseEvent e ) {
            double scale= editor.getScale();
            Point p= e.getPoint();
            Point p0= new Point( (int)( p.x / scale ), (int)( p.y / scale ) );
            Point p1= new Point( (int)( startPoint.x / scale ), (int)( startPoint.y / scale ) );
            Rectangle r= new Rectangle( p0 );
            r.add(p1);
            editor.setSelection( new Area( r ) );
        }
        
        public void mousePressed(MouseEvent e) {
            startPoint= e.getPoint();
            doSelection(e);
        }
        
        public void mouseDragged(MouseEvent e) {
            doSelection(e);
        }
        
        public void mouseReleased(MouseEvent e) {
            doSelection(e);
        }
        
        public void mouseWheelMoved(MouseWheelEvent e) {
        }
        
        public void paint(Graphics2D g) {
            return;
        }
        
        public Cursor getCursor() {
            return new Cursor( Cursor.CROSSHAIR_CURSOR );
        }
        
        public void mouseMoved(MouseEvent e) {
        }
        
    }
    
    class PaintDelegate implements ToolDelegate {
        boolean enabled= true;
        public void mousePressed(MouseEvent e) {
            if ( enabled ) {
                editor.initPixelOp();
                editor.doPaint( e.getPoint() );
            }
        }
        
        public void mouseDragged(MouseEvent e) {
            if ( enabled ) editor.doPaint( e.getPoint() );
        }
        
        public void mouseReleased(MouseEvent e) {
            if ( enabled ) {
                editor.finishPixelOp();
                editor.repaint();
            }
        }
        
        public void mouseWheelMoved(MouseWheelEvent e) {
            enabled= false; // disable further drawing.
            if ( e.getWheelRotation() < 0 ) {
                editor.undoPaintOp();
            } else if ( e.getWheelRotation()> 0 ) {
                editor.redoPaintOp();
            }
        }
        
        public void paint(Graphics2D g) {
            
        }
        
        public Cursor getCursor() {
            return new Cursor( Cursor.CROSSHAIR_CURSOR );
        }
        
        public void mouseMoved(MouseEvent e) {
            editor.setBrushLocation( e.getPoint() );
            editor.repaint();
        }
    }
    
    
    private Action createUndoAction() {
        return new AbstractAction( "Undo" ) {
            public void actionPerformed( ActionEvent e ) {
                editor.undo();
            }
        };
    }
    
    private Action createRedoAction() {
        return new AbstractAction( "Redo" ) {
            public void actionPerformed( ActionEvent e ) {
                editor.redo();
            }
        };
    }
    
    private Action selectAllAction() {
        return new AbstractAction( "SelectAll" ) {
            public void actionPerformed( ActionEvent e ) {
                editor.support.selectAll();
            }
        };
    }
    
    private Action createCropAction() {
        return new AbstractAction( "Crop" ) {
            public void actionPerformed( ActionEvent e ) {
                editor.support.crop();
                editor.repaint();
            }
        };
    }
    
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        if ( e.getModifiers()==18 ) {
            applyCtrlConstraint( e );
        }
        if ( tool!=null ) {
            tool.mouseReleased(e);
            if ( e.getButton()==toolButton )  tool=null;
        }
        
    }
    
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
    }
    
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
    }
    
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
    }
    
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        editor.requestFocus();
        this.pressLocation= e.getPoint();
        if ( tool==null ) {
            if ( e.getButton()==MouseEvent.BUTTON2 ) {
                tool= new PanDelegate();
            } else if ( e.getButton()==MouseEvent.BUTTON1 ) {
                editor.pushState();
                tool= new PaintDelegate();
            } else if ( e.getButton()==MouseEvent.BUTTON3 && e.isControlDown() ) {
                tool= new RectangleDelegate();
            } 
            toolButton= e.getButton();
            if ( tool!=null ) editor.setCursor( tool.getCursor() );
        }
        if ( tool!=null ) tool.mousePressed(e);
    }
    
    public void mouseWheelMoved(MouseWheelEvent e) {
        if ( tool!=null ) {
            tool.mouseWheelMoved(e);
        } else {
            float scale= editor.getScale();
            scale += e.getWheelRotation();
            editor.setScale(scale);
        }
    }
    
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        if ( e.getModifiers()==18 ) { //TODO: constant
            applyCtrlConstraint( e );
        } else {
            pressLocation= e.getPoint();
        }
        Rectangle dirty= editor.getBrushDirty();
        editor.repaint(dirty);
        editor.setBrushLocation(e.getPoint() );
        if ( tool!=null ) tool.mouseDragged(e);
    }
    
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        Rectangle dirty= editor.getBrushDirty();
        //System.err.println(dirty);
        if ( dirty!=null ) {
            editor.repaint(dirty);
        }
        editor.setBrushLocation(e.getPoint() );
        if ( tool!=null ) tool.mouseMoved(e);
    }
    
    private void applyCtrlConstraint( MouseEvent e ) {
        double angle= Math.abs( Math.atan( ( e.getY() - pressLocation.getY() ) / (  e.getX() - pressLocation.getX() ) ) * 180 / Math.PI );
        if ( angle<45 ) e.translatePoint( 0, pressLocation.y-e.getY() );
        if ( angle>=45 ) e.translatePoint( pressLocation.x- e.getX() , 0 );
        return;
    }
    
    public void keyTyped(KeyEvent e) {
      /*  if ( e.getKeyCode()==KeyEvent.VK_UNDO || (e.getKeyCode()==KeyEvent.VK_Z && e.getModifiers()==18 ) ) {
            Object state= editor.getAppHistory().undo();
            if ( state!=null ) editor.setImage( (BufferedImage)state ); // TODO: momento
        }*/
    }
    
    /* used by keyPressed to do copy operation */
    private BufferedImage createBufferedImage( Image image ) {
        int w, h;
        w= image.getWidth(editor);
        h= image.getHeight(editor);
        BufferedImage bimage= new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
        bimage.getGraphics().drawImage( image, 0, 0, editor );
        return bimage;
    }
    
    public void keyPressed(KeyEvent e) {
        
        // move all control stuff to here.
        if ( e.isControlDown() ) {
            boolean doConsume=true;
            switch ( e.getKeyCode() ) {
                case KeyEvent.VK_MINUS:
                    editor.zoomOut();
                    break;
                case KeyEvent.VK_EQUALS:
                case KeyEvent.VK_PLUS:
                    editor.zoomIn();
                    break;
                case KeyEvent.VK_A:
                    editor.support.selectAll();
                    break;
                case KeyEvent.VK_Y:
                    editor.redo();
                    break;
                case KeyEvent.VK_Z:
                    editor.undo();
                    break;
                case KeyEvent.VK_0:
                    if ( e.isAltDown() ) editor.setScale(1);
                    break;
                default:
                    doConsume= false;
            }
            if ( doConsume ) e.consume();
        }
        if ( e.isConsumed() ) return;
        
        if ( e.getKeyCode()==KeyEvent.VK_UNDO ) {
            editor.undo();
        } else if ( e.getKeyCode()==KeyEvent.VK_BACK_SPACE ) {
            if ( editor.getSelection()!=null ) {
                Color c0=null;
                if ( e.isShiftDown() ) {
                    c0= editor.getBackground();
                    editor.setBackground( new Color( 255, 255, 255, 0 ) );
                    
                }
                editor.setPixelOp( new ErasePixelOperation() );
                if ( c0!=null ) editor.setBackground( c0 );            
            }

        } else if (  e.getKeyCode()==KeyEvent.VK_V && e.isControlDown() ) {
            Clipboard clipBoard= Toolkit.getDefaultToolkit().getSystemClipboard();
            DataFlavor myFlav= clipBoard.getAvailableDataFlavors()[0];
            if ( clipBoard.isDataFlavorAvailable( DataFlavor.imageFlavor ) ) {
                try {
                    Image image= (Image)clipBoard.getData( DataFlavor.imageFlavor );
                    editor.setImage( createBufferedImage(image) );
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (UnsupportedFlavorException ex) {
                    throw new RuntimeException(ex);
                }
            }
            e.consume();
        }
    }
    
    public void keyReleased(KeyEvent e) {
        System.err.println("keyReleased: "+e.getKeyCode());
    }
}
