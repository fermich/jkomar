/*
 * $Id: ChatInputBuffer.java,v 1.2 2007/06/16 09:03:04 fermich Exp $
 */

package pl.fermich.jkomar;

import java.util.ArrayList;

class ChatInputBuffer extends InputDataBuffer {


    private ArrayList<String> msgList;


    public ChatInputBuffer() {
        super();
        msgList = new ArrayList<String>();
    }


    public void addMessage(String m) {
        msgList.add(m);
    }


    //	Property.CHAT dataSize data
    public Packet readPacket() {
        if (msgList.size() > 0) {
            byte[] msg = msgList.get(0).getBytes();
            int msgLen = msg.length;
            if (msgLen > Property.MSG_LEN)
                msgLen = Property.MSG_LEN;
            Packet packet = new Packet(Property.CHAT_MSG, 0);
            packet.setData(msg);
            packet.setDataSize(msgLen);
            msgList.remove(0);
            return packet;
        } else
            return null;
    }
}

