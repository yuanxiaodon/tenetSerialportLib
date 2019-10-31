package com.serialportlibrary.service;

/**
 * @author Yuanxd
 * @date 2018/12/26  下午2:55
 */
public interface ISerialPortService {

    /**
     * 发送十六进制异步的字符串数据
     *
     * @param data
     * @return
     */
    void sendData(String data);
    /**
     * 关闭串口
     */
    void close();
}
