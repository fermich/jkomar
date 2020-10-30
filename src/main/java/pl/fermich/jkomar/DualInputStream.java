/*
 * $Id: DualInputStream.java,v 1.2 2007/06/16 09:03:05 fermich Exp $
 */

package pl.fermich.jkomar;

import javax.swing.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

class DualInputStream extends Thread {


    private ObjectInputStream ois;        //socket input stream
    private boolean readFlag;            //read from ois
    private DownloadPanel dowPanel;        //add new file to download panel
    private SharedPanel shaPanel;        //clear when CLOSE packet read
    private ArrayList<FilePacketWriter> fpWriterList;    //copying data to file
    private ArrayList<FilePacketReader> fpReaderList;    //set start/stop/pause value to GET flag
    private ChatPanel chPanel;            //add message
    private JTextArea logsArea;            //print logs
    private HostPanel hostPanel;        //close connection stopServer/disconnect


    public DualInputStream(DownloadPanel dp, ChatPanel cp, JTextArea logs) {
        fpWriterList = new ArrayList<FilePacketWriter>();
        fpReaderList = new ArrayList<FilePacketReader>();
        readFlag = false;
        dowPanel = dp;
        chPanel = cp;
        logsArea = logs;
        ois = null;
    }


    public boolean setSocketInputStream(InputStream iss) {
        try {
            ois = new ObjectInputStream(iss);
        } catch (Exception e) {
            logsArea.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
            StackTraceElement[] ste = e.getStackTrace();
            for (int i = 0; i < ste.length; i++)
                logsArea.append(ste[i] + "\n");
            return false;
        }
        ;
        return true;
    }


    public void setReadFlag(boolean f) {
        readFlag = f;
    }


    public void run() {
        int k = 0;
        Packet packet;
        while (true) {
            if (readFlag) {
                try {
                    packet = (Packet) ois.readUnshared();
                    switch (packet.getPacketType()) {
                        case Property.FILE_PART: {
                            int hashCode = packet.getHashCode();
                            for (int i = 0; i < fpWriterList.size(); i++)
                                if (fpWriterList.get(i).getHashCode() == hashCode) {
                                    fpWriterList.get(i).savePacket(packet);
                                    break;
                                }
                            break;
                        }
                        case Property.CHAT_MSG: {
                            int dataSize = packet.getDataSize();
                            byte[] data = packet.getData();
                            chPanel.addMessage(new String(data, 0, dataSize));
                            break;
                        }
                        case Property.NEW_FILE: {
                            FilePacketWriter fpWriter = new FilePacketWriter(packet);
                            dowPanel.addFilePacketWriter(fpWriter);
                            fpWriterList.add(fpWriter);
                            logsArea.append("New file in download panel: " + new String(packet.getFName()) + "; Size " + packet.getFSize() + " bytes\n");
                            break;
                        }
                        case Property.DEL_FILE: {
                            int hashCode = packet.getHashCode();
                            for (int i = 0; i < fpWriterList.size(); i++)
                                if (fpWriterList.get(i).getHashCode() == hashCode) {
                                    fpWriterList.get(i).closeStream();
                                    dowPanel.removeFilePacketWriter(i);
                                    fpWriterList.remove(i);
                                    break;
                                }
                            break;
                        }
                        case Property.GET_START: {
                            int hashCode = packet.getHashCode();
                            for (int i = 0; i < fpReaderList.size(); i++)
                                if (fpReaderList.get(i).getHashCode() == hashCode)
                                    fpReaderList.get(i).getStart();
                            break;
                        }
                        case Property.GET_STOP: {
                            int hashCode = packet.getHashCode();
                            for (int i = 0; i < fpReaderList.size(); i++)
                                if (fpReaderList.get(i).getHashCode() == hashCode)
                                    fpReaderList.get(i).getStop();
                            break;
                        }
                        case Property.GET_PAUSE: {
                            int hashCode = packet.getHashCode();
                            for (int i = 0; i < fpReaderList.size(); i++)
                                if (fpReaderList.get(i).getHashCode() == hashCode)
                                    fpReaderList.get(i).getPause();
                            break;
                        }
                        case Property.CLOSE: {
                            hostPanel.closeConnection();
                            break;
                        }
                    }
                    Thread.sleep(0, 01);
                } catch (Exception e) {
                    logsArea.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
                    StackTraceElement[] ste = e.getStackTrace();
                    for (int i = 0; i < ste.length; i++)
                        logsArea.append(ste[i] + "\n");
                    readFlag = false;
                }
                ;
            } else {
                try {
                    Thread.sleep(100);    //check every 0.1 sec
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


    public void addFilePacketReader(FilePacketReader fib) {
        fpReaderList.add(fib);
    }


    public void removeFilePacketReader(int hc) {
        for (int i = 0; i < fpReaderList.size(); i++)
            if (fpReaderList.get(i).getHashCode() == hc)
                fpReaderList.remove(i);
    }


    public void setHostPanel(HostPanel hp) {
        hostPanel = hp;
    }


    public void removeAllReadersWritersCloseSocketStream() {
        while (fpReaderList.size() > 0) {
            fpReaderList.get(0).closeStream();
            fpReaderList.remove(0);
        }

        while (fpWriterList.size() > 0) {
            fpWriterList.get(0).closeStream();
            fpWriterList.remove(0);
        }

        shaPanel.removeFilePacketReaders();
        dowPanel.removeFilePacketWriters();
        try {
            if (ois != null) ois.close();
            ois = null;
        } catch (Exception e) {
            logsArea.append("** EXCEPTION: " + e.getMessage() + " **\nStackTrace:\n");
            StackTraceElement[] ste = e.getStackTrace();
            for (int i = 0; i < ste.length; i++)
                logsArea.append(ste[i] + "\n");
        }
        ;
    }


    public void setSharedPanel(SharedPanel sp) {
        shaPanel = sp;
    }
}
