/*
 * ResizeDialog.java
 *
 * Created on June 7, 2007, 10:09 AM
 */

package com.cottagesystems.imagetool;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author  jbf
 */
public class ResizeDialog extends javax.swing.JPanel {
    
    Dimension initialDim;
    Dimension newDim;
    
    Unit currentUnit;
    
    private boolean isCancelled;
    
    /** Creates new form ResizeDialog */
    public ResizeDialog(  ) {
        initComponents();
        setResize(true);
        initialDim= new Dimension( 100,100 );
        newDim= new Dimension( 100,100 );
        currentUnit= PIXELS;
        updateGui();
    }
    
    public void setInitialDimensions( Dimension d ) {
        this.initialDim= new Dimension( d );
        this.newDim= new Dimension( d );
        updateGui();
    }
    
    public Dimension getDimensions() {
        return newDim;
    }
    
    private interface Unit {
        String format( int initPixel, int newPixel );
        int parse( int initPixel, String value ) throws ParseException;
        NumberFormat getFormat();
    }
    
    private Unit PIXELS= new Unit() {
        public String format(int initPixel, int newPixel) {
            return ""+newPixel;
        }
        
        public int parse(int initPixel, String value) throws ParseException {
            try {
                return Integer.parseInt(value);
            } catch ( NumberFormatException e ) {
                throw new ParseException( "unable to parse: "+value, 0 );
            }
        }
        public String toString() {
            return "pixels";
        }
        public NumberFormat getFormat() {
            return NumberFormat.getIntegerInstance();
        }
        
    };
    
    private Unit PERCENT= new Unit() {
        DecimalFormat percentFormat= new DecimalFormat( "0.0" );
        public String format(int initPixel, int newPixel) {
            return percentFormat.format( 100.*newPixel / initPixel );
        }
        
        public int parse(int initPixel, String value) throws ParseException {
            return (int)( percentFormat.parse(value).doubleValue() / 100 * initPixel );
        }
        public String toString() {
            return "percent";
        }
        
        public NumberFormat getFormat() {
            return NumberFormat.getIntegerInstance();
        }
    };
    
    private Unit DPI75= new Unit() {
        DecimalFormat format= new DecimalFormat( "0.00" );
        public String format(int initPixel, int newPixel) {
            return format.format( newPixel/75. );
        }
        
        public int parse(int initPixel, String value) throws ParseException {
            return (int)(format.parse( value ).doubleValue() * 75);
        }
        public String toString() {
            return "inches@75dpi";
        }
        public NumberFormat getFormat() {
            return NumberFormat.getNumberInstance();
        }
        
    };
    
    private void updateGui() {
        widthTextField.setFormatterFactory( new DefaultFormatterFactory( new NumberFormatter(currentUnit.getFormat()) ) );
        heightTextField.setFormatterFactory( new DefaultFormatterFactory( new NumberFormatter(currentUnit.getFormat()) ) );
        widthTextField.setText( currentUnit.format( initialDim.width, newDim.width ) );
        heightTextField.setText( currentUnit.format( initialDim.height, newDim.height ) );
    }
    
    private void parseWidth() {
        try {
            newDim.width= currentUnit.parse( initialDim.width, widthTextField.getText() );
            widthTextField.setBackground( Color.WHITE );
            widthTextField.setToolTipText(null);
            if ( constrainProportionsCheckBox.isSelected() ) {
                double aspect= 1.0 * initialDim.height / initialDim.width;
                newDim.height= (int)( newDim.width * aspect );
                updateGui();
            }
        } catch ( ParseException e ) {
            widthTextField.setBackground( Color.WHITE );
            widthTextField.setToolTipText(null);
        }
    }
    
    private void parseHeight() {
        try {
            newDim.height= currentUnit.parse( initialDim.height, heightTextField.getText() );
            heightTextField.setBackground( Color.WHITE );
            heightTextField.setToolTipText(null);
            if ( constrainProportionsCheckBox.isSelected() ) {
                double aspect= 1.0 * initialDim.height / initialDim.width;
                newDim.width= (int)( newDim.height /  aspect );
                updateGui();
            }
        } catch ( ParseException e ) {
            heightTextField.setBackground( Color.YELLOW );
            heightTextField.setToolTipText("Parse Error");
        }
        
    }
    
