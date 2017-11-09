/*
 * ColorIndicator.java
 *
 * Created on December 13, 2006, 9:15 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.util.prefs.Preferences;
import javax.swing.JPanel;

/**
 *
 * @author jbf
 */
public class ColorIndicator extends JPanel {
    
    public static final int SELECT_FOREGROUND=1;
    public static final int SELECT_BACKGROUND=2;
    private int selected= SELECT_FOREGROUND;
    
    Color[] palette= new Color[12];
    static int PSIZE=10;
    static int POFFX=36;
    static int POFFY=0;
    
    public static String toHexString( Color c ) {
        return "0x"+ Integer.toHexString( c.getRGB() ).substring(2).toUpperCase();
    }
    
    final static TexturePaint bgPaint= Util.getBgPaint();
    
    /** Creates a new instance of ColorIndicator */
    public ColorIndicator() {
        Dimension size= new Dimension( 76,30 );
        setPreferredSize( size );
        setMaximumSize( size );
        setMinimumSize( size );
        ColorIndicatorMouseAdapter mia= new ColorIndicatorMouseAdapter( this );
        addMouseListener( mia );
        addMouseMotionListener( mia );
        addKeyListener(new ColorIndicatorKeyListener(this));
        setFocusable(true);
        setOpaque( true );
        
        StringBuffer dp=new StringBuffer();
        dp.append( toHexString( Color.WHITE ) + "," );
        dp.append( toHexString( Color.RED.brighter() )+ "," );
        dp.append( toHexString( Color.GREEN.brighter())+ "," );
        dp.append( toHexString(  Color.BLUE.brighter())+ "," );
        dp.append( toHexString( Color.GRAY)+ "," );
        dp.append( toHexString(  Color.RED)+ "," );
        dp.append( toHexString( Color.GREEN)+ "," );
        dp.append( toHexString(  Color.BLUE)+ "," );
        dp.append( toHexString(  Color.BLACK)+ "," );
        dp.append( toHexString(  Color.RED.darker())+ "," );
        dp.append( toHexString(  Color.GREEN.darker())+ "," );
        dp.append( toHexString( Color.BLUE.darker())+ "," );
        
        String ps= Preferences.userNodeForPackage( this.getClass() ).get( "palette", dp.toString() );
        setPalette(ps);
    }
    
    private void setPalette( String s ) {
        String[] ss= s.split(",");
        for ( int i=0;i<12; i++ ) {
            palette[i]= Color.decode(ss[i] );
        }
    }
    
    private String getPalette() {
        StringBuffer dp= new StringBuffer(); 
        for ( int i=0;i<12; i++ ) {
            dp.append( toHexString( palette[i] ) + "," );
        }
        return dp.toString();
    }
    
    public void paintComponent( Graphics g1 ) {
        Graphics2D g= (Graphics2D)g1;
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setColor( getBackground() );
        g.fillRect( 0, 0, getWidth(), getHeight() );
        
        g.setPaint( bgPaint );
        g.fillRoundRect( 10, 10, 20, 20, 7, 7 );
        g.setColor( backgroundColor );
        g.fillRoundRect( 10, 10, 20, 20, 7, 7 );
        if ( selected==SELECT_BACKGROUND ) {
            g.setColor( Color.DARK_GRAY );
            g.drawRoundRect(10, 10, 19, 19, 7, 7 );
        }
        
        g.setPaint( bgPaint );
        g.fillRoundRect( 0, 0, 20, 20, 7, 7 );
        
        g.setColor( foregroundColor );
        g.fillRoundRect( 0, 0, 20, 20, 7, 7 );
        if ( selected==SELECT_FOREGROUND ) {
            g.setColor( Color.DARK_GRAY );
            g.drawRoundRect( 0, 0, 19, 19, 7, 7 );
        }
        
        for ( int j=0;j<3;j++ ) {
            for ( int i=0;i<4;i++ ) {
                g.setColor( palette[j*4+i]);
                Rectangle r= new Rectangle( POFFX + i*PSIZE, POFFY+j*PSIZE, PSIZE-1, PSIZE-1 );
                g.fill( r );
                if ( selected-3 == j*4+i ) {
                    g.setColor( Color.DARK_GRAY );
                    g.drawRect( r.x-1,r.y-1,r.width+1,r.height+1  );
                }
            }
        }
        
    }
    
    
    /**
     * Holds value of property foregroundColor.
     */
    private Color foregroundColor= Color.BLUE;
    
    /**
     * Getter for property foreground.
     * @return Value of property foreground.
     */
    public Color getForegroundColor() {
        return this.foregroundColor;
    }
    
    /**
     * Setter for property foreground.
     * @param foreground New value of property foreground.
     */
    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        repaint();
    }
    
    /**
     * Holds value of property backgroundColor.
     */
    private Color backgroundColor= Color.WHITE;
    
    /**
     * Getter for property background.
     * @return Value of property background.
     */
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }
    
    /**
     * Setter for property background.
     * @param background New value of property background.
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        repaint();
    }
    
    public Color getSelectedColor() {
        if ( selected==SELECT_FOREGROUND ) {
            return foregroundColor;
        } else if ( selected==SELECT_BACKGROUND ) {
            return backgroundColor;
        } else if ( selected>2 ) {
            return this.palette[selected-3];
        } else {
            return null;
        }
    }
    
    void setSelectedColor(Color c) {
        if ( selected==SELECT_FOREGROUND ) {
            this.foregroundColor=c;
        } else if ( selected==SELECT_BACKGROUND ) {
            this.backgroundColor=c;
        } else if ( selected>2 ) {
            this.palette[selected-3]= c;
            String ps=getPalette();
            Preferences.userNodeForPackage( this.getClass() ).put( "palette", ps );
        }
        repaint();
    }
    
    /**
     * identifies the color at the point
     */
    protected int getEditColor( Point p ) {
        int edit=0;  // 1==fore 2==back;
        if ( p.x<20 && p.y<20 ) {
            edit=SELECT_FOREGROUND;
        } else if ( p.x>10 && p.x<30 && p.y>10 ) {
            edit=SELECT_BACKGROUND;
        } else {
            int j= ( p.y - POFFY ) / PSIZE ;
            int i= ( p.x - POFFX ) / PSIZE;
            if ( i<4 && i>=0 && j<3 && j>=0 ) {
                edit= 3 + j*4 + i;
            }
        }
        return edit;
    }
    
    public void setSelected( int select ) {
        if ( select>0 ) {
            this.selected= select;
            repaint();
        }
    }
    
}
