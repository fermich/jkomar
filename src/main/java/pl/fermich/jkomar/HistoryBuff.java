/*
 * $Id: HistoryBuff.java,v 1.2 2007/06/16 09:03:05 fermich Exp $
 */

package pl.fermich.jkomar;

import java.io.*;

class HistoryBuff {

    private String[] buffer;
    private int curHist;
    private int walkInd;
    private int buffSize;


    public HistoryBuff(int bfsize) {
        buffer = new String[bfsize];
        curHist = 0;
        walkInd = curHist;
        buffSize = bfsize;
        for (int i = 0; i < bfsize; i++)
            buffer[i] = null;
    }


    public void toHistory(String cmdLine) {
        buffer[curHist] = cmdLine;
        curHist++;
        curHist %= buffSize;
        walkInd = curHist;
    }


    public String getHistory() {
        if (buffer[walkInd] != null)
            return buffer[walkInd];
        else {
            walkInd = curHist;
            return new String("");
        }
    }


    public void prevQuery() {
        walkInd--;
        if (walkInd < 0)
            walkInd += buffSize;
    }


    public void forwQuery() {
        walkInd++;
        walkInd %= buffSize;
    }


    public boolean saveHistory(File outFile) throws Exception {
        FileWriter fwriter;
        try {
            fwriter = new FileWriter(outFile);

            for (int i = 0; i < buffSize; i++)
                fwriter.write(buffer[i], 0, buffer[i].length());
        } catch (IOException e) {
            return false;
        }
        fwriter.close();
        return true;
    }


    public boolean readHistory(File inFile) throws Exception {
        int i = 0;
        BufferedReader bfreader;
        try {
            bfreader = new BufferedReader(new FileReader(inFile));
        } catch (IOException e) {
            return false;
        }

        try {
            while (i < buffSize) {
                buffer[i] = bfreader.readLine();
                i++;
            }
        } catch (EOFException e) {
        }
        ;

        curHist = i;
        bfreader.close();
        return true;
    }


    public void reset() {
        int i;
        for (i = 0; i < buffSize; i++)
            buffer[i] = null;
        curHist = 0;
    }
}
