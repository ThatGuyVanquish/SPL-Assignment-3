package bgu.spl.net.api;

import java.util.LinkedList;

public class EncDecImpl implements MessageEncoderDecoder<String> {
    
    private LinkedList<Byte> byteList;

    public EncDecImpl() {
        this.byteList = new LinkedList<Byte>();
    }

    @Override
    public String decodeNextByte(byte nextByte) {
        if (nextByte == ';') {
            byte[] byteArr = objectsToBytes(this.byteList.toArray());
            String ret = new String(byteArr);
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
        return message.getBytes();
    }
    
//     public short bytesToShort(byte[] byteArr)
// {
//     short result = (short)((byteArr[0] & 0xff) << 8);
//     result += (short)(byteArr[1] & 0xff);
//     return result;
// }

// public byte[] shortToBytes(short num)
// {
//     byte[] bytesArr = new byte[2];
//     bytesArr[0] = (byte)((num >> 8) & 0xFF);
//     bytesArr[1] = (byte)(num & 0xFF);
//     return bytesArr;
// }

}
