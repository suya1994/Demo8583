package com.example.thinkpad.demo8583.pack;

import com.example.thinkpad.demo8583.EncryptUtil;
import com.example.thinkpad.demo8583.Utils;
import com.example.thinkpad.demo8583.bean.BCDFormat;
import com.example.thinkpad.demo8583.bean.Field;
import com.example.thinkpad.demo8583.bean.FieldType;

import java.util.Arrays;

/**
 * 一般性8583包
 */
public class Pack {
    /**
     * 5个字节；
     */
    public Field tpdu;

    /**
     * 6个字节
     */
    public Field msgHead;

    /**
     * 消息类型
     */
    public Field msgType;

    /**
     * 位图字符串
     */
    public Field bitmap;

    /**
     * 二进制显示的位图
     */
    public String bitmapStr;

    /**
     * 数据域,默认64位
     */
    public Field[] fields;

    /**
     * 正解析到哪一位数据
     */
    public int unpackIndex;

    /**
     * 解析后的包显示
     */
    public String unpackString;

    /**
     * 三个工作秘钥，分别是PIN秘钥，MAC秘钥，磁道秘钥
     */
    public static byte[] key1;
    public static byte[] key2;
    public static byte[] key3;


    public Pack() {
        //tpdu
        tpdu = new Field();
        tpdu.length = 5;
        tpdu.bytesData = new byte[tpdu.length];

        //msgHead
        msgHead = new Field();
        msgHead.length = 6;
        msgHead.bytesData = new byte[msgHead.length];


        //消息类型，N4
        msgType = new Field();
        msgType.type = FieldType.BCD;
        msgType.length = 2;
        msgType.bytesData = new byte[msgType.length];

    }


    /**
     * 设置tpdu
     * @param tpdu
     */
    public void setTpdu(String tpdu) {
        Utils.bcd2Bytes(tpdu,this.tpdu.bytesData);
    }

    /**
     * 设置msgHead
     * @param msgHead
     */
    public void setMsgHead(String msgHead) {
        Utils.bcd2Bytes(msgHead,this.msgHead.bytesData);
    }

    /**
     * 设置msgType
     * @param msgType
     */
    public void setMsgType(String msgType) {
        Utils.bcd2Bytes(msgType,this.msgType.bytesData);
    }


    /**
     * 设置binary
     * @param index
     * @param binary
     */
    public void setBinaryField(int index,byte[] binary){
        fields[index].isExist = true;

        //算binary长度
        int lengthInt = binary.length;
        byte[] lengthBytes = getLengthBytes(lengthInt, index);

        //分类型算
        switch (fields[index].lengthFormat){
            case LLVAR:
                fields[index].bytesData = new byte[ 1 + lengthInt ];
                fields[index].bytesData = Utils.mergeAllBytes(lengthBytes,binary);
                break;
            case LLLVAR:
                fields[index].bytesData = new byte[ 2 + lengthInt];
                fields[index].bytesData = Utils.mergeAllBytes(lengthBytes,binary);
                break;
            case FIXED:
                fields[index].bytesData = binary;
                break;
        }
    }



    /**
     * 设置ANS
     * @param index
     * @param ans
     */
    public void setANSField(int index,String ans){
        fields[index].isExist = true;

        int lengthInt = ans.length();
        byte[] lengthBytes = getLengthBytes(lengthInt,index);

        String ansHexStr = Utils.ans2HexStr(ans);
        byte[] ansBytes = Utils.hexStrToBytes(ansHexStr);

        switch (fields[index].lengthFormat){
            case LLVAR:
                fields[index].bytesData = new byte[ 1 + lengthInt ];
                fields[index].bytesData = Utils.mergeAllBytes(lengthBytes,ansBytes);
                break;
            case LLLVAR:
                fields[index].bytesData = new byte[ 2 + lengthInt];
                fields[index].bytesData = Utils.mergeAllBytes(lengthBytes,ansBytes);
                break;
            case FIXED:
                fields[index].bytesData = new byte[lengthInt];
                fields[index].bytesData = ansBytes;
                break;
        }
    }

