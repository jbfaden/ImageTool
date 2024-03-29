/*
 * ImageEditorPanel.java
 *
 * Created on December 13, 2006, 8:55 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import com.cottagesystems.imagetool.ImageEditorSupport.RepaintTimerRunnable;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import javax.swing.JPanel;

/**
 * draws the image and stats about the controls.
 * @author jbf
 */
public final class ImageEditorPanel extends JPanel {
    
    final TexturePaint bgPaint;
    
    AppHistory history;
    static final int HISTORY_SIZE=3;
    
    /**
     * the history for the active operation, the mouse gesture.
     */
    AppHistory opHistory;
    
    private Area selectionArea;
    private RepaintTimerRunnable selectionRunnable;
    protected ImageEditorSupport support;
    
    private final ImageEditorDropTarget dropTarget;
    
    /** Creates a new instance of ImageEditorPanel */
    public ImageEditorPanel( ) {
        setOpaque(true);
        
        ImageEditorMouseAdapter mia= new ImageEditorMouseAdapter(this);
        addMouseListener( mia );
        addMouseMotionListener( mia );
        addMouseWheelListener( mia );
        addKeyListener( mia );
        setFocusable(true);
        
        dropTarget= new ImageEditorDropTarget(this );
        setDropTarget( dropTarget );
        
        setBrush( new RoundBrushDescriptor( 3 ) );
        
        this.support= new ImageEditorSupport(this);
        
        bgPaint= Util.getBgPaint();
        history= new AppHistory(HISTORY_SIZE);
    }
    
    private BufferedImage image;
    
    /**
     * shape indicating the pixels that would be affected by the brush.
     */
    private Shape brushOutline= null;
    private Point brushLocation= null;
    
    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g= (Graphics2D)g1;
        
        if ( image==null ) {
            g.drawString( "(No Image)", 20, 20 );
            return;
        }
        
        Dimension d= new Dimension( (int) (image.getWidth()*getScaleFrac()), (int)(image.getHeight()*getScaleFrac()) );
        
        Color color0= g.getColor();
        
        switch (backgroundTexture) {
            case checkers:
                g.setPaint( bgPaint );
                break;
            case black:
                g.setPaint( null );
                g.setColor( Color.BLACK );
                break;
            case white:
                g.setPaint( null );
                g.setColor( Color.WHITE );
                break;
            default:
                break;
        }
        
        g.fillRect( 0, 0, d.width, d.height );
        
        g.setPaint( null );
        g.setColor( color0 );
        
        Dimension c= this.getSize();
        
        g.setColor( getBackground() );
        if ( d.width<c.width ) g.fillRect( d.width, 0, c.width-d.width, c.height );
        if ( d.height<c.height ) g.fillRect( 0, d.height, c.width, c.height-d.height );
        
        Graphics2D scaleG= (Graphics2D)g.create();
        scaleG.scale(getScaleFrac(),getScaleFrac());
        scaleG.drawImage( image, 0, 0, this );
        
        if ( selectionArea!=null ) {
            //float phase= System.currentTimeMillis() % 1700 / 1700.f * 3.14159f;
            float phase= System.currentTimeMillis() % 1700 / 1700.f * 10f;
            float rr= (float)( 0.7+Math.abs( Math.sin( phase )*0.3 ) );
            float gg= (float)( 0.7+Math.abs( Math.sin(phase)*0.3 ) );
            //scaleG.setColor( new Color( rr, gg, gg ) );
            scaleG.setColor( Color.WHITE );
            scaleG.setStroke( new BasicStroke( 1/getScaleFrac() ) );
            scaleG.draw( selectionArea );
            float[] DASH= new float[] { 5/getScaleFrac(), 5/getScaleFrac() } ;
            scaleG.setStroke( new BasicStroke( 1/getScaleFrac(), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1.0f, DASH, phase/getScaleFrac() ) );
            scaleG.setColor( Color.BLACK );
            scaleG.draw( selectionArea );
        }
        
