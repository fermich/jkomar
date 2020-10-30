/*
 * $Id: CmdInputBuffer.java,v 1.2 2007/06/16 09:03:05 fermich Exp $
 */

package pl.fermich.jkomar;

import java.util.ArrayList;

class CmdInputBuffer extends InputDataBuffer {


    private ArrayList<Packet> cmdList;
    private Packet chPacket;


    public CmdInputBuffer() {
        super();
        cmdList = new ArrayList<Packet>();
    }


    public void addPacket(Packet cmd) {
        cmdList.add(cmd);
    }


    public Packet readPacket() {
        if (cmdList.size() > 0) {
            Packet packet = cmdList.get(0);
            cmdList.remove(0);
            return packet;
        } else
            return null;
    }
}
