/*
 * $Id: PropertyPanel.java,v 1.2 2007/06/16 09:03:06 fermich Exp $
 */

package pl.fermich.jkomar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class PropertyPanel extends JPanel {


    private JPanel pathPanel;
    private JPanel dataSizePanel;

    private JSpinner dataSizeMB;
    private JSpinner dataSizeKB;
    private JSpinner dataSizeB;
    private JSpinner msgLen;
    private JTextField pathField;
    private JButton dirButton;
    private boolean CONNECTED;

    public PropertyPanel() {
        setLayout(new BorderLayout());

        CONNECTED = false;

        JPanel north = new JPanel(new GridLayout(3, 1));

        SpinnerNumberModel dsMBmodel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE / (1024 * 1024), 1);
        SpinnerNumberModel dsKBmodel = new SpinnerNumberModel(50, 0, Integer.MAX_VALUE / 1024, 1);
        SpinnerNumberModel dsBmodel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);

        dataSizeMB = new JSpinner(dsMBmodel);
        dataSizeKB = new JSpinner(dsKBmodel);
        dataSizeB = new JSpinner(dsBmodel);

        dataSizePanel = new JPanel(new FlowLayout());
        dataSizePanel.add(dataSizeMB);
        dataSizePanel.add(new JLabel("MB "));
        dataSizePanel.add(dataSizeKB);
        dataSizePanel.add(new JLabel("KB "));
        dataSizePanel.add(dataSizeB);
        dataSizePanel.add(new JLabel("B "));
        dataSizePanel.setBorder(BorderFactory.createTitledBorder("PacketSize"));

        JPanel msgLenPanel = new JPanel();
        SpinnerNumberModel mlmodel = new SpinnerNumberModel(1024, 0, Integer.MAX_VALUE, 5);
        msgLen = new JSpinner(mlmodel);
        msgLenPanel.add(msgLen);
        msgLenPanel.setBorder(BorderFactory.createTitledBorder("MessageLength"));

        pathPanel = new JPanel();
        pathField = new JTextField("", 30);
        dirButton = new JButton("Select");
        dirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Open directory");
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    pathField.setText(file.getAbsolutePath() + File.separator);
                }
            }
        });
        pathPanel.add(pathField);
        pathPanel.add(dirButton);
        pathPanel.setBorder(BorderFactory.createTitledBorder("DownloadPath"));

        north.add(dataSizePanel);
        north.add(msgLenPanel);
        north.add(pathPanel);

        add(north, BorderLayout.NORTH);

        JPanel savePanel = new JPanel();

        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (CONNECTED == false) {
                    int ds = 0;
                    ds = ((Integer) dataSizeMB.getValue()) * 1024 * 1024 + ((Integer) dataSizeKB.getValue()) * 1024 + (Integer) dataSizeB.getValue();
                    if (ds > 0)
                        Property.DATA_SIZE = ds;
                    else {
                        dataSizeMB.setValue(0);
                        dataSizeKB.setValue(10);
                        dataSizeB.setValue(0);
                        Property.DATA_SIZE = 10 * 1024;
                    }
                    Property.DL_PATH = pathField.getText();
                }
                int ms = (Integer) msgLen.getValue();
                if (ms > 0)
                    Property.MSG_LEN = ms;
                else {
                    msgLen.setValue(1024);
                    Property.MSG_LEN = 1024;
                }
                JOptionPane.showMessageDialog(new JFrame(), "Properties saved!");
            }
        });

        savePanel.add(save);
        add(savePanel, BorderLayout.SOUTH);
    }


    public void disableProperties() {
        dataSizeMB.setEnabled(false);
        dataSizeKB.setEnabled(false);
        dataSizeB.setEnabled(false);
        pathField.setEnabled(false);
        dirButton.setEnabled(false);
        CONNECTED = true;
    }


    public void enableProperties() {
        dataSizeMB.setEnabled(true);
        dataSizeKB.setEnabled(true);
        dataSizeB.setEnabled(true);
        pathField.setEnabled(true);
        dirButton.setEnabled(true);
        CONNECTED = false;
    }
}
