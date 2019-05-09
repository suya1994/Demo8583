package com.example.thinkpad.demo8583.bean;

public class Field {
    /**
     * 是否存在
     */
    public boolean isExist;
    /**
     * 格式：ans binary bcd
     */
    public FieldType type;
    /**
     * 定长，或是不定长；
     */
    public LengthFormat lengthFormat;
    /**
     * 数据长度
     */
    public int length;
    /**
     * BCD格式，左靠还是右靠
     */
    public BCDFormat bcdFormat;

    public byte[] bytesData;

    public Field() {
        isExist = false;
        lengthFormat = LengthFormat.FIXED;
        //默认右靠,往左补0
        bcdFormat = BCDFormat.FORMAT_BY_RIGHT;
    }
}
