/*
 * $Id: DownloadPanel.java,v 1.2 2007/06/16 09:03:05 fermich Exp $
 */

package pl.fermich.jkomar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class DownloadPanel extends JPanel {


    private JList downList;
    private DefaultListModel downdlm;
    private CmdInputBuffer cib;        //send start/stop/pause commands


    public DownloadPanel(CmdInputBuffer cIB) {
        setLayout(new BorderLayout());

        cib = cIB;
        downdlm = new DefaultListModel();

        java.net.URL url;
        ImageIcon buttonIcon;

        downList = new JList(downdlm);
        downList.setCellRenderer(new DowCellRenderer());
        downList.setBackground(this.getBackground());

        ActionListener checkChanges = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downList.setEnabled(false);
                downList.setEnabled(true);
            }
        };

        new Timer(5000, checkChanges).start();

        JButton button;
        JToolBar downBar = new JToolBar();
        downBar.setFloatable(false);

        url = this.getClass().getResource("/graph/get.gif");
        buttonIcon = new ImageIcon(url);
        button = new JButton("", buttonIcon);
        downBar.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //GET_START hashCode
                int fInd = downList.getSelectedIndex();
                if (fInd >= 0) {
                    FilePacketWriter fpWriter = (FilePacketWriter) downdlm.getElementAt(fInd);
                    Packet packet = new Packet(Property.GET_START, fpWriter.getHashCode());
                    if (fpWriter.SAVED == false)
                        cib.addPacket(packet);
                }
            }
        });
        downBar.addSeparator();

        url = this.getClass().getResource("/graph/pause.gif");
        buttonIcon = new ImageIcon(url);
        button = new JButton("", buttonIcon);
        downBar.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //GET_PAUSE hashCode
                int fInd = downList.getSelectedIndex();
                if (fInd >= 0) {
                    FilePacketWriter fpWriter = (FilePacketWriter) downdlm.getElementAt(fInd);
                    Packet packet = new Packet(Property.GET_PAUSE, fpWriter.getHashCode());
                    if (fpWriter.SAVED == false)
                        cib.addPacket(packet);
                }
            }
        });
        downBar.addSeparator();

        url = this.getClass().getResource("/graph/stop.gif");
        buttonIcon = new ImageIcon(url);
        button = new JButton("", buttonIcon);
        downBar.add(button);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //GET_STOP hashCode
                int fInd = downList.getSelectedIndex();

                if (fInd >= 0) {
                    FilePacketWriter fpWriter = (FilePacketWriter) downdlm.getElementAt(fInd);
                    Packet packet = new Packet(Property.GET_STOP, fpWriter.getHashCode());
                    if (fpWriter.SAVED == false) {
                        fpWriter.resetStream();
                        cib.addPacket(packet);
                    }

                }
            }
        });

        add(downBar, BorderLayout.NORTH);
        add(new JScrollPane(downList), BorderLayout.CENTER);
        add(new JLabel(" "), BorderLayout.EAST);
        add(new JLabel(" "), BorderLayout.WEST);
    }


    public void addFilePacketWriter(FilePacketWriter fpw) {
        downdlm.addElement(fpw);
    }


    public void removeFilePacketWriter(int j) {
        downdlm.removeElementAt(j);
    }


    public void removeFilePacketWriters() {
        for (int i = 0; i < downdlm.size(); i++)
            downdlm.removeElementAt(i);
    }

}
