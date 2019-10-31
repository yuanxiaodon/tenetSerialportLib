package com.serialportlibrary.service;

/**
 * Created by yuanxd on 2019/10/30 0030.
 */

public interface SerialPortListener {

    public void onReceive(String content);

    public void onReceiveTimeout();
}
