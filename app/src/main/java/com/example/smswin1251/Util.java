package com.example.smswin1251;

public class Util {
    public static byte[] encrypt(long offset, byte[] data, byte[] key) {
        byte[] result=new byte[data.length];
        for(int i=0;i<data.length;i++){
            result[i]=(byte) (data[i]^key[i+(int) offset]);
        }

        return result;
    }

}
