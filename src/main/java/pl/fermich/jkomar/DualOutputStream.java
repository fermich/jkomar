/*
 * $Id: DualOutputStream.java,v 1.2 2007/06/16 09:03:05 fermich Exp $
 */

package pl.fermich.jkomar;

import javax.swing.*;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

class DualOutputStream extends Thread {


    private ArrayList<InputDataBuffer> ccfList;
    private ObjectOutputStream oos;
    private boolean writeFlag;
    private JTextArea logs;
    private HostPanel hostPanel;


    public DualOutputStream(JTextArea loGS) {
        //array of fileinputbuffers and chatinputbuffer
        ccfList = new ArrayList<InputDataBuffer>();
        writeFlag = false;
        logs = loGS;
        oos = null;
    }


    public void setSocketOutputStream(OutputStream oSS) {
        try {
            oos = new ObjectOutputStream(oSS);
        } catch (Exception e) {
            logs.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
            StackTraceElement[] ste = e.getStackTrace();
            for (int i = 0; i < ste.length; i++)
                logs.append(ste[i] + "\n");
        }
    }


    public void setWriteFlag(boolean f) {
        writeFlag = f;
    }


    public void addPacketReader(InputDataBuffer n) {
        ccfList.add(n);
    }


    public void run() {
        Packet packet;
        while (true) {
            if (writeFlag && ccfList.size() > 0)        //if socket is open
            {
                for (int i = 0; i < ccfList.size(); i++) {
                    packet = ccfList.get(i).readPacket();
                    try {
                        if (packet != null) {
                            oos.writeUnshared(packet);
                            oos.flush();

                            if (packet.getPacketType() == Property.DEL_FILE) {
                                ccfList.remove(i);
                                i = 0;
                            } else if (packet.getPacketType() == Property.CLOSE)
                                hostPanel.closeConnection();
                        }
                    } catch (Exception e) {
                        logs.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
                        StackTraceElement[] ste = e.getStackTrace();
                        for (int j = 0; j < ste.length; j++)
                            logs.append(ste[j] + "\n");
                        writeFlag = false;
                    }
                }
                try {
                    Thread.sleep(0, 01);
                } catch (Exception e) {
                    logs.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
                    StackTraceElement[] ste = e.getStackTrace();
                    for (int i = 0; i < ste.length; i++)
                        logs.append(ste[i] + "\n");
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    logs.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
                    StackTraceElement[] ste = e.getStackTrace();
                    for (int i = 0; i < ste.length; i++)
                        logs.append(ste[i] + "\n");
                }
            }
        }
    }


    void setHostPanel(HostPanel hp) {
        hostPanel = hp;
    }


    void removeFilePacketReadersCloseSocketStream() {
        while (ccfList.size() > 2)
            ccfList.remove(2);
        try {
            if (oos != null) oos.close();
            oos = null;
        } catch (Exception e) {
            logs.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
            StackTraceElement[] ste = e.getStackTrace();
            for (int j = 0; j < ste.length; j++)
                logs.append(ste[j] + "\n");
        }
    }
}
