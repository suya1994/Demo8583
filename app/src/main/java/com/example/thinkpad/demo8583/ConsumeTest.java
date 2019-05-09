package com.example.thinkpad.demo8583;

import com.example.thinkpad.demo8583.pack.ConsumePack;

public class ConsumeTest {




    public static byte[] getPack(){
        ConsumePack pack = new ConsumePack();

        //tpdu
        pack.setTpdu("6000490000");

        //msgHead
        pack.setMsgHead("603200320501");

        //消息类型为请求(0200)
        pack.setMsgType("0200");

        //3域，交易处理码，N6 ，400000 —— 卡卡转帐
        pack.setBCDField(3,"000000");

        //4域，交易金额，N12，1.23元
        pack.setBCDField(4,"1.23");

        //11域，受卡方系统跟踪号，N6，“123456”
        pack.setBCDField(11,"000078");

        //14域，卡有效期，N4，“5月20”表示为0520
//        pack.setBCDField(14,"0520");

        //22域，服务点输入方式码，N3，比如“123”
        pack.setBCDField(22,"022");

        //25域，服务点条件码，N2
        pack.setBCDField(25,"00");

        //35域，2磁道数据，z...37，比如“12345678901234567890123”
        pack.setBCDField(35,"6221884350000976742=00002207340600000");

        //36域，3磁道数据，z...104，比如“1234567890123456789012345678901234567890”
        pack.setBCDField(36,"996221884350000976742=1561560000000000000003000000214141400001=000000000000D000000000000D000000073406000");

        //41域，受卡机终端标志码，ans8，比如“1234abcd”
        pack.setANSField(41,"12345678");

        //42域，受卡方标志码，ans15，比如 “123456789abcdef”
        pack.setANSField(42,"123456789123456");

        //49域，交易货币代码，an3，人民币是“001”
        pack.setANSField(49,"156");

        //52域，设置PIN
        pack.setPIN("123456");

        //53域，安全控制信息，N16
        pack.setBCDField(53,"0004F10260000610");


        //64域，MAC，B64，8个FE
        pack.setBinaryField(64,new byte[]{
                (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
        });


        return pack.pack();
    }
}
