package com.example.thinkpad.demo8583;

import com.example.thinkpad.demo8583.bean.BCDFormat;

public class Utils {

    /**
     * bytes转8字节16进制，然后转16字节16进制（比如0x12，转0x31,0x32),再转bytes
     * @param data
     * @return
     */
    public static byte[] doubleBytes(byte[] data){
        String hexStr = Utils.bytes2HexStr(data);
        String hexStr2 = Utils.ans2HexStr(hexStr);
        return Utils.hexStrToBytes(hexStr2);
    }

    /**
     *
     * @param bcd
     * @param length 字节长度
     * @param format 左靠补，还是右靠补
     * @return
     */
    public static String formatBCD(String bcd, int length, BCDFormat format){
        //奇数或者不符合长度
        if (bcd.length() % 2 != 0 || bcd.length() != length * 2){
            switch (format){
                case FORMAT_BY_LEFT:
                    return bcd + addZero(length*2 - bcd.length());
                case FORMAT_BY_RIGHT:
                    return addZero(length*2 - bcd.length()) + bcd;
            }
        }
        return bcd;
    }

    /**
     * 补0操作
     * @param number
     * @return
     */
    public static String addZero(int number){
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i<number; i++){
            sb.append("0");
        }
        return sb.toString();
    }

    /**
     * bytes数组转String
     * @param byteArray
     * @return
     */
    public static String bytes2Str(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        return new String(byteArray);
    }

    /**
     * 合并所有bytes数组并返回
     * @param values
     * @return
     */
    public static byte[] mergeAllBytes(byte[]... values) {
        int length_byte = 0;
        for (int i = 0; i < values.length; i++) {
            length_byte += values[i].length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (int i = 0; i < values.length; i++) {
            byte[] b = values[i];
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    /**
     * bcd转byte数组
     * @param bcd
     * @param data
     */
    public static void bcd2Bytes(String bcd, byte[] data){
        for (int i = 0,j = 0; i<bcd.length(); i += 2,j++){
            String s = bcd.substring(i,i+2);
            int byteInt = Integer.parseInt(s,16) & 0xFF;
            data[j] = Integer.valueOf(byteInt).byteValue();
        }
    }

    /**
     * bytes转bcd
     * 比如：0x30->30
     * @param data
     * @return
     */
    public static int bytes2BcdInt(byte[] data){
        String hex = bytes2HexStr(data);
        return Integer.parseInt(hex,10);
    }

    /**
     *
     * @param data
     * @return
     */
    public static int bytes2HexInt(byte[] data){
        String hex = bytes2HexStr(data);
        return Integer.parseInt(hex,16);
    }


    /**
     * bit转16进制
     * @param s
     * @return
     */
    public static String bit2HexStr(String s){
        int i = Integer.parseInt(s,2);
        return Integer.toHexString(i).toUpperCase();
    }

    /**
     * 字符转16进制
     * @param s
     * @return
     */
    public static String ans2HexStr(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch).toUpperCase();
            str = str + s4;
        }
        return str;
    }



    /**
     * 16进制字符串转byte数组
     * @param hexString
     * @return
     */
    public static byte[] hexStrToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * bytes转16进制string显示
     * @param src
     * @return
     */
    public static String bytes2HexStr(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


}
