/*
 * $Id: FilePacketWriter.java,v 1.2 2007/06/16 09:03:05 fermich Exp $
 */

package pl.fermich.jkomar;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

class FilePacketWriter {

    private final File file;
	private BufferedOutputStream fos;
	private final long fileSize;
	private long curSize;
	private final String fileName;
	private final int hashCode;
	boolean SAVED;

	private final JProgressBar progress;
	private final JLabel label;

	private int dataSize;

    public FilePacketWriter(Packet nfPacket) throws IOException {
        hashCode = nfPacket.getHashCode();
        fileName = new String(nfPacket.getFName());

        try {
            file = new File(Property.DL_PATH + fileName);
            fos = new BufferedOutputStream(new FileOutputStream(file));
        } catch (IOException e) {
            throw new IOException("Cannot Open File: " + fileName);
        }
        ;

        fileSize = nfPacket.getFSize();

        curSize = 0;

        if (fileSize < 1024)
            label = new JLabel(fileSize + "  " + fileName);
        else if (fileSize < 1024 * 1024)
            label = new JLabel(fileSize / 1024 + "KB  " + fileName);
        else
            label = new JLabel(fileSize / (1024 * 1024) + "MB  " + fileName);

        progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);

        SAVED = false;
    }


    public int getHashCode() {
        return hashCode;
    }

    //for share listcellrenderer
    public JProgressBar getProgressBar() {
        return progress;
    }

    //for share listcellrenderer
    public JLabel getFileLabel() {
        return label;
    }

    void savePacket(Packet fpart) {
        try {
            dataSize = fpart.getDataSize();
            fos.write(fpart.getData(), 0, dataSize);
            fos.flush();
            curSize += dataSize;
            progress.setValue((int) (100 * ((float) curSize / (float) fileSize)));
        } catch (Exception e) {
            System.out.println("Cannot write to file: " + fileName + e.getMessage());
        }
        ;

        try {
            if (curSize == fileSize) {
                SAVED = true;
                fos.close();
            }
        } catch (Exception e) {
            System.out.println("Cannot close file: " + fileName);
        }
        ;
    }

    void closeStream() {
        try {
            fos.close();
        } catch (Exception e) {
            System.out.println("Cannot close file: " + fileName);
        }
        ;
    }

    void resetStream() {
        try {
            fos.close();
            curSize = 0;
            fos = new BufferedOutputStream(new FileOutputStream(file));
            progress.setValue(0);
        } catch (Exception e) {
            System.out.println("Cannot close file: " + fileName);
        }
        ;
    }
}
