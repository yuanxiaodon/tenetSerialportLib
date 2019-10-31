package com.serialportlibrary.service.impl;

import android.serialport.SerialPort;

import com.serialportlibrary.service.ISerialPortService;
import com.serialportlibrary.service.SerialPortListener;
import com.serialportlibrary.util.ByteStringUtil;
import com.serialportlibrary.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @author Yuanxd
 * @date 2018/12/26  下午2:42
 */
public class SerialPortService implements ISerialPortService, Runnable {
    //尝试读取数据间隔时间
    private static int RE_READ_WAITE_TIME = 20;
    //读取返回结果超时时间
    private Long mTimeOut = 100L;
    //串口地址
    private String mDevicePath;
    // 波特率
    private int mBaudrate;
    //数据位
    private int mDataBits;
    //停止位
    private int mStopBits;
    //标志位
    private int mParity;
    //默认完整包的长度
    private int mPkgLength = 1;
    //串口对象
    SerialPort mSerialPort;
    //输出流对象
    public OutputStream outputStream;
    //输入流对象
    public InputStream inputStream;
    //发送消息队列
    private byte[] sendLoopData = {127};
    //当前发送消息时间用于超时统计
    private volatile long currentTime = 0l;
    //是否正在运行
    private volatile boolean isRunning = true;
    // 每次收到实际长度
    int available = 0;
    // 定义一个包的最大长度
    int maxLength = 1024;
    // 当前已经收到包的总长度
    int currentLength = 0;
    //读数据
    byte[] buffer = new byte[1024];
    //串口监听回调
    private SerialPortListener portListener;


    /**
     * 初始化串口
     *
     * @param devicePath 串口地址
     * @param baudrate   波特率
     * @param timeOut    数据返回超时时间
     * @throws IOException 打开串口出错
     */
    public SerialPortService(String devicePath, int baudrate, Long timeOut, int pkgLength) throws IOException {
        mTimeOut = timeOut;
        mDevicePath = devicePath;
        mBaudrate = baudrate;
        mPkgLength = pkgLength;
        mSerialPort = new SerialPort(new File(mDevicePath), mBaudrate);
        inputStream = mSerialPort.getInputStream();
        outputStream = mSerialPort.getOutputStream();
    }


    /**
     * 初始化串口
     *
     * @param devicePath 串口地址
     * @param baudrate   波特率
     * @param timeOut    数据返回超时时间
     * @throws IOException 打开串口出错
     */
    public SerialPortService(String devicePath, int baudrate, Long timeOut, int dataBits, int stopBits, int parity, int pkgLength) throws IOException {
        mTimeOut = timeOut;
        mDevicePath = devicePath;
        mBaudrate = baudrate;
        mDataBits = dataBits;
        mStopBits = stopBits;
        mParity = parity;
        mPkgLength = pkgLength;
        mSerialPort = new SerialPort(new File(mDevicePath), mBaudrate, mDataBits, mStopBits, mParity);
        inputStream = mSerialPort.getInputStream();
        outputStream = mSerialPort.getOutputStream();
    }

    /**
     * 串口发送消息
     *
     * @param message
     */
    @Override
    public void sendData(String message) {
        sendLoopData = ByteStringUtil.hexStrToByteArray(message);
        try {
            outputStream.write(sendLoopData);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentTime = System.currentTimeMillis();
    }

    public void setPortListener(SerialPortListener portListener) {
        this.portListener = portListener;
    }

    /**
     * 关闭串口
     */
    @Override
    public void close() {
        LogUtil.e("关闭串口");
        currentTime = 0;
        try {
            isRunning = false;

            if (mSerialPort != null) {
                mSerialPort.closePort();
            }
            if (null != inputStream) {
                inputStream.close();
            }
            if (null != outputStream) {
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("关闭串口异常");
        }
    }


    /**
     * 是否输出日志
     *
     * @param debug
     */

    public void isOutputLog(boolean debug) {
        LogUtil.isDebug = debug;
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                if (inputStream == null) {
                    return;
                }
                try {
                    available = inputStream.available();
                    if (available > 0) {
                        // 防止超出数组最大长度导致溢出
                        if (available > maxLength - currentLength) {
                            available = maxLength - currentLength;
                        }
                        inputStream.read(buffer, currentLength, available);
                        currentLength += available;
                    }

                } catch (Exception e) {

                }
                if (currentLength < 0) {
                    currentLength = 0;
                }
                while (currentLength >= mPkgLength) {
                    String recinfo = new String(buffer, 0, currentLength);
                    currentTime = 0;
                    currentLength = 0;
                    LogUtil.e("接收到串口信息:" + recinfo);
                    if (null != portListener) {
                        portListener.onReceive(recinfo);
                    }
                }
//                    SystemClock.sleep(RE_READ_WAITE_TIME);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("接收到串口异常");
            }
            while (currentTime != 0 && (System.currentTimeMillis() - currentTime > mTimeOut)) {
                currentTime = 0;
                LogUtil.e("同步接收消息超时");
                if (null != portListener) {
                    portListener.onReceiveTimeout();
                }

            }
        }
    }


}

