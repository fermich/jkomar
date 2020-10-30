/*
 * $Id: FilePacketReader.java,v 1.2 2007/06/16 09:03:05 fermich Exp $
 */

package pl.fermich.jkomar;

import java.io.*;
import javax.swing.*;

class FilePacketReader extends InputDataBuffer {

    private final JProgressBar progress;
    private final JLabel label;

    BufferedInputStream fis;

    private boolean NEW;    //send new packet
    public boolean REMOVE;    //send REMOVE packet
    private boolean GET;    // download/wait

    private final long fileSize;
    private int hashCode;
    private long curSize;

    private final Packet newFilePacket;
    private final Packet remFilePacket;
    private final Packet filePart;

    private final File file;

    public FilePacketReader(File nfile) throws IOException {
        super();

        file = nfile;

        progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);

        String filePaNa = file.getPath();

        hashCode = filePaNa.hashCode();
        if (hashCode < 0) hashCode *= -1;

        try {
            fis = new BufferedInputStream(new FileInputStream(file));
        } catch (IOException e) {
            throw new IOException("Cannot open file: " + file.getName());
        }
        ;

        fileSize = file.length();

        newFilePacket = new Packet(hashCode, fileSize, file.getName());
        remFilePacket = new Packet(Property.DEL_FILE, hashCode);

        if (fileSize < 1024)
            label = new JLabel(fileSize + "  " + filePaNa);
        else if (fileSize < 1024 * 1024)
            label = new JLabel(fileSize / 1024 + "KB  " + filePaNa);
        else label = new JLabel(fileSize / (1024 * 1024) + "MB  " + filePaNa);

        curSize = 0;

        NEW = true;
        REMOVE = false;
        GET = false;
        filePart = new Packet(Property.FILE_PART, hashCode);
    }


    // read data and make packet
    public Packet readPacket() {
        int dataSize;

        try {
            if (GET) {
                dataSize = fis.read(filePart.getData(), 0, Property.DATA_SIZE);
                if (dataSize > 0) {
                    filePart.setDataSize(dataSize);
                    curSize += dataSize;
                    progress.setValue((int) (100 * ((float) curSize / (float) fileSize)));
                    return filePart;
                } else {
                    if (dataSize == -1)    //remove file when read
                    {
                        closeStream();
                        return remFilePacket;
                    }
                }
            } else if (NEW) {
                NEW = false;
                return newFilePacket;
            }

            if (REMOVE) {
                REMOVE = false;
                return remFilePacket;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        ;
        return null;
    }


    public void getStart() {
        if (GET == false)
            GET = true;
    }


    public void getPause() {
        GET = false;
    }


    public void getStop() {
        GET = false;
        streamReset();
        curSize = 0;
        progress.setValue(0);
    }


    public void streamReset() {
        try {
            fis.close();
            fis = new BufferedInputStream(new FileInputStream(file));
        } catch (Exception e) {
            System.out.println("Cannot reset stream for file: " + file.getName());
        }
        ;
    }


    //for share listcellrenderer
    public JProgressBar getProgressBar() {
        return progress;
    }


    //for share listcellrenderer
    public JLabel getFileLabel() {
        return label;
    }


    public int getHashCode() {
        return hashCode;
    }


    public void closeStream() {
        try {
            fis.close();
        } catch (Exception e) {
            System.out.println("Cannot close stream ");
        }
        ;
        GET = false;
    }
}
