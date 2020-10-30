/*
 * $Id: SharedPanel.java,v 1.2 2007/06/16 09:03:06 fermich Exp $
 */

package pl.fermich.jkomar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class SharedPanel extends JPanel {

    private JList sharedList;
    private DefaultListModel sharedlm;
    private DualOutputStream dos;
    private DualInputStream dis;

    public SharedPanel(DualOutputStream dOS, DualInputStream dIS) {
        setLayout(new BorderLayout());

        dos = dOS;
        dis = dIS;

        sharedlm = new DefaultListModel();

        sharedList = new JList(sharedlm);
        sharedList.setCellRenderer(new ShaCellRenderer());
        sharedList.setBackground(this.getBackground());


        ActionListener checkChanges = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sharedList.setEnabled(false);
                sharedList.setEnabled(true);
            }
        };

        new Timer(5000, checkChanges).start();

        JButton button;

        java.net.URL url;
        ImageIcon buttonIcon;

        JToolBar sharedBar = new JToolBar();
        sharedBar.setFloatable(false);

        url = this.getClass().getResource("/graph/new.gif");
        buttonIcon = new ImageIcon(url);
        button = new JButton("", buttonIcon);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Open file");

                int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        FilePacketReader fpReader = new FilePacketReader(file);
                        sharedlm.addElement(fpReader);    //add object to list
                        dos.addPacketReader(fpReader);    //add object to dual output- packets transport object
                        dis.addFilePacketReader(fpReader);    //listening for start/stop/pause in DualInputStream
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(new JFrame(), "Couldn't open file\nEXCEPION: " + ex.getMessage() + "\n");
                    }
                    ;
                }
            }
        });
        sharedBar.add(button);
        sharedBar.addSeparator();

        url = this.getClass().getResource("/graph/can.gif");
        buttonIcon = new ImageIcon(url);
        button = new JButton("", buttonIcon);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int fInd = sharedList.getSelectedIndex();
                if (fInd >= 0) {
                    FilePacketReader fpReader = (FilePacketReader) sharedlm.getElementAt(fInd);
                    fpReader.REMOVE = true;    //send remove packet
                    fpReader.closeStream();
                    dis.removeFilePacketReader(fpReader.getHashCode());    //stop listening for cmds start/stop/pause
                    sharedlm.removeElementAt(fInd);
                }
            }
        });
        sharedBar.add(button);

        add(sharedBar, BorderLayout.NORTH);
        add(new JScrollPane(sharedList), BorderLayout.CENTER);
        add(new JLabel(" "), BorderLayout.EAST);
        add(new JLabel(" "), BorderLayout.WEST);
    }


    void removeFilePacketReaders() {
        for (int i = 0; i < sharedlm.size(); i++)
            sharedlm.removeElementAt(i);
    }

}