        if ( brushOutline!=null && brushLocation!=null ) {
            GeneralPath p= new GeneralPath( brushOutline );
            AffineTransform at= new AffineTransform();
            at.translate( (int)(brushLocation.x/getScaleFrac()) * getScaleFrac(), (int)(brushLocation.y/getScaleFrac()) * getScaleFrac() );
            at.scale( getScaleFrac(),getScaleFrac() );
            
            p.transform( at );
            
            g.setXORMode( Color.WHITE );
            
            g.setColor( Color.BLACK );
            g.draw( p );
            g.setXORMode( Color.BLACK );
        }
        
        scaleG.dispose();
        
        
        g.setColor( Color.GRAY );
        String scaleStr= getScaleString( (int)scale );
        String infoStr= "w:" + image.getWidth() + " \u00D7 h:" + image.getHeight() + " scale: "+scaleStr ;
        g.drawString( infoStr, 2, getScaleFrac()*image.getHeight()+15 );
        
        String toolStr= "tool: "+getPixelOp() + "  brush: "+getBrush();
        g.drawString( toolStr,  2, getScaleFrac()*image.getHeight()+15 + 1 * g.getFontMetrics().getHeight()  );
        if ( selectionArea!=null ) {
            infoStr= "selection bounds: "+selectionArea.getBounds();
            g.drawString(infoStr, 2, getScaleFrac()*image.getHeight()+15+2 * g.getFontMetrics().getHeight() );
        }
       
        
    }
    
    public  static String getScaleString( int scale ) {
        String scaleStr= ( scale<1 ) ? "1/"+(int)(2-scale) : ""+(int)scale;
        return scaleStr;
    }
    /**
     * Getter for property image.
     * @return Value of property image.
     */
    public BufferedImage getImage() {
        return this.image;
    }
    
    /**
     * Setter for property image.
     * @param image New value of property image.
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        if ( history==null ) history= new AppHistory(HISTORY_SIZE);
        resetSize();
    }
    
    public enum BackgroundTextureType {
        checkers,
        black,
        white
    }
    
    private BackgroundTextureType backgroundTexture = BackgroundTextureType.checkers;

    public static final String PROP_BACKGROUNDTEXTURE = "backgroundTexture";

    public BackgroundTextureType getBackgroundTexture() {
        return backgroundTexture;
    }

    public void setBackgroundTexture(BackgroundTextureType backgroundTexture) {
        if ( backgroundTexture==null ) backgroundTexture= BackgroundTextureType.checkers;
        BackgroundTextureType oldBackgroundTexture = this.backgroundTexture;
        this.backgroundTexture = backgroundTexture;
        firePropertyChange(PROP_BACKGROUNDTEXTURE, oldBackgroundTexture, backgroundTexture);
    }

    /**
     * Holds value of property scale.
     */
    private float scale=1.0f;
    
    /**
     * Getter for property scale.
     * @return Value of property scale.
     */
    public float getScale() {
        return this.scale;
    }
    
    /**
     * Setter for property scale.
     * @param scale New value of property scale.
     */
    public void setScale(float scale) {
        float oldScale= this.scale;
        if ( scale< -6.f ) scale=-6.f;
        if ( scale> 10.f ) scale=10f;
        if ( scale==this.scale ) return;
        this.scale = scale;
        firePropertyChange( "scale", oldScale, scale );
        resetSize();
    }
    
    /**
     * 1=1, 0= 1/2, -1= 1/3
     */
    private float getScaleFrac() {
        if ( scale<1 ) {
            return 1.f / ( 2-scale );
        } else {
            return scale;
        }
    }
    
    /**
     * return the point location after scaling.
     * @param p
     * @return 
     */
    public Point scalePoint( Point p ) {
        p= p.getLocation();
        p.x= (int)( p.x / getScaleFrac() );
        p.y= (int)( p.y / getScaleFrac() );
        return p;
    }
    
    private void resetSize() {
        Dimension size= new Dimension( (int)(image.getWidth()*getScaleFrac()), (int)(image.getHeight()*getScaleFrac()) );
        Dimension parentSize= getParent().getSize();
        if ( parentSize.width > size.width) size.width= parentSize.width;
        if ( parentSize.height > size.height) size.height= parentSize.height;
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        
        repaint();
        revalidate();
    }
    
    protected void doPaint( Point p ) {
        opHistory.pushState( p, "" );
        doPaintInternal(p);
    }
    
    private void doPaintInternal(final Point p) {
        brushLocation= p;
        Point p0= new Point( (int)( p.x / getScaleFrac() ), (int)( p.y / getScaleFrac() ) );
        ImageEditorSupport.applyBrushOperator( image, brush, pixelOp, colorIndicator, p0 );
        Rectangle r= brush.getBounds();
        r.x-=1;
        r.y-=1;
        r.width+=2;
        r.height+=2; // for the tool bounds marker
        r.translate( p0.x, p0.y );
        applyImageToScreen(r,true,true);
        r.width+=1; // for the tool bounds marker
        r.height+=1;
        repaint( r );
    }
    
    /**
     * convenient do the scale operation.
     */
    public void applyImageToScreen( Rectangle r, boolean location, boolean size ) {
        if ( location ) {
            r.x *= getScaleFrac();
            r.y *= getScaleFrac();
        }
        if ( size ) {
            r.width *= getScaleFrac();
            r.height *= getScaleFrac();
        }
    }
    
    protected void setBrushLocation( final Point p ) {
        this.brushLocation= p;
    }
    
    /**
     * Holds value of property brush.
     */
    private BrushDescriptor brush=null;
    
    /**
     * Getter for property brush.
     * @return Value of property brush.
     */
    public BrushDescriptor getBrush() {
        return this.brush;
    }
    
    /**
     * Setter for property brush.
     * @param brush New value of property brush.
     */
    public void setBrush(BrushDescriptor brush) {
        BrushDescriptor oldBrush= this.brush;
        this.brush = brush;
        Rectangle r= brush.getBounds();
        Area brushArea= new Area();
        Point center= new Point( r.width/2, r.height/2 );
        for ( int i=0; i< r.width; i++ ) {
            for ( int j=0; j<r.height; j++ ) {
                if ( brush.getWeight( center, new Point( i, j ) ) > 0.0 ) {
                    brushArea.add( new Area( new Rectangle( i-center.x, j-center.y, 1, 1 ) ) );
                }
            }
        }
        brushOutline= brushArea;
        firePropertyChange( "brush", oldBrush, brush );
        repaint();
    }
    
    /**
     * Holds value of property pixelOp.
     */
    private PixelOperation pixelOp= new PaintPixelOperation();
    
    /**
     * Getter for property pixelOp.
     * @return Value of property pixelOp.
     */
    public PixelOperation getPixelOp() {
        return this.pixelOp;
    }
    
    /**
     * Setter for property pixelOp.
     * @param pixelOp New value of property pixelOp.
     */
    public void setPixelOp(PixelOperation pixelOp) {
        PixelOperation oldPO= this.pixelOp;
        this.pixelOp = pixelOp;
        if ( this.selectionArea!=null ) {
            doPaintSelectionArea();
            selectionArea=null;
            this.pixelOp= oldPO;
        }
        repaint();        
        firePropertyChange( "pixelOp", oldPO, pixelOp );
    }
    
    private ColorIndicator colorIndicator;
    
    public ColorIndicator getColorIndicator() {
        return this.colorIndicator;
    }
    
    public void setColorIndicator(ColorIndicator colorIndicator) {
        this.colorIndicator = colorIndicator;
    }
    
    protected AppHistory getAppHistory() {
        return this.history;
    }
    
    boolean undoPushesState= false;
    
    void undo() {
        if ( undoPushesState ) {
            pushState();
            history.undo();
            undoPushesState=false;
        }
        BufferedImage last= (BufferedImage) history.undo();
        if ( last!=null ) {
            this.image= last;
            repaint();
        }
    }
    
    void pushState() {
        BufferedImage copy= new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB );
        copy.getGraphics().drawImage( image, 0, 0, this );
        history.pushState( copy, "paint" );
        opHistory= new AppHistory(Integer.MAX_VALUE);
        undoPushesState= true;
    }
    
    void redo() {
        BufferedImage next= (BufferedImage) history.redo();
        if ( next!=null ) {
            this.image= next;
            repaint();
        }
    }
    
    void redoPaintOp() {
        if ( opHistory==null ) return;
        BufferedImage last= (BufferedImage) history.peek();
        BufferedImage copy= new BufferedImage( last.getWidth(), last.getHeight(), BufferedImage.TYPE_INT_ARGB );
        copy.getGraphics().drawImage( last, 0, 0, this );
        
        if ( last==null ) return;
        this.image= copy;
        
        opHistory.redo();
        Iterator i= opHistory.peekAll();
        if ( i==null ) return;
        while ( i.hasNext() ) {
            Point p= (Point) i.next();
            doPaintInternal(p);
        }
    }
    
    
    void undoPaintOp() {
        if ( opHistory==null ) return;
        BufferedImage last= (BufferedImage) history.peek();
        BufferedImage copy= new BufferedImage( last.getWidth(), last.getHeight(), BufferedImage.TYPE_INT_ARGB );
        copy.getGraphics().drawImage( last, 0, 0, this );
        
        if ( last==null ) return;
        this.image= copy;
        
        opHistory.undo();
        
        Iterator i= opHistory.peekAll();
        if ( i==null ) return;
        while ( i.hasNext() ) {
            Point p= (Point) i.next();
            doPaintInternal(p);
        }
        
        Point p0= (Point) opHistory.peek();
        Rectangle r= brush.getBounds();
        r.translate( p0.x, p0.y );
        r.x *= getScaleFrac();
        r.y *= getScaleFrac();
        r.width *= getScaleFrac();
        r.height *= getScaleFrac();
        repaint( r );
        
    }
    
    void setSelection(Area area) {
        if ( this.selectionArea!=null && this.selectionArea!=area ) {
            selectionRunnable.keepGoing= false;
        }
        if ( this.selectionArea==area ) return;
        Area oldArea= this.selectionArea;
        this.selectionArea= area;
        if ( this.selectionRunnable!=null ) {
            selectionRunnable.keepGoing= false;
        }
        this.selectionRunnable= new ImageEditorSupport.RepaintTimerRunnable(20, this,area);
        new Thread(selectionRunnable).start();
                
        firePropertyChange( "selection", oldArea, area );
    }
    
    public Area getSelection() {
        return selectionArea;
    }
    
    
    
    void initPixelOp() {
        pixelOp.init( image, colorIndicator.getForegroundColor(), colorIndicator.getBackgroundColor() );
    }
    
    void finishPixelOp() {
        pixelOp.finished( image );
    }
    
    private void doPaintSelectionArea() {
        
        pushState();
        
        Color foregroundColor= colorIndicator.getForegroundColor();
        Color backgroundColor= colorIndicator.getBackgroundColor();
        
        pixelOp.init( image, foregroundColor, backgroundColor );
        
        for ( int i=0; i<image.getWidth(); i++ ) {
            for ( int j=0; j<image.getHeight(); j++ ) {
                if ( selectionArea.contains( i, j ) ) {
                    Color c= pixelOp.applyAt( image, i, j, foregroundColor, backgroundColor );
                    image.setRGB( i,j, c.getRGB() );
                }
            }
        }
        
        pixelOp.finished( image );
    }
    
    Point getBrushLocation() {
        return brushLocation;
    }
    
    Rectangle getBrushDirty() {
        Rectangle dirty= getBrush().getBounds();
        if ( dirty==null ) return null;
        dirty.x-=1;
        dirty.y-=1;
        dirty.height+=2;
        dirty.width+=2;
        Point brushPosition= getBrushLocation();
        if ( brushPosition==null ) return null;
        dirty.x *= getScaleFrac();
        dirty.y *= getScaleFrac();
        dirty.translate( brushPosition.x, brushPosition.y );
        dirty.width*= getScaleFrac();
        dirty.height *= getScaleFrac();
        return dirty;
    }
    
    public void zoomIn() {
        setScale( getScale() + 1 );
    }
    
    public void zoomOut() {
        setScale( getScale() - 1 );
    }

    protected ImageEditorDropTarget getImageEditorDropTarget() {
        return dropTarget;
    }
}
