/*
 * $Id: HostPanel.java,v 1.2 2007/06/16 09:03:05 fermich Exp $
 */

package pl.fermich.jkomar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

class HostPanel extends JPanel {

    private JTextField cliPortNum;        //listening server port
    private JTextField serverAddr;        //listening server address

    private JTextField servPortNum;        //starting server port

    private JButton servStart;    //start Server
    private JButton servStop;    //stop Server
    private JButton cliCon;        //connect client
    private JButton cliDis;        //disconnect client

    private JTextArea logsArea;

    private Socket sock;

    private DualOutputStream dos;
    private DualInputStream dis;

    private JLabel status;

    private CmdInputBuffer cmdIB;
    private PropertyPanel proPanel;

    private ServerThread st;


    public HostPanel(DualOutputStream dOS, DualInputStream dIS, JTextArea logs,
                     JLabel stat, CmdInputBuffer cib, PropertyPanel pp) {
        dos = dOS;
        dis = dIS;
        cmdIB = cib;
        status = stat;
        logsArea = logs;
        proPanel = pp;
        cliPortNum = new JTextField("1111", 10);
        serverAddr = new JTextField("127.0.0.1", 10);
        servPortNum = new JTextField("1111", 10);
        setLayout(new BorderLayout());
        JPanel north = new JPanel(new BorderLayout());
        JPanel northWest = new JPanel();
        northWest.add(new JLabel("Port: "));
        JPanel northEast = new JPanel();
        northEast.add(servPortNum);
        JPanel northSouth = new JPanel();
        servStart = new JButton("START");
        servStop = new JButton("STOP");
        servStop.setEnabled(false);

        servStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                st = new ServerThread();
                st.start();
                servStart.setEnabled(false);
                cliCon.setEnabled(false);
                cliDis.setEnabled(false);
                servStop.setEnabled(true);

                servPortNum.setEnabled(false);
                serverAddr.setEnabled(false);
                cliPortNum.setEnabled(false);

                proPanel.disableProperties();
            }
        });

        northSouth.add(servStart);

        servStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (st.CONNECTED)
                    cmdIB.addPacket(new Packet(Property.CLOSE, 0));
                else {
                    st.closeServerSocket();
                    st = null;
                    closeConnection();
                }
            }
        });

        northSouth.add(servStop);

        north.add(northWest, BorderLayout.WEST);
        north.add(northEast, BorderLayout.EAST);
        north.add(northSouth, BorderLayout.SOUTH);
        north.setBorder(BorderFactory.createTitledBorder("SERVER"));

        JPanel south = new JPanel(new BorderLayout());

        JPanel southWest = new JPanel(new GridLayout(2, 1));
        southWest.add(new JLabel("Server address:"));
        southWest.add(new JLabel("Port: "));

        JPanel southEast = new JPanel(new GridLayout(2, 1));
        southEast.add(serverAddr);
        southEast.add(cliPortNum);

        JPanel southSouth = new JPanel();
        cliCon = new JButton("Connect");
        cliDis = new JButton("Disconnect");
        cliDis.setEnabled(false);

        cliCon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    sock = new Socket(serverAddr.getText(), new Integer(cliPortNum.getText()));
                    servStart.setEnabled(false);
                    servStop.setEnabled(false);
                    cliCon.setEnabled(false);
                    cliDis.setEnabled(true);

                    servPortNum.setEnabled(false);
                    serverAddr.setEnabled(false);
                    cliPortNum.setEnabled(false);

                    proPanel.disableProperties();

                    dos.setSocketOutputStream(sock.getOutputStream());
                    dis.setSocketInputStream(sock.getInputStream());

                    dis.setReadFlag(true);
                    dos.setWriteFlag(true);

                    status.setText("Connected to " + sock.getInetAddress().toString().replace('/', ' ') +
                            " on port " + cliPortNum.getText());
                } catch (Exception e) {
                    logsArea.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
                    StackTraceElement[] ste = e.getStackTrace();
                    for (int i = 0; i < ste.length; i++)
                        logsArea.append(ste[i] + "\n");
                    status.setText("Couldn't connect to " + serverAddr.getText());
                }
                ;
            }
        });

        southSouth.add(cliCon);

        cliDis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cmdIB.addPacket(new Packet(Property.CLOSE, 0));
            }
        });

        southSouth.add(cliDis);

        south.add(southWest, BorderLayout.WEST);
        south.add(southEast, BorderLayout.EAST);
        south.add(southSouth, BorderLayout.SOUTH);

        south.setBorder(BorderFactory.createTitledBorder("CLIENT"));
        add(north, BorderLayout.NORTH);
        add(south, BorderLayout.SOUTH);
    }


    public void closeConnection() {
        dis.setReadFlag(false);
        dos.setWriteFlag(false);

        servStart.setEnabled(true);
        servStop.setEnabled(false);
        cliCon.setEnabled(true);
        cliDis.setEnabled(false);

        servPortNum.setEnabled(true);
        serverAddr.setEnabled(true);
        cliPortNum.setEnabled(true);

        dis.removeAllReadersWritersCloseSocketStream();
        dos.removeFilePacketReadersCloseSocketStream();

        proPanel.enableProperties();

        status.setText("Connection closed");
        try {
            if (sock != null) sock.close();
        } catch (Exception e) {
            logsArea.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
            StackTraceElement[] ste = e.getStackTrace();
            for (int i = 0; i < ste.length; i++)
                logsArea.append(ste[i] + "\n");
            status.setText("Couldn't close socket");
        }
        ;
    }


    class ServerThread extends Thread {
        ServerSocket ss;
        public boolean CONNECTED;

        public void run() {
            CONNECTED = false;
            try {
                ss = new ServerSocket(new Integer(servPortNum.getText()));
                logsArea.append("\nServer is listening on interfaces:\n");

                Enumeration e = NetworkInterface.getNetworkInterfaces();
                while (e.hasMoreElements()) {
                    NetworkInterface iface = (NetworkInterface) e.nextElement();
                    logsArea.append(iface.toString() + "\n");

                    if (iface.getName().startsWith("ppp")) {
                        status.setText("Server is listening on " + iface.getName() + ": address: ");
                        Enumeration ie = iface.getInetAddresses();
                        while (ie.hasMoreElements()) {
                            InetAddress ia = (InetAddress) ie.nextElement();
                            status.setText(status.getText() + " " + ia.toString().replace('/', ' '));
                        }
                        status.setText(status.getText() + ",  port: " + servPortNum.getText());
                    }
                }
                sock = ss.accept();
                CONNECTED = true;
                logsArea.append("\nClient " + sock.getInetAddress().toString() + " connected.\n");
                status.setText("Client " + sock.getInetAddress().toString().replace('/', ' ') + " connected\n");

                dos.setSocketOutputStream(sock.getOutputStream());
                dos.setWriteFlag(true);

                dis.setSocketInputStream(sock.getInputStream());
                dis.setReadFlag(true);
                ss.close();
            } catch (Exception e) {
                logsArea.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
                StackTraceElement[] ste = e.getStackTrace();
                for (int i = 0; i < ste.length; i++)
                    logsArea.append(ste[i] + "\n");
            }
            ;
        }


        public void closeServerSocket() {
            try {
                sock = null;
                CONNECTED = false;
                ss.close();
            } catch (Exception e) {
                logsArea.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
                StackTraceElement[] ste = e.getStackTrace();
                for (int i = 0; i < ste.length; i++)
                    logsArea.append(ste[i] + "\n");
            }
            ;
        }
    }
}
