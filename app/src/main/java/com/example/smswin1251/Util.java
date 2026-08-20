package com.example.smswin1251;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;
import java.io.ByteArrayOutputStream;
public class Util {
    public static byte[] encrypt(long offset, byte[] data, byte[] key) {
        byte[] result=new byte[data.length];
        for(int i=0;i<data.length;i++){
            result[i]=(byte) (data[i]^key[i+(int) offset]);
        }

        return result;
    }

    public static byte[] compress(byte[] input) {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);
        deflater.setInput(input);
        deflater.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
        byte[] buffer = new byte[1024];

        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            bos.write(buffer, 0, count);
        }
        deflater.end();
        return bos.toByteArray();
    }

    public static byte[] decompress(byte[] compressed) throws IOException {
        Inflater inflater = new Inflater(true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length * 2);
        try(InflaterOutputStream inflaterOutputStream= new InflaterOutputStream(bos,inflater)){
            inflaterOutputStream.write(compressed);
            inflaterOutputStream.finish();
            return bos.toByteArray();
        }
        finally {
            inflater.end();
        }
    }

    public static ArrayList<String> divideMessage(String convertedMessage) {

        ArrayList<String> smsList= new ArrayList<String>();
        StringBuilder currentSMS= new StringBuilder();
        currentSMS.append(Integer.toString(smsList.size()));
        int currentSMSLength=currentSMS.length();

        for(int i=0; i<convertedMessage.length();i++){
            if ((convertedMessage.charAt(i)=='^'&&currentSMSLength>=159)||currentSMSLength>=160){
                smsList.add(currentSMS.toString());
                currentSMS = new StringBuilder();
                currentSMS.append(Integer.toString(smsList.size()));
                currentSMSLength=currentSMS.length();
            }
            currentSMS.append(convertedMessage.charAt(i));
            if(convertedMessage.charAt(i)=='^')currentSMSLength+=1;
            currentSMSLength+=1;
        }
        smsList.add(currentSMS.toString());
        return smsList;
    }
}
