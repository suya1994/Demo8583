package com.example.thinkpad.demo8583;

import com.example.thinkpad.demo8583.pack.SignPack;

public class SignTest {

    /**
     * 返回一个打包好的pack;
     * @return
     */
    public static byte[] getPack(){
        SignPack pack = new SignPack();

        //tpdu
        pack.setTpdu("6000490000");

        //msgHead
        pack.setMsgHead("603200320501");

        //消息类型为请求(0800)
        pack.setMsgType("0800");


        //11域，受卡方系统跟踪号，N6，“123456”
        pack.setBCDField(11,"000074");

        //41域，受卡机终端标志码，ans8，比如“1234abcd”
        pack.setANSField(41,"12345678");

        //42域，受卡方标志码，ans15，比如 “123456789abcdef”
        pack.setANSField(42,"123456789123456");

        //60域，自定义域1 60.1 = n2,60.2 = n6,60.3 = n3
        pack.setBCDField(60,"00000000003");

        byte[] b = Utils.hexStrToBytes("53657175656E6365204E6F3234393439313030303030313034313831324341383539323931");
        pack.setBinaryField(62,b);

        //63域，自定义域
        pack.setANSField(63,"123");

        return pack.pack();

    }
}
