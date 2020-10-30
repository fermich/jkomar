/*
 * $Id: Property.java,v 1.2 2007/06/16 09:03:06 fermich Exp $
 */

package pl.fermich.jkomar;

class Property {
    public static int DATA_SIZE = 1024 * 50;
    public static int MSG_LEN = 1024;
    public static int FNAME_LEN = 256;
    public static String DL_PATH = "";

    public static final byte CHAT_MSG = 1;
    public static final byte FILE_PART = 2;
    public static final byte NEW_FILE = 3;
    public static final byte DEL_FILE = 4;
    public static final byte GET_START = 5;
    public static final byte GET_STOP = 6;
    public static final byte GET_PAUSE = 7;
    public static final byte CLOSE = 8;
}
