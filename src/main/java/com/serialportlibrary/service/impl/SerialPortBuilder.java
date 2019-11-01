package com.serialportlibrary.service.impl;

import java.io.IOException;

/**
 * @author Yuanxd
 * @date 2018/12/26  下午3:39
 */
public class SerialPortBuilder {
    //读取返回结果超时时间
    private Long mTimeOut = 100L;
    //串口地址
    private String mDevicePath;
    //波特率
    private int mBaudrate;
    //数据位
    private int mDataBits;
    //停止位
    private int mStopBits;
    //参数：校验位置 NONE
    private int mParity;
    //默认完整包的长度
    private int mPkgLength = 1;
    //是否自定义数据接收方式
    private boolean mAnalyseReceive = false;

    public SerialPortBuilder setBaudrate(int baudrate) {
        mBaudrate = baudrate;
        return this;
    }

    public SerialPortBuilder setDataBits(int mDataBits) {
        this.mDataBits = mDataBits;
        return this;
    }

    public SerialPortBuilder setStopBits(int mStopBits) {
        this.mStopBits = mStopBits;
        return this;
    }

    public SerialPortBuilder setParity(int mParity) {
        this.mParity = mParity;
        return this;
    }

    public SerialPortBuilder setDevicePath(String devicePath) {
        mDevicePath = devicePath;
        return this;
    }


    public SerialPortBuilder setTimeOut(Long timeOut) {
        mTimeOut = timeOut;
        return this;
    }


    public SerialPortBuilder setPkgLength(int mPkgLength) {
        this.mPkgLength = mPkgLength;
        return this;
    }


    public SerialPortBuilder setmAnalyseReceive(boolean isAnalyseReceive) {
        this.mAnalyseReceive = isAnalyseReceive;
        return this;
    }

    public SerialPortService createService() {
        SerialPortService serialPortService = null;
        try {
            serialPortService = new SerialPortService(mDevicePath, mBaudrate, mTimeOut, mPkgLength,mAnalyseReceive);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serialPortService;
    }

    public SerialPortService createServiceFill() {
        SerialPortService serialPortService = null;
        try {
            serialPortService = new SerialPortService(mDevicePath, mBaudrate, mTimeOut, mDataBits, mStopBits, mParity, mPkgLength,mAnalyseReceive);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serialPortService;
    }


}