    public boolean isCancelled() {
        return isCancelled;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        inputTypeLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        constrainProportionsCheckBox = new javax.swing.JCheckBox();
        unitsComboBox = new javax.swing.JComboBox();
        widthTextField = new javax.swing.JFormattedTextField();
        heightTextField = new javax.swing.JFormattedTextField();

        inputTypeLabel.setText("Resize Image:");

        jLabel2.setText("Width:");

        jLabel3.setText("Height:");

        widthTextField.setText("jTextField1");
        widthTextField.setMinimumSize(new java.awt.Dimension(100, 100));
        widthTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widthTextFieldActionPerformed(evt);
            }
        });
        widthTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                widthTextFieldFocusLost(evt);
            }
        });

        heightTextField.setText("jTextField1");
        heightTextField.setMinimumSize(new java.awt.Dimension(60, 20));
        heightTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heightTextFieldActionPerformed(evt);
            }
        });
        heightTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                heightTextFieldFocusLost(evt);
            }
        });

        constrainProportionsCheckBox.setSelected(true);
        constrainProportionsCheckBox.setText("constrain proporations");
        constrainProportionsCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        constrainProportionsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        unitsComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unitsComboBoxActionPerformed(evt);
            }
        });

        widthTextField.setText("jFormattedTextField1");
        widthTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                widthTextFieldActionPerformed(evt);
            }
        });
        widthTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                widthTextFieldFocusLost(evt);
            }
        });

        heightTextField.setText("jFormattedTextField2");
        heightTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heightTextFieldActionPerformed(evt);
            }
        });
        heightTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                heightTextFieldFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(constrainProportionsCheckBox))
                    .add(inputTypeLabel)
                    .add(layout.createSequentialGroup()
                        .add(8, 8, 8)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(heightTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(widthTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(unitsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .add(91, 91, 91))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(inputTypeLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(constrainProportionsCheckBox)
                .add(11, 11, 11)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(widthTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(unitsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(heightTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(89, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void heightTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heightTextFieldActionPerformed
        parseHeight();
    }//GEN-LAST:event_heightTextFieldActionPerformed

    private void widthTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_widthTextFieldActionPerformed
        parseWidth();
    }//GEN-LAST:event_widthTextFieldActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        isCancelled= true;
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        isCancelled= false;
    }//GEN-LAST:event_okButtonActionPerformed
    
    private void unitsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unitsComboBoxActionPerformed
        currentUnit= (ResizeDialog.Unit) unitsComboBox.getSelectedItem();
        updateGui();
    }//GEN-LAST:event_unitsComboBoxActionPerformed
    
    private void heightTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_heightTextFieldFocusLost
        parseHeight();
    }//GEN-LAST:event_heightTextFieldFocusLost
    
    private void widthTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_widthTextFieldFocusLost
        parseWidth();
    }//GEN-LAST:event_widthTextFieldFocusLost
    
    void setResize(boolean b) {
        if ( b ) {
            currentUnit= PERCENT;
            DefaultComboBoxModel model= new DefaultComboBoxModel();
            model.addElement( PIXELS );
            model.addElement( PERCENT );
            model.addElement( DPI75 );
            unitsComboBox.setModel(model);
            constrainProportionsCheckBox.setVisible(true);
            inputTypeLabel.setText( "Resize Image:" );
        } else {
            currentUnit= PIXELS;
            DefaultComboBoxModel model= new DefaultComboBoxModel();
            model.addElement( PIXELS );
            model.addElement( DPI75 );
            unitsComboBox.setModel(model);            
            constrainProportionsCheckBox.setVisible(false);
            constrainProportionsCheckBox.setSelected(false);
            inputTypeLabel.setText( "Image Size:" );
        }
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox constrainProportionsCheckBox;
    private javax.swing.JFormattedTextField heightTextField;
    private javax.swing.JLabel inputTypeLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JComboBox unitsComboBox;
    private javax.swing.JFormattedTextField widthTextField;
    // End of variables declaration//GEN-END:variables
    
    }
