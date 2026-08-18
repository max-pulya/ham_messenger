package com.example.smswin1251;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
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

    public static byte[] decompress(byte[] compressed) throws Exception {
        Inflater inflater = new Inflater(true);
        inflater.setInput(compressed);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length * 2);
        byte[] buffer = new byte[1024];

        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            bos.write(buffer, 0, count);
        }
        inflater.end();

        return bos.toByteArray();
    }

}
