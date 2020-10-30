/*
 * $Id: JKomar.java,v 1.2 2007/06/16 09:03:06 fermich Exp $
 */

package pl.fermich.jkomar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class JKomar extends JFrame {

    private JList sharedList;
    private JList downList;
    private JTextArea logs;

    //tabbed icons
    private java.net.URL url;
    private ImageIcon buttonIcon;

    //packets gates:
    private DualOutputStream dos;
    private DualInputStream dis;

    //connection status
    private JLabel status;

    private CmdInputBuffer cmib;

    public JKomar() {
        super("JKomar 1.0");
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        status = new JLabel(" ");

        //logArea:
        logs = new JTextArea("", 10, 51);
        logs.setBackground(getBackground());
        logs.setEditable(false);

        //packets output stream:
        dos = new DualOutputStream(logs);
        dos.start();

        //start/stop/pause commands buffer
        cmib = new CmdInputBuffer();

        //message buffer
        ChatInputBuffer chib = new ChatInputBuffer();

        //add chat buffer to outputsocketstream
        dos.addPacketReader(cmib);
        //add ccommands buffer to outputsocketstream
        dos.addPacketReader(chib);

        //Panels:
        DownloadPanel dp = new DownloadPanel(cmib);
        ChatPanel cp = new ChatPanel(chib);

        //packets input stream
        dis = new DualInputStream(dp, cp, logs);//manage files in downloadPanel, send chat message using cp
        dis.start();

        PropertyPanel pp = new PropertyPanel();

        //lock/unlock streams when socket closed/opened
        HostPanel hp = new HostPanel(dos, dis, logs, status, cmib, pp);
        //sharePanel:
        //register new files in dualoutputstream - reading from files-> writing to socket,
        //register in dualInputstream- listening for start/stop/pause commands -> reading from socket
        SharedPanel sp = new SharedPanel(dos, dis);

        dis.setSharedPanel(sp);
        dis.setHostPanel(hp);
        dos.setHostPanel(hp);

        //close connection when program is closing
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                cmib.addPacket(new Packet(Property.CLOSE, 0));
            }
        });

        url = this.getClass().getResource("/graph/host.gif");
        buttonIcon = new ImageIcon(url);

        //host panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("HOST", buttonIcon, hp);

        //properties panel
        url = this.getClass().getResource("/graph/prop.gif");
        buttonIcon = new ImageIcon(url);
        tabbedPane.addTab("PROPERTIES", buttonIcon, pp);

        //download panel
        url = this.getClass().getResource("/graph/down.gif");
        buttonIcon = new ImageIcon(url);
        tabbedPane.addTab("DOWNLOAD", buttonIcon, dp);

        //shared panel
        url = this.getClass().getResource("/graph/share.gif");
        buttonIcon = new ImageIcon(url);
        tabbedPane.addTab("SHARED", buttonIcon, sp);

        //chat panel
        url = this.getClass().getResource("/graph/chat.gif");
        buttonIcon = new ImageIcon(url);
        tabbedPane.addTab("CHAT", buttonIcon, cp);

        //info panel
        url = this.getClass().getResource("/graph/komar.gif");
        buttonIcon = new ImageIcon(url);

        JLabel komarver = new JLabel(" ", buttonIcon, JLabel.CENTER);

        JPanel logsPanel = new JPanel();
        logsPanel.setLayout(new BorderLayout());
        logs.append("\t\tJKomar 1.0\n\tWRITTEN BY: \n\t\tMichal Ferlinski\n\n");

        JScrollPane logssp = new JScrollPane(logs);
        logssp.setBorder(BorderFactory.createTitledBorder("LOGS"));
        logsPanel.add(komarver, BorderLayout.NORTH);
        logsPanel.add(logssp, BorderLayout.CENTER);

        url = this.getClass().getResource("/graph/logs.gif");
        buttonIcon = new ImageIcon(url);
        tabbedPane.addTab("INFO", buttonIcon, logsPanel);

        getContentPane().add(new JLabel(" "), BorderLayout.NORTH);
        getContentPane().add(new JLabel(" "), BorderLayout.EAST);
        getContentPane().add(new JLabel(" "), BorderLayout.WEST);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(status, BorderLayout.SOUTH);
        pack();
    }


    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JKomar komar = new JKomar();
                komar.setLocationRelativeTo(null);
                komar.setVisible(true);
            }
        });
    }
}
