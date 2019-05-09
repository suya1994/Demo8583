package com.example.thinkpad.demo8583.pack;

import com.example.thinkpad.demo8583.EncryptUtil;
import com.example.thinkpad.demo8583.Utils;
import com.example.thinkpad.demo8583.bean.BCDFormat;
import com.example.thinkpad.demo8583.bean.Field;
import com.example.thinkpad.demo8583.bean.FieldType;
import com.example.thinkpad.demo8583.bean.LengthFormat;

import java.util.Arrays;

public class SignPack extends Pack {

    public SignPack() {
        bitmap = new Field();
        bitmap.type = FieldType.BINARY;
        bitmap.length = 8;
        bitmap.bytesData = new byte[bitmap.length];
        initField();
    }

    /**
     * 初始化数据域
     */
    private void initField(){
        //初始化
        fields = new Field[65];
        for (int i = 0;i <65; i++){
            fields[i] = new Field();
        }

        fields[11].type = FieldType.BCD;
        fields[11].length = 6;

        fields[12].type = FieldType.BCD;
        fields[12].length = 6;

        fields[13].type = FieldType.BCD;
        fields[13].length = 4;

        fields[15].type = FieldType.BCD;
        fields[15].length = 4;

        fields[32].type = FieldType.BCD;
        fields[32].lengthFormat = LengthFormat.LLVAR;

        fields[37].type = FieldType.ANS;
        fields[37].length = 12;

        fields[38].type = FieldType.ANS;
        fields[38].length = 6;

        fields[39].type = FieldType.ANS;
        fields[39].length = 2;

        fields[41].type = FieldType.ANS;
        fields[41].length = 8;

        fields[42].type = FieldType.ANS;
        fields[42].length = 15;

        fields[44].type = FieldType.ANS;
        fields[44].lengthFormat = LengthFormat.LLVAR;

        //自定义域
        fields[60].type = FieldType.BCD;
        fields[60].lengthFormat = LengthFormat.LLLVAR;
        fields[60].bcdFormat = BCDFormat.FORMAT_BY_LEFT;

        fields[62].type = FieldType.BINARY;
        fields[62].lengthFormat = LengthFormat.LLLVAR;

        fields[63].type = FieldType.ANS;
        fields[63].lengthFormat = LengthFormat.LLLVAR;

        fields[64].type = FieldType.BINARY;
        fields[64].length = 8;

    }


    /**
     * 解析签到报文第62域
     */
    public void unpack62Field(){

        System.out.println("\n-----start  unpack62Field------");

        //将62域的数据提出来，分组
        int contentLength = fields[62].bytesData.length - 2;
        byte[] content = new byte[contentLength];
        System.arraycopy(fields[62].bytesData,2,content,0,contentLength);

        //工作秘钥密文
        byte[] content1 = Arrays.copyOfRange(content,0,16);
        byte[] content2 = Arrays.copyOfRange(content,20,36);
        byte[] content3 = Arrays.copyOfRange(content,40,56);
        //校验码
        byte[] check1 = Arrays.copyOfRange(content,16,20);
        byte[] check2 = Arrays.copyOfRange(content,36,40);
        byte[] check3 = Arrays.copyOfRange(content,56,60);

        System.out.println("check1 = " + Utils.bytes2HexStr(check1));
        System.out.println("check2 = " + Utils.bytes2HexStr(check2));
        System.out.println("check3 = " + Utils.bytes2HexStr(check3));

        //用3des解密第62域，主秘钥为16个0x11，解密出三个工作秘钥
        byte[] key = new byte[]{
                0x31,0x31,0x31,0x31,0x31,0x31,0x31,0x31,
                0x31,0x31,0x31,0x31,0x31,0x31,0x31,0x31
        };

        //解密后的工作秘钥
        byte[] decrypt1 = EncryptUtil.decryptByDESede(content1,key);
        byte[] decrypt2 = EncryptUtil.decryptByDESede(content2,key);
        byte[] decrypt3 = EncryptUtil.decryptByDESede(content3,key);
        System.out.println("decrypt1 = " + Utils.bytes2HexStr(decrypt1));
        System.out.println("decrypt2 = " + Utils.bytes2HexStr(decrypt2));
        System.out.println("decrypt3 = " + Utils.bytes2HexStr(decrypt3));


        //用工作秘钥给8字节0x00加密，若结果的前4字节和校验码相同，则说明工作秘钥对
        byte[] encrypt = new byte[]{
                0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00
        };
        byte[] decrypt1by3des = EncryptUtil.encryptByDESede(encrypt,decrypt1);
        byte[] decrypt2by3des = EncryptUtil.encryptByDESede(encrypt,decrypt2);
        byte[] decrypt3by3des = EncryptUtil.encryptByDESede(encrypt,decrypt3);
        System.out.println("decrypt1by3des = " + Utils.bytes2HexStr(decrypt1by3des));
        System.out.println("decrypt2by3des = " + Utils.bytes2HexStr(decrypt2by3des));
        System.out.println("decrypt3by3des = " + Utils.bytes2HexStr(decrypt3by3des));

        //前四字节，与校验码对比
        byte[] _decrypt1by3des = Arrays.copyOf(decrypt1by3des,4);
        byte[] _decrypt2by3des = Arrays.copyOf(decrypt2by3des,4);
        byte[] _decrypt3by3des = Arrays.copyOf(decrypt3by3des,4);
        //若相同，则保存三个工作秘钥
        if (Arrays.equals(_decrypt1by3des,check1) && Arrays.equals(_decrypt2by3des,check2) && Arrays.equals(_decrypt3by3des,check3)){
            Pack.key1 = decrypt1;
            Pack.key2 = decrypt2;
            Pack.key3 = decrypt3;
        }


        System.out.println("-----end  unpack62Field------\n");
    }

}