    /**
     * 设置bcd
     * @param index
     * @param bcd
     */
    public void setBCDField(int index,String bcd){
        fields[index].isExist = true;

        //当数据为金额时，去掉.
        bcd = bcd.replace(".","");

        String primaryBCD = bcd;

        //实际长度
        int lengthInt = bcd.length();
        byte[] lengthBytes = getLengthBytes(lengthInt, index);

        //数据,这里针对变长来说
        int bcdBytesLengthInt = lengthInt%2 == 0 ? lengthInt/2 : lengthInt/2 + 1;
        bcd = Utils.formatBCD(bcd, bcdBytesLengthInt, fields[index].bcdFormat);
        byte[] bcdBytes = new byte[bcdBytesLengthInt];
        Utils.bcd2Bytes(bcd, bcdBytes);

        switch (fields[index].lengthFormat){
            case LLVAR:
                fields[index].bytesData = new byte[ 1 + bcdBytesLengthInt ];
                fields[index].bytesData = Utils.mergeAllBytes(lengthBytes, bcdBytes);
                break;
            case LLLVAR:
                fields[index].bytesData = new byte[ 2 + bcdBytesLengthInt];
                fields[index].bytesData = Utils.mergeAllBytes(lengthBytes, bcdBytes);
                break;
            case FIXED:
                int requiredDataBytesLength = fields[index].length % 2 == 0 ? fields[index].length/2 : fields[index].length/2 + 1;
                bcd = Utils.formatBCD(primaryBCD, requiredDataBytesLength, BCDFormat.FORMAT_BY_RIGHT);
                fields[index].bytesData = new byte[requiredDataBytesLength];
                Utils.bcd2Bytes(bcd,fields[index].bytesData);
                break;
        }
    }


    /**
     *
     * @param length 长度int型
     * @param index 域的Index
     */
    private byte[] getLengthBytes(int length, int index){
        String lengthBCD;
        byte[] lengthBytes = null;

        //分类型算
        switch (fields[index].lengthFormat){
            case LLVAR:
                lengthBCD = Utils.formatBCD(String .valueOf(length),1,BCDFormat.FORMAT_BY_RIGHT);
                lengthBytes = new byte[1];
                Utils.bcd2Bytes(lengthBCD,lengthBytes);
                break;
            case LLLVAR:
                lengthBCD = Utils.formatBCD(String .valueOf(length),2,BCDFormat.FORMAT_BY_RIGHT);
                lengthBytes = new byte[2];
                Utils.bcd2Bytes(lengthBCD,lengthBytes);
                break;
            case FIXED:
                break;
        }

        return lengthBytes;
    }


    /**
     * 银联mac算法
     */
    protected void setUnionpayMAC(){

        byte[] data = getMacBytes();
        byte[] result = new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};

        //8字节循环异或
        for (int i = 0; i< data.length; i += 8){
            byte[] bytes = Arrays.copyOfRange(data,i,i+8);
            //异或
            for (int j = 0;j < 8; j++){
                result[j] = (byte) (result[j] ^ bytes[j]);
            }
        }

        //bytes转8字节16进制，然后转16字节16进制（比如0x12，转0x31,0x32),再转bytes
        byte[] ansResultBytes = Utils.doubleBytes(result);

        //分别取左右两边8字节赋值,然后left加密
        byte[] left = Arrays.copyOfRange(ansResultBytes,0,8);
        byte[] right = Arrays.copyOfRange(ansResultBytes,8,16);
        result = EncryptUtil.encryptByDESede(left,key2);

        //加密后和right异或，再加密
        for (int j = 0;j < 8; j++){
            result[j] = (byte) (result[j] ^ right[j]);
        }
        result = EncryptUtil.encryptByDESede(result,key2);

        //取前四字节，再转ascii，再转16进制
        byte[] mac = Arrays.copyOf(result,4);
        setBinaryField(64, Utils.doubleBytes(mac));
    }

    /**
     * 获得mac初始的结果：msgType + bitmap + 2-63域
     * @return
     */
    private byte[] getMacBytes(){
        //生成位图
        buildBitmap();

        //msgType + bitmap + 2-63域
        //若三个字段的Bytes合起来不是8字节的倍数，后面添0x00
        byte[] b1 = Utils.mergeAllBytes(msgType.bytesData,bitmap.bytesData);
        byte[] b2 = packField(63);
        byte[] b3 = null;

        //若字节不是8的倍数，b3添0x00
        int length = b1.length + b2.length;
        if (length % 8 != 0){
            int remainder = 8 - length % 8;
            b3 = new byte[remainder];
            for (int i = 0 ;i<remainder; i++){
                b3[i] = 0x00;
            }
        }

        //合起来的bytes
        byte[] b;
        if (null == b3){
            b = Utils.mergeAllBytes(b1,b2);
        }else {
            b = Utils.mergeAllBytes(b1,b2,b3);
        }

        return b;
    }


    /**
     * 打包数据域
     * @param finalIndex 这里是指到位图的多少位
     *                   比如消费报文，若是算MAC，则传63进去
     *                   再比如其他，传bitmap.length()进去
     * @return 返回打包好的数据域的bytes
     */
    private byte[] packField(int finalIndex){
        int length = 0;
        for (int i = 1 ; i< finalIndex ; i++){
            if ("1".equals(bitmapStr.substring(i,i+1))){
                //fields中的[2]对应Bbitmap中的[1];
                length += fields[i+1].bytesData.length;
            }
        }

        byte[] bytes = new byte[length];
        int desIndex = 0;
        for (int i = 1 ; i< finalIndex ; i++){
            if ("1".equals(bitmapStr.substring(i,i+1))){
                //fields中的[2]对应Bbitmap中的[1];
                System.arraycopy(fields[i+1].bytesData,0,bytes,desIndex,fields[i+1].bytesData.length);
                desIndex += fields[i+1].bytesData.length;
            }
        }
        return bytes;
    }

    /**
     * 生成位图
     */
    private void buildBitmap(){
        StringBuilder sbBit = new StringBuilder();
        //只有64个域，所以位图第一位是0；
        sbBit.append("0");
        for (int i = 2; i<fields.length ; i++){
            if (fields[i].isExist){
                sbBit.append("1");
            }else {
                sbBit.append("0");
            }
        }
        bitmapStr = sbBit.toString();

        StringBuilder sbBitmap = new StringBuilder();
        for (int i = 0 ;i < sbBit.length(); i += 4){
            String s = sbBit.substring(i,i+4);
            String hex = Utils.bit2HexStr(s);
            sbBitmap.append(hex);
        }
        Utils.bcd2Bytes(sbBitmap.toString(),bitmap.bytesData);
    }


    /**
     * 组包
     * @return
     */
    public byte[] pack(){
        buildBitmap();
        byte[] b1 = Utils.mergeAllBytes(tpdu.bytesData,msgHead.bytesData,msgType.bytesData,bitmap.bytesData);
        byte[] b2 = packField(bitmapStr.length());
        byte[] b = Utils.mergeAllBytes(b1,b2);
        return  addPackLength(b);
    }


    /**
     * 添加包的总长度；
     * @param packBytes
     * @return
     */
    private byte[] addPackLength(byte[] packBytes){
        int length = packBytes.length;
        String hexLength = Integer.toHexString(length);
        if (hexLength.length() < 2){
            hexLength = "000" + hexLength;
        }else if (hexLength.length() < 3){
            hexLength = "00" + hexLength;
        }else if (hexLength.length() <4){
            hexLength = "0" + hexLength;
        }
        byte[] bytesLength = Utils.hexStrToBytes(hexLength);
        return Utils.mergeAllBytes(bytesLength,packBytes);
    }


    /**
     * 解包
     * @param packBytes
     */
    public void unpack(byte[] packBytes){
        unpackIndex = 0;

        //tpdu
        tpdu.bytesData = Arrays.copyOfRange(packBytes, unpackIndex, unpackIndex + tpdu.length);
        unpackIndex += tpdu.length;

        //msgHead
        msgHead.bytesData = Arrays.copyOfRange(packBytes, unpackIndex, unpackIndex + msgHead.length);
        unpackIndex += msgHead.length;

        //消息类型
        msgType.bytesData = Arrays.copyOfRange(packBytes, unpackIndex, unpackIndex + msgType.length);
        unpackIndex += msgType.length;

        //位图
        bitmap.bytesData = Arrays.copyOfRange(packBytes, unpackIndex, unpackIndex + bitmap.length);
        unpackIndex += bitmap.length;

        //位图二进制字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < bitmap.bytesData.length; i++){
            sb.append(Integer.toBinaryString((bitmap.bytesData[i] & 0xFF) + 0x100).substring(1));
        }
        bitmapStr = sb.toString();

        //各数据域
        unpackFiled(packBytes);


        //打印消息
        StringBuilder unpack = new StringBuilder();
        unpack.append("tpdu：" + Utils.bytes2HexStr(tpdu.bytesData) + "\n");
        unpack.append("msgHead：" + Utils.bytes2HexStr(msgHead.bytesData) + "\n");
        unpack.append("msgType：" + Utils.bytes2HexStr(msgType.bytesData) + "\n");
        unpack.append("bitmap：" + Utils.bytes2HexStr(bitmap.bytesData) + "\n");
        unpack.append("bitmap2进制字符串表示：" + bitmapStr + "\n");

        for (int i = 1; i< bitmapStr.length(); i++){
            if ("1".equals(bitmapStr.substring(i,i+1))){
                unpack.append("第 " + ( i+1 ) + " 域 ：" + fieldData2String(fields[i+1]) + "\n");
            }
        }
        unpackString = unpack.toString();
    }


    /**
     * 解数据域
     * @param packBytes
     */
    private void unpackFiled(byte[] packBytes){
        //解析各数据域
        for (int i = 1; i< bitmapStr.length(); i++){
            String bit = bitmapStr.substring(i,i+1);
            if ("1".equals(bit)){
                Field field = fields[i+1];

                byte[] lengthBytes;
                int lengthInt;
                //filed[].bytesData的长度
                int bytesDataLengthInt = 0;

                switch (field.lengthFormat){
                    case LLVAR:
                        lengthBytes = Arrays.copyOfRange(packBytes, unpackIndex, unpackIndex + 1);
                        lengthInt = Utils.bytes2BcdInt(lengthBytes);
                        field.length = lengthInt;

                        bytesDataLengthInt = getBytesDataLengthInt(field, lengthInt);
                        break;

                    case LLLVAR:
                        lengthBytes = Arrays.copyOfRange(packBytes, unpackIndex, unpackIndex + 2);
                        lengthInt = Utils.bytes2BcdInt(lengthBytes);
                        field.length = lengthInt;

                        bytesDataLengthInt = getBytesDataLengthInt(field, lengthInt);
                        break;

                    case FIXED:
                        int length = field.length;
                        bytesDataLengthInt = getBytesDataLengthInt(field,length);
                        break;
                }

                field.bytesData = new byte[bytesDataLengthInt];
                field.bytesData = Arrays.copyOfRange(packBytes, unpackIndex, unpackIndex + bytesDataLengthInt);

                unpackIndex += bytesDataLengthInt;
            }
        }
    }

    /**
     * 获取field数据域bytesData的长度的int值；
     * @param field
     * @param lengthInt
     * @return
     */
    private int getBytesDataLengthInt(Field field, int lengthInt){
        int bytesDataLengthInt = 0;
        int varLengthInt = 0;

        switch (field.lengthFormat){
            case LLVAR:
                varLengthInt = 1;
                break;
            case LLLVAR:
                varLengthInt = 2;
                break;
            case FIXED:
                varLengthInt = 0;
                break;
        }


        switch (field.type){
            case ANS:
                bytesDataLengthInt = varLengthInt + lengthInt;
                break;
            case BCD:
                bytesDataLengthInt = lengthInt%2 == 0 ? varLengthInt + lengthInt/2 : varLengthInt + lengthInt/2 + 1;
                break;
            case BINARY:
                bytesDataLengthInt = varLengthInt + lengthInt;
                break;
        }
        return bytesDataLengthInt;
    }

    /**
     * bytes数据转成16进制显示，赋值在unpackString;
     * @param field
     * @return
     */
    private String fieldData2String(Field field){
        int bytesDataLength = field.bytesData.length;
        byte[] realData = null;

        switch (field.lengthFormat){
            case LLVAR:
                realData = Arrays.copyOfRange(field.bytesData,1,bytesDataLength);
                break;
            case LLLVAR:
                realData = Arrays.copyOfRange(field.bytesData,2,bytesDataLength);
                break;
            case FIXED:
                realData = field.bytesData;
                break;
        }

        String realDataHex = Utils.bytes2HexStr(realData);
        int hexLength = realDataHex.length();
        switch (field.type){
            case ANS:
                return Utils.bytes2Str(realData);
            case BCD:
                if (hexLength > field.length){
                    switch (field.bcdFormat){
                        case FORMAT_BY_RIGHT:
                            return realDataHex.substring(hexLength - field.length,hexLength);
                        case FORMAT_BY_LEFT:
                            return realDataHex.substring(0, field.length);
                    }
                }
                return realDataHex;
            case BINARY:
                return realDataHex;
        }
        return null;
    }

    /**
     * 返回39域
     * @return
     */
    public boolean get39FieldStatus(){
        String s = Utils.bytes2Str(fields[39].bytesData);
        return "00".equals(s);
    }


}
