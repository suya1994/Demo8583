package com.example.thinkpad.demo8583;

import com.example.thinkpad.demo8583.pack.ConsumePack;
import com.example.thinkpad.demo8583.pack.SignPack;

import java.io.IOException;

public class Test {

    public static void main(String[] args){
        try {
            Client client = new Client("10.0.0.23", 8001);
            client.run();

            byte[] sign = client.sendPack(SignTest.getPack());
            SignPack signPack = new SignPack();
            //解包
            signPack.unpack(sign);
            System.out.println("解析结果：");
            System.out.println(signPack.unpackString);
            //解析工作秘钥
            signPack.unpack62Field();
//        if (unpack.get39FieldStatus()) {
//            unpack.unpack62Field();
//        }

            byte[] consume = client.sendPack(ConsumeTest.getPack());
            ConsumePack consumePack = new ConsumePack();
            consumePack.unpack(consume);
            System.out.println("解析结果：");
            System.out.println(consumePack.unpackString);

            client.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
