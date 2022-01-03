package bgu.spl.net.api;

import java.util.LinkedList;
import java.util.Vector;

public class EncDecImpl implements MessageEncoderDecoder<String> {

    private LinkedList<Byte> byteList;

    public EncDecImpl() {
        this.byteList = new LinkedList<>();
    }

    @Override
    public String decodeNextByte(byte nextByte) {
        if (nextByte == ';') {
            byte[] byteArr = objectsToBytes(this.byteList.toArray());
            byte[] opCodeArr = {byteArr[0], byteArr[1]};
            short opCode = bytesToShort(opCodeArr);
            String ret = new String(byteArr, 2, byteArr.length - 2);
            ret = opCode + "\0" + ret;
            this.byteList = new LinkedList<>();
            return ret;
        }
        this.byteList.add(nextByte);
        return null;
    }

    public byte[] objectsToBytes(Object[] arr) { // Why the heck is it difficult to cast an Object array to a byte array
        byte[] ret = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            ret[i] = (byte)arr[i];
        }
        return ret;
    }

    @Override
    public byte[] encode(String message) {
        byte[] ret = null;
        String[] msg = message.split(" ");
        short opCode = (short)Integer.parseInt(msg[0]);
        
        if (opCode == 9) { // Notification
            byte[] opCodeByte = shortToBytes(opCode);
            byte[] msgType = shortToBytes((short)Integer.parseInt(msg[1]));
            String msgToSend = message.substring(4);
            byte[] msgBytes = msgToSend.getBytes();
            ret = new byte[opCodeByte.length + msgType.length + msgBytes.length];
            ret[0] = opCodeByte[0]; ret[1] = opCodeByte[1]; ret[2] = msgType[0]; ret[3] = msgType[1];
            for (int i = 0; i < msgBytes.length; i++) {
                ret[4 + i] = msgBytes[i];
            }
            return ret;
        }
        else if (opCode == 10) { // Acknowledge
            short messageOpCode = (short)Integer.parseInt(msg[1]);
            byte[] sh1 = shortToBytes(opCode);
            byte[] sh2 = shortToBytes(messageOpCode);
            Vector<Byte> byteVector = new Vector<>();
            byteVector.add(sh1[0]); byteVector.add(sh1[1]); byteVector.add(sh2[0]); byteVector.add(sh2[1]);
            if (messageOpCode == 4 || messageOpCode == 7 || messageOpCode == 8) { // We have optional info
                byte[] optional = msg[2].getBytes();
                for (int i = 0; i < optional.length; i++) 
                    byteVector.add(optional[i]);
            }
            ret = objectsToBytes(byteVector.toArray());
        }
        else if (opCode == 11) { // Error
            short messageOpCode = (short)Integer.parseInt(msg[1]);
            byte[] sh1 = shortToBytes(opCode);
            byte[] sh2 = shortToBytes(messageOpCode);
            ret = new byte[sh1.length + sh2.length];
            ret[0] = sh1[0]; ret[1] = sh1[1]; ret[2] = sh2[0]; ret[3] = sh2[1];
        }
        byte[] ret2 = new byte[ret.length + 1];
        for (int i = 0; i < ret.length; i++) {
            ret2[i] = ret[i];
        }
        ret2[ret2.length - 1] = ';';
        return ret2;
    }


    public short bytesToShort(byte[] byteArr)
{
    short result = (short)((byteArr[0] & 0xff) << 8);
    result += (short)(byteArr[1] & 0xff);
    return result;
}

public byte[] shortToBytes(short num)
{
    byte[] bytesArr = new byte[2];
    bytesArr[0] = (byte)((num >> 8) & 0xFF);
    bytesArr[1] = (byte)(num & 0xFF);
    return bytesArr;
}

}