/*
 * $Id: Packet.java,v 1.2 2007/06/16 09:03:06 fermich Exp $
 */

package pl.fermich.jkomar;

import java.io.Serializable;

class Packet implements Serializable {

    private byte cmdType;
    private int hashCode;

    private byte[] fileName;
    private int fnSize;

    private byte[] data;
    private int dataSize;

    private long fileSize;


    public Packet() {
        cmdType = 0;
        hashCode = 0;
        fileSize = 0;

        fileName = null;
        fnSize = 0;

        data = null;
        dataSize = 0;
    }


    public Packet(int hc, long fs, String fname) {
        cmdType = Property.NEW_FILE;
        hashCode = hc;
        fileSize = fs;
        fileName = fname.getBytes();
        fnSize = fname.length();
    }


    public Packet(byte ct, int hc) {
        cmdType = ct;
        hashCode = hc;
        dataSize = 0;
        if (cmdType == Property.FILE_PART)
            data = new byte[Property.DATA_SIZE];
    }


    public void setData(byte[] d) {
        data = d;
    }


    public void setDataSize(int ds) {
        dataSize = ds;
    }


    public int getDataSize() {
        return dataSize;
    }


    public byte[] getData() {
        return data;
    }


    public long getFSize() {
        return fileSize;
    }


    public byte[] getFName() {
        return fileName;
    }


    public byte getPacketType() {
        return cmdType;
    }


    public int getHashCode() {
        return hashCode;
    }


    private void writeObject(java.io.ObjectOutputStream out) {
        try {
            out.writeByte(cmdType);
            switch (cmdType) {

//FILE_PART hashcode packetSize data
                case Property.FILE_PART: {
                    out.writeInt(hashCode);
                    out.writeInt(dataSize);
                    out.write(data, 0, dataSize);
                    out.flush();
                    break;
                }

//CHAT_MSG msgLen msg 
                case Property.CHAT_MSG: {
                    if (dataSize > Property.MSG_LEN)
                        dataSize = Property.MSG_LEN;
                    out.writeInt(dataSize);
                    out.write(data, 0, dataSize);
                    break;
                }

//NEW_FILE hashcode filesize fnSize filename
                case Property.NEW_FILE: {
                    out.writeInt(hashCode);
                    out.writeLong(fileSize);
                    if (fnSize > Property.FNAME_LEN)
                        fnSize = Property.FNAME_LEN;
                    out.writeInt(fnSize);
                    out.write(fileName, 0, fnSize);
                    break;
                }

//DELF hashcode
                case Property.DEL_FILE:
                    out.writeInt(hashCode);
                    break;

//GET_START hashCode
                case Property.GET_START:
                    out.writeInt(hashCode);
                    break;

//GET_STOP hashCode
                case Property.GET_STOP:
                    out.writeInt(hashCode);
                    break;

//GET_PAUSE hashCode
                case Property.GET_PAUSE:
                    out.writeInt(hashCode);
                    break;

//CLOSE_QUESTION
                case Property.CLOSE:
                    break;

            }
        } catch (Exception e) {
        }
        ;
    }


    private void readObject(java.io.ObjectInputStream in) {
        try {
            cmdType = in.readByte();
            switch (cmdType) {

                //FILE_PART hashcode packetSize data
                case Property.FILE_PART: {
                    hashCode = in.readInt();
                    dataSize = in.readInt();
                    data = new byte[dataSize];
                    in.readFully(data, 0, dataSize);
                    break;
                }

                //CHAT_MSG msgLen msg
                case Property.CHAT_MSG: {
                    dataSize = in.readInt();
                    data = new byte[dataSize];
                    in.read(data, 0, dataSize);
                    break;
                }

                //NEW_FILE hashcode filesize fnSize filename
                case Property.NEW_FILE: {
                    hashCode = in.readInt();
                    fileSize = in.readLong();
                    fnSize = in.readInt();
                    fileName = new byte[fnSize];
                    in.read(fileName, 0, fnSize);
                    break;
                }

                //DELF hashcode
                case Property.DEL_FILE:
                    hashCode = in.readInt();
                    break;

                //GET_START hashCode
                case Property.GET_START:
                    hashCode = in.readInt();
                    break;

                //GET_STOP hashCode
                case Property.GET_STOP:
                    hashCode = in.readInt();
                    break;

                //GET_PAUSE hashCode
                case Property.GET_PAUSE:
                    hashCode = in.readInt();
                    break;

                //CLOSE_QUESTION
                case Property.CLOSE:
                    break;

            }
        } catch (Exception e) {
        }
        ;
    }

}
