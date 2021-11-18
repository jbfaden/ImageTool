package com.cottagesystems.imagetool;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
public final class ImageEditorTopComponent extends TopComponent {
    
    private static ImageEditorTopComponent instance;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    
    private static final String PREFERRED_ID = "ImageEditorTopComponent";
    
    
    private ImageEditorTopComponent() {
        initComponents();
        imageEditorPanel1.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                updateStatus();
            }
        });
        backgroundComboBox.setModel( new DefaultComboBoxModel<>(  ImageEditorPanel.BackgroundTextureType.values() ) );
        
        for ( int i=-5; i<7; i++ ) {
            final int finalI= i;
            scaleMenu.add( new JMenuItem( new AbstractAction( ImageEditorPanel.getScaleString(i) ) {
                public void actionPerformed( ActionEvent e ) {
                    imageEditorPanel1.setScale(finalI);
                }
            } ) );
        }
        
        imageEditorPanel1.addMouseMotionListener( new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if ( e.isShiftDown() ) {
                    mouseClickPosition= imageEditorPanel1.scalePoint( e.getPoint() );
                    mousePosition= imageEditorPanel1.scalePoint( e.getPoint() );
                } else {
                    mousePosition= imageEditorPanel1.scalePoint( e.getPoint() );
                }
                updateStatusTimer.restart();
            }
        });
        
        imageEditorPanel1.getImageEditorDropTarget().setImageEditorTopComponent(this); // kludge
        
        updateStatusTimer.setRepeats(false);
        
        menuBarPanel.add( jMenuBar1 );
        menuBarPanel.revalidate();
        DefaultComboBoxModel model= new DefaultComboBoxModel();
        model.addElement(new PaintPixelOperation());
        model.addElement(new ErasePixelOperation());
        model.addElement(new MagicErasePixelOperation());
        model.addElement(new MagicPaintPixelOperation());
        model.addElement(new SelectionPixelOperation(imageEditorPanel1));
        pixelOpComboBox.setModel(model);
        setName(NbBundle.getMessage(ImageEditorTopComponent.class, "CTL_ImageEditorTopComponent"));
        setToolTipText(NbBundle.getMessage(ImageEditorTopComponent.class, "HINT_ImageEditorTopComponent"));
        revertImage();
    }
    
    private Timer updateStatusTimer= new Timer( 100,new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            updateStatus();
        } 
    });

    private void updateStatus() {
        StringBuffer label= new StringBuffer();
        label.append("<html>");
        BufferedImage image= this.imageEditorPanel1.getImage();
        label.append("x:"+image.getWidth()+" y:"+image.getHeight()+"  scale:"+imageEditorPanel1.getScaleString((int)imageEditorPanel1.getScale()));
        label.append("<br>");
        label.append("tool:"+imageEditorPanel1.getPixelOp()+"  brush: "+imageEditorPanel1.getBrush() );
        label.append("<br>");
        label.append("mouse: " + mousePosition.getX() + ","+mousePosition.getY() );
        label.append("<br>");
        Point p= mousePosition.getLocation();
        p.translate( -mouseClickPosition.x, -mouseClickPosition.y );
        label.append("from mouse with shift: " + p.getX() + ","+p.getY() );
        
        Area selection= imageEditorPanel1.getSelection();
        if (selection!=null ) label.append("Selection:"+selection.getBounds()+"<br>" );
        label.append("</html>");
        statusLabel.setText( label.toString() );
        statusLabel.repaint();
    }
    
    private void revertImage() {
        BufferedImage image;
        try {
            //image = ImageIO.read(new URL("http://www.sarahandjeremy.net/%7Ejbf/family/photoServer.php?image=/20060815_bday1/P1010011.JPG&width=500&rotate=0"));
            //image = ImageIO.read(new URL("http://entropymine.com/jason/testbed/pngtrans/rgba8.png"));
            image = ImageIO.read( ImageEditorTopComponent.class.getResource("/com/cottagesystems/imagetool/resources/badge_ok.png") );
            //image = ImageIO.read(new URL("file:///j:/images/result_mag.png"));
            BufferedImage im1= new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB );
            im1.getGraphics().drawImage( image, 0, 0, imageEditorPanel1 );
            image= im1;
            System.err.println(image.getColorModel());
            imageEditorPanel1.setImage(image);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        imageEditorPanel1.setColorIndicator( colorIndicator1 );
    }
    
    public void loadFile( File file ) {
        BufferedImage image;
        try {
            image = ImageIO.read(file);
            BufferedImage im1= new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB );
            im1.getGraphics().drawImage( image, 0, 0, imageEditorPanel1 );
            image= im1;
            imageEditorPanel1.setImage(image);
            setCurrentFile(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void loadFile() {
        String saveDir= Preferences.userNodeForPackage( this.getClass() ).get( "saveDirectory", System.getProperty("user.home" ) ) ;
        String dir= Preferences.userNodeForPackage( this.getClass() ).get( "loadDirectory", saveDir ) ;
        
        JFileChooser chooser= new JFileChooser(new File(dir));
        chooser.setFileFilter( new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".png") || f.getName().toLowerCase().endsWith(".jpg" )|| f.getName().toLowerCase().endsWith(".gif" );
            }
            public String getDescription() {
                return "png,jpg,gif file";
            }
            
        });
        int result= chooser.showOpenDialog(this);
        if ( result==JFileChooser.APPROVE_OPTION ) {
            Preferences.userNodeForPackage( this.getClass() ).put( "loadDirectory", chooser.getCurrentDirectory().toString() );
            BufferedImage image;
            
            loadFile( chooser.getSelectedFile() );
        }
    }
    
    public void saveAsFile() {
        
        String dir= Preferences.userNodeForPackage( this.getClass() ).get( "loadDirectory", System.getProperty("user.home" ) ) ;
        dir= Preferences.userNodeForPackage( this.getClass() ).get( "saveDirectory", dir ) ;
        
        JFileChooser chooser= new JFileChooser( new File( dir ) );
        
        chooser.setFileFilter( new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".png"); // || f.getName().endsWith(".jpg" )|| f.getName().endsWith(".gif" );
            }
            public String getDescription() {
                return "png file";
            }
            
        });
        int result= chooser.showSaveDialog(this);
        if ( result==JFileChooser.APPROVE_OPTION ) {
            Preferences.userNodeForPackage( this.getClass() ).put( "saveDirectory", chooser.getCurrentDirectory().toString() );
            saveFile( chooser.getSelectedFile() );
        }
    }
    
    public void saveFile( File name ) {
        BufferedImage image= imageEditorPanel1.getImage();
        try {
            String s= name.toString();
            String defaultExt= "png";
            if ( s.length()<4 ) s+="."+defaultExt;
            String ext= s.substring( s.length()-4 ).toLowerCase();
            if (!ext.equals("."+defaultExt)) {
                s+="."+defaultExt;
            }
            ext=defaultExt;
            
            ImageIO.write( image, ext, new File( s ) );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
    }
    
    public void aquireWholeScreen() {
        Runnable run= new Runnable() {
            public void run() {
                BufferedImage image;
                try {
                    image = ToolSupport.getWholeScreenShot();
                    imageEditorPanel1.setImage(image);
                } catch (AWTException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        new Thread(run).start();
    }
    
    public void aquirePartScreen() {
        Runnable run= new Runnable() {
            public void run() {
                BufferedImage image;
                try {
                    
                    image = ToolSupport.getPartialScreenShot( null );
                    imageEditorPanel1.setImage(image);
                } catch (AWTException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        new Thread(run).start();
    }
    

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openFilejMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        aquireMenu = new javax.swing.JMenu();
        wholeScreenItem2 = new javax.swing.JMenuItem();
        regionItem2 = new javax.swing.JMenuItem();
        imageMenu = new javax.swing.JMenu();
        resizeMenuItem = new javax.swing.JMenuItem();
        cropMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        zoomInMenuItem = new javax.swing.JMenuItem();
        zoomoutMenuItem = new javax.swing.JMenuItem();
        actualPixelsMenuItem = new javax.swing.JMenuItem();
        scaleMenu = new javax.swing.JMenu();
        jScrollPane1 = new javax.swing.JScrollPane();
        imageEditorPanel1 = new com.cottagesystems.imagetool.ImageEditorPanel();
        menuBarPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        colorIndicator1 = new com.cottagesystems.imagetool.ColorIndicator();
        oneToOneButton = new javax.swing.JButton();
        paintButton = new javax.swing.JButton();
        eraseButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        pixelOpComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        backgroundComboBox = new javax.swing.JComboBox();
        statusPanel = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(fileMenu, "File");
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(newMenuItem, "New...");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);

        org.openide.awt.Mnemonics.setLocalizedText(openFilejMenuItem, "Open File...");
        openFilejMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFilejMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openFilejMenuItem);

        org.openide.awt.Mnemonics.setLocalizedText(saveMenuItem, "Save");
        saveMenuItem.setEnabled(false);
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        org.openide.awt.Mnemonics.setLocalizedText(saveAsMenuItem, "Save As...");
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);

        org.openide.awt.Mnemonics.setLocalizedText(aquireMenu, "Aquire...");

        org.openide.awt.Mnemonics.setLocalizedText(wholeScreenItem2, "whole screen snapshot");
        wholeScreenItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wholeScreenItem2ActionPerformed(evt);
            }
        });
        aquireMenu.add(wholeScreenItem2);

        org.openide.awt.Mnemonics.setLocalizedText(regionItem2, "region snapshot");
        regionItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regionItem2ActionPerformed(evt);
            }
        });
        aquireMenu.add(regionItem2);

        fileMenu.add(aquireMenu);

        jMenuBar1.add(fileMenu);

        org.openide.awt.Mnemonics.setLocalizedText(imageMenu, "Image");

        org.openide.awt.Mnemonics.setLocalizedText(resizeMenuItem, "Resize...");
        resizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeMenuItemActionPerformed(evt);
            }
        });
        imageMenu.add(resizeMenuItem);

        org.openide.awt.Mnemonics.setLocalizedText(cropMenuItem, "Crop to Selection");
        cropMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cropMenuItemActionPerformed(evt);
            }
        });
        imageMenu.add(cropMenuItem);

        jMenuBar1.add(imageMenu);

        org.openide.awt.Mnemonics.setLocalizedText(viewMenu, "View");
        viewMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewMenuActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(zoomInMenuItem, "Zoom In");
        zoomInMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(zoomInMenuItem);

        org.openide.awt.Mnemonics.setLocalizedText(zoomoutMenuItem, "Zoom Out");
        zoomoutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomoutMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(zoomoutMenuItem);

        org.openide.awt.Mnemonics.setLocalizedText(actualPixelsMenuItem, "Actual Pixels (1:1)");
        actualPixelsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actualPixelsMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(actualPixelsMenuItem);

        org.openide.awt.Mnemonics.setLocalizedText(scaleMenu, "Set Scale");
        scaleMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleMenuActionPerformed(evt);
            }
        });
        viewMenu.add(scaleMenu);

        jMenuBar1.add(viewMenu);

        org.jdesktop.layout.GroupLayout imageEditorPanel1Layout = new org.jdesktop.layout.GroupLayout(imageEditorPanel1);
        imageEditorPanel1.setLayout(imageEditorPanel1Layout);
        imageEditorPanel1Layout.setHorizontalGroup(
            imageEditorPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 591, Short.MAX_VALUE)
        );
        imageEditorPanel1Layout.setVerticalGroup(
            imageEditorPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 486, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(imageEditorPanel1);

        menuBarPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        menuBarPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        org.jdesktop.layout.GroupLayout colorIndicator1Layout = new org.jdesktop.layout.GroupLayout(colorIndicator1);
        colorIndicator1.setLayout(colorIndicator1Layout);
        colorIndicator1Layout.setHorizontalGroup(
            colorIndicator1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 76, Short.MAX_VALUE)
        );
        colorIndicator1Layout.setVerticalGroup(
            colorIndicator1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 30, Short.MAX_VALUE)
        );

        oneToOneButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cottagesystems/imagetool/resources/oneToOne.png"))); // NOI18N
        oneToOneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneToOneButtonActionPerformed(evt);
            }
        });

        paintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cottagesystems/imagetool/resources/brush.png"))); // NOI18N
        paintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paintButtonActionPerformed(evt);
            }
        });

        eraseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/cottagesystems/imagetool/resources/eraser.png"))); // NOI18N
        eraseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eraseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Brush Operation:");

        pixelOpComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pixelOpComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Brush Shape:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Round 3", "Round 5", "Round 9", "Round 15", "Square 1", "Square 3", "Square 5", "Square 9", "Square 15" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "Background:");

        backgroundComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "checkers", "black", "white" }));
        backgroundComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backgroundComboBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pixelOpComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jComboBox1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(backgroundComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(colorIndicator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(paintButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(eraseButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(oneToOneButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jLabel2)
                            .add(jLabel3))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {eraseButton, oneToOneButton, paintButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(colorIndicator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(15, 15, 15)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(paintButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .add(eraseButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .add(oneToOneButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pixelOpComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(backgroundComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {eraseButton, oneToOneButton, paintButton}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.openide.awt.Mnemonics.setLocalizedText(statusLabel, "w: 0 x h: 0 scale: 1");
        statusLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1)
                    .add(statusPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(menuBarPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(menuBarPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(statusPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        saveFile( currentFile );
    }//GEN-LAST:event_saveMenuItemActionPerformed
        
    private void cropMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cropMenuItemActionPerformed
        imageEditorPanel1.support.crop();
    }//GEN-LAST:event_cropMenuItemActionPerformed

    private void actualPixelsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualPixelsMenuItemActionPerformed
        imageEditorPanel1.setScale(1);
    }//GEN-LAST:event_actualPixelsMenuItemActionPerformed
    
    private void viewMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewMenuActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_viewMenuActionPerformed
    
    private void scaleMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleMenuActionPerformed
        
    }//GEN-LAST:event_scaleMenuActionPerformed
    
    private void zoomoutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomoutMenuItemActionPerformed
        this.imageEditorPanel1.zoomOut();
    }//GEN-LAST:event_zoomoutMenuItemActionPerformed
    
    private void zoomInMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInMenuItemActionPerformed
        this.imageEditorPanel1.zoomIn();
    }//GEN-LAST:event_zoomInMenuItemActionPerformed
    
    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        saveAsFile();
    }//GEN-LAST:event_saveAsMenuItemActionPerformed
    
    private void openFilejMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFilejMenuItemActionPerformed
        loadFile();
    }//GEN-LAST:event_openFilejMenuItemActionPerformed
    
    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        ResizeDialog rd= new ResizeDialog();
        rd.setResize(false);
        int r= JOptionPane.showConfirmDialog( this, rd, "New Image Size", JOptionPane.OK_CANCEL_OPTION );
        if ( r==JOptionPane.OK_OPTION ) {
            Dimension d= rd.getDimensions();
            BufferedImage newImage= new BufferedImage( (int) d.getWidth(), (int)d.getHeight(), BufferedImage.TYPE_INT_ARGB );
            Graphics g= newImage.getGraphics();
            g.setColor( imageEditorPanel1.getColorIndicator().getBackgroundColor() );
            g.fillRect( 0, 0, (int) d.getWidth(), (int) d.getHeight() );
            imageEditorPanel1.setImage( newImage );
        }
    }//GEN-LAST:event_newMenuItemActionPerformed
    
    private void oneToOneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneToOneButtonActionPerformed
        imageEditorPanel1.setScale(1.0f);
    }//GEN-LAST:event_oneToOneButtonActionPerformed
    
    private void eraseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eraseButtonActionPerformed
        imageEditorPanel1.setPixelOp( new ErasePixelOperation() );
        pixelOpComboBox.setSelectedIndex(1);
    }//GEN-LAST:event_eraseButtonActionPerformed
    
    private void paintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paintButtonActionPerformed
        imageEditorPanel1.setPixelOp( new PaintPixelOperation() );
        pixelOpComboBox.setSelectedIndex(0);
    }//GEN-LAST:event_paintButtonActionPerformed
    
    private void resizeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeMenuItemActionPerformed
        ResizeDialog rd= new ResizeDialog();
        rd.setInitialDimensions( new Dimension( imageEditorPanel1.getImage().getWidth(), imageEditorPanel1.getImage().getHeight() ) );
        
        int i= JOptionPane.showConfirmDialog( this, rd, "Select New Image Size", JOptionPane.OK_CANCEL_OPTION );
        if ( i==JOptionPane.OK_OPTION ) {
            BufferedImage newImage= ToolSupport.resizeImage( imageEditorPanel1.getImage(), rd.getDimensions() );
            imageEditorPanel1.setImage( newImage );
        }
    }//GEN-LAST:event_resizeMenuItemActionPerformed
    
    private void regionItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regionItem2ActionPerformed
        aquirePartScreen();
    }//GEN-LAST:event_regionItem2ActionPerformed
    
    private void wholeScreenItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wholeScreenItem2ActionPerformed
        aquireWholeScreen();
    }//GEN-LAST:event_wholeScreenItem2ActionPerformed
    
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        saveAsFile();
    }//GEN-LAST:event_jMenuItem3ActionPerformed
    
    private void pixelOpComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pixelOpComboBoxActionPerformed
        Object select= pixelOpComboBox.getSelectedItem();
        imageEditorPanel1.setPixelOp( (PixelOperation)select);
        pixelOpComboBox.setSelectedItem( imageEditorPanel1.getPixelOp() );
    }//GEN-LAST:event_pixelOpComboBoxActionPerformed
    
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        loadFile();
    }//GEN-LAST:event_jMenuItem1ActionPerformed
    
    private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_fileMenuActionPerformed
    
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        int idx= jComboBox1.getSelectedIndex();
        BrushDescriptor brush;
        if ( idx<4 ) {
            int[] sizes= new int[] { 3, 5, 9, 15 };
            brush= new RoundBrushDescriptor( sizes[idx]/2 );
        } else {
            int[] sizes= new int[] { 1, 3, 5, 9, 15 };
            brush= new SquareBrushDescriptor( sizes[idx-4]/2 );
        }
        imageEditorPanel1.setBrush(brush);
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void backgroundComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backgroundComboBoxActionPerformed
        ImageEditorPanel.BackgroundTextureType bb= (ImageEditorPanel.BackgroundTextureType)backgroundComboBox.getSelectedItem();
        imageEditorPanel1.setBackgroundTexture( bb );
        imageEditorPanel1.repaint();
    }//GEN-LAST:event_backgroundComboBoxActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem actualPixelsMenuItem;
    private javax.swing.JMenu aquireMenu;
    private javax.swing.JComboBox backgroundComboBox;
    private com.cottagesystems.imagetool.ColorIndicator colorIndicator1;
    private javax.swing.JMenuItem cropMenuItem;
    private javax.swing.JButton eraseButton;
    private javax.swing.JMenu fileMenu;
    private com.cottagesystems.imagetool.ImageEditorPanel imageEditorPanel1;
    private javax.swing.JMenu imageMenu;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel menuBarPanel;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JButton oneToOneButton;
    private javax.swing.JMenuItem openFilejMenuItem;
    private javax.swing.JButton paintButton;
    private javax.swing.JComboBox pixelOpComboBox;
    private javax.swing.JMenuItem regionItem2;
    private javax.swing.JMenuItem resizeMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenu scaleMenu;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JMenuItem wholeScreenItem2;
    private javax.swing.JMenuItem zoomInMenuItem;
    private javax.swing.JMenuItem zoomoutMenuItem;
    // End of variables declaration//GEN-END:variables
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link findInstance}.
     */
    public static synchronized ImageEditorTopComponent getDefault() {
        if (instance == null) {
            instance = new ImageEditorTopComponent();
        }
        return instance;
    }
    
    /**
     * Obtain the ImageEditorTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized ImageEditorTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find ImageEditor component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof ImageEditorTopComponent) {
            return (ImageEditorTopComponent)win;
        }
        ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }
    
    public void componentOpened() {
        // TODO add custom code on component opening
    }
    
    public void componentClosed() {
        // TODO add custom code on component closing
    }
    
    /** replaces this in object stream */
    public Object writeReplace() {
        return new ResolvableHelper();
    }
    
    protected String preferredID() {
        return PREFERRED_ID;
    }
    
    final static class ResolvableHelper implements Serializable {
        private static final long serialVersionUID = 1L;
        public Object readResolve() {
            return ImageEditorTopComponent.getDefault();
        }
    }
    
    private Point mousePosition= new Point(0,0);
    private Point mouseClickPosition= new Point(0,0);
    
    /**
     * Holds value of property currentFile.
     */
    private File currentFile;
    
    /**
     * Getter for property currentFile.
     * @return Value of property currentFile.
     */
    public File getCurrentFile() {
        return this.currentFile;
    }
    
    /**
     * Setter for property currentFile.
     * @param currentFile New value of property currentFile.
     */
    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
        saveMenuItem.setEnabled( currentFile!=null );
    }
    
}
