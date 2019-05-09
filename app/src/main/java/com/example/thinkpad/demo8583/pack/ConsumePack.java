package com.example.thinkpad.demo8583.pack;

import com.example.thinkpad.demo8583.EncryptUtil;
import com.example.thinkpad.demo8583.Utils;
import com.example.thinkpad.demo8583.bean.BCDFormat;
import com.example.thinkpad.demo8583.bean.Field;
import com.example.thinkpad.demo8583.bean.FieldType;
import com.example.thinkpad.demo8583.bean.LengthFormat;

/**
 * 针对9.2.2消费的8583包
 */
public class ConsumePack extends Pack {

    /**
     * 卡号的数据
     */
    public String cardNumber;

    public ConsumePack() {
        super();
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

        fields[2].type = FieldType.BCD;
        fields[2].lengthFormat = LengthFormat.LLVAR;
        fields[2].bcdFormat = BCDFormat.FORMAT_BY_LEFT;

        fields[3].type = FieldType.BCD;
        fields[3].length = 6;

        fields[4].type = FieldType.BCD;
        fields[4].length = 12;

        fields[11].type = FieldType.BCD;
        fields[11].length = 6;

        fields[12].type = FieldType.BCD;
        fields[12].length = 6;

        fields[13].type = FieldType.BCD;
        fields[13].length = 4;

        fields[14].type = FieldType.BCD;
        fields[14].length = 4;

        fields[15].type = FieldType.BCD;
        fields[15].length = 4;

        fields[22].type = FieldType.BCD;
        fields[22].length = 3;

        fields[23].type = FieldType.BCD;

        fields[25].type = FieldType.BCD;
        fields[25].length = 2;

        fields[26].type = FieldType.BCD;

        fields[32].type = FieldType.BCD;
        fields[32].lengthFormat = LengthFormat.LLVAR;

        fields[35].type = FieldType.BCD;
        fields[35].lengthFormat = LengthFormat.LLVAR;

        fields[36].type = FieldType.BCD;
        fields[36].lengthFormat = LengthFormat.LLLVAR;

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

        fields[49].type = FieldType.ANS;
        fields[49].length = 3;

        fields[52].type = FieldType.BINARY;
        fields[52].length = 8;

        fields[53].type = FieldType.BCD;
        fields[53].length = 16;

        fields[54].type = FieldType.ANS;
        //包含多个子域，啥意思？？？
        fields[55].type = FieldType.BINARY;
        fields[60].type = FieldType.BCD;

        fields[62].type = FieldType.ANS;
        fields[62].lengthFormat = LengthFormat.LLLVAR;

        fields[63].type = FieldType.ANS;
        fields[63].lengthFormat = LengthFormat.LLLVAR;

        fields[64].type = FieldType.BINARY;
        fields[64].length = 8;

    }


    @Override
    public void setBCDField(int index, String bcd) {

        //解析第35和36域，从里面拿出卡号；
        if ((35 == index || 36 == index)){
            if (null == cardNumber){
                getCardFromField(bcd);
            }
            //当数据为磁道数据时，把“=”替换成“D”，“=”的ASCII值为“0x3D”
            bcd = bcd.replace("=","D");
        }

        super.setBCDField(index, bcd);
    }

    /**
     * 获取卡号的数据
     * 如果有二磁道数据，取二磁道=号前的数据作为卡号，最长不超过19.
     * 如果没有二磁道数据，取三磁道的。
     * @param bcd
     * @return
     */
    private void getCardFromField(String bcd){
        if (bcd.contains("=")){
            String left = bcd.substring(0,bcd.indexOf("="));
            cardNumber = left.length() <= 19 ? left : left.substring(left.length() - 19, left.length());
            super.setBCDField(2,cardNumber);
        }
    }

    //设置PIN
    public void setPIN(String pin){
        //先组成8字节pin
        String pin8 = "06" + pin + "FFFFFFFF";
        byte[] bytesPin = new byte[8];
        Utils.bcd2Bytes(pin8,bytesPin);

        //卡号12个数字取法
        String card12 = cardNumber.length() == 12 ? cardNumber : cardNumber.substring(cardNumber.length() - 13, cardNumber.length() -1);
        byte[] bytesCard = new byte[8];
        Utils.bcd2Bytes("0000" + card12,bytesCard);


        //异或
        byte[] _result = new byte[8];
        for (int i = 0; i< 8; i++){
            _result[i] = (byte) (bytesPin[i] ^ bytesCard[i]);
        }


        //用PIN秘钥加密后设置52域；
        byte[] result = EncryptUtil.encryptByDESede(_result,Pack.key1);
        super.setBinaryField(52,result);

    }

    /**
     * 组包，消费报文组包需要mac校验
     * @return
     */
    @Override
    public byte[] pack() {
        super.setUnionpayMAC();
        return super.pack();
    }
}
