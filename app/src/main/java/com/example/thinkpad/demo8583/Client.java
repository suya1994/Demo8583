package com.example.thinkpad.demo8583;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    private Socket client;
    private OutputStream out;
    private InputStream in;


    public Client(String host, int port) throws IOException {
        client = new Socket(host, port);
    }

    public void run() throws IOException {

        client.setKeepAlive(true);
        client.setSoTimeout(60 * 1000);

        //获取Socket的输出流，用来发送数据到服务端
        out = client.getOutputStream();
        //获取Socket的输入流，用来接收从服务端发送过来的数据
        in = client.getInputStream();

    }

    public void close() throws IOException {
        if (null != in){
            in.close();
        }
        if (null != out){
            out.close();
        }
        if (null != client){
            client.close();
        }
    }


    /**
     * 发送报文，并return返回报文
     * @param send 需要发出的报文数组
     * @return 返回的报文
     */
    public byte[] sendPack(byte[] send) throws IOException {

        byte[] receive = null;

        //发送数据到服务端
        out.write(send);
        System.out.println("\n发送报文:\n" + Utils.bytes2HexStr(send));
        out.flush();

        //拿到返回包的长度
        byte[] bytesLength = new byte[2];
        int read = in.read(bytesLength);
        int length = -1;
        if (-1 != read){
            System.out.println("收到报文:");
            length = Utils.bytes2HexInt(bytesLength);
            System.out.println("长度："+Utils.bytes2HexStr(bytesLength) + ",bcd : " + length);
        }

        //拿到返回包的报文
        if (-1 != length){
            receive = new byte[length];
            read = in.read(receive);
        }

        if (-1 != read){
            System.out.println("报文:\n" + Utils.bytes2HexStr(receive));
        }

        return receive;
    }







}
