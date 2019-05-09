package com.example.thinkpad.demo8583;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

public class EncryptUtil {


    public static void main(String [] args){

        String hexContent = "0123456789ABCDEF";
        String hexKey = "133457799BBCDFF1";
        String hex3DesKey = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";


        System.out.println("---des加密---");
        byte[] encrypt = encryptByDes(Utils.hexStrToBytes(hexContent),Utils.hexStrToBytes(hexKey));
        System.out.println("encryptByDes : " + Utils.bytes2HexStr(encrypt));


        System.out.println("---des解密---");
        byte[] decrypt = decryptByDes(encrypt,Utils.hexStrToBytes(hexKey));
        System.out.println("decryptByDes : " + Utils.bytes2HexStr(decrypt));

        System.out.println("---3des加密---");
        byte[] encrypt3 = encryptByDESede(Utils.hexStrToBytes(hexContent),Utils.hexStrToBytes(hex3DesKey));
        System.out.println("encryptByDes : " + Utils.bytes2HexStr(encrypt3));


        System.out.println("---3des解密---");
        byte[] decrypt3 = decryptByDESede(encrypt3,Utils.hexStrToBytes(hex3DesKey));
        System.out.println("decryptByDes : " + Utils.bytes2HexStr(decrypt3));

    }





    /**
     * des解密
     * @param content
     * @param key
     * @return
     */
    public static byte[] decryptByDes(byte[] content, byte[] key){
        try {
            DESKeySpec desKey = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            return cipher.doFinal(content);

        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeySpecException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * des加密
     * @param content
     * @param key
     * @return
     */
    public static byte[] encryptByDes(byte[] content, byte[] key) {
        try {
            DESKeySpec desKey = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(content);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 3des加密
     * @param content
     * @param key
     * @return
     */
    public static byte[] encryptByDESede(byte[] content, byte[] key) {

        byte[] realKey;
        if (key.length == 16){
            byte[] key1 = new byte[8];
            System.arraycopy(key,0,key1,0,8);
            realKey = Utils.mergeAllBytes(key,key1);
        }else {
            realKey = key;
        }


        try {
            DESedeKeySpec desKey = new DESedeKeySpec(realKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey secretKey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(content);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 3des解密
     * @param content
     * @param key
     * @return
     */
    public static byte[] decryptByDESede(byte[] content, byte[] key){
        byte[] realKey;
        if (key.length == 16){
            byte[] key1 = new byte[8];
            System.arraycopy(key,0,key1,0,8);
            realKey = Utils.mergeAllBytes(key,key1);
        }else {
            realKey = key;
        }


        try {
            DESedeKeySpec desKey = new DESedeKeySpec(realKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
            SecretKey secretKey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            return cipher.doFinal(content);

        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeySpecException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

}
