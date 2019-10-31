# tenetSerialportLib
串口工具
[![](https://jitpack.io/v/yuanxiaodon/tenetSerialportLib.svg)](https://jitpack.io/#yuanxiaodon/tenetSerialportLib)

串口实例调用方法
SerialPortService  serialPortService = new SerialPortBuilder()
                        .setTimeOut(1000L)
                        .setBaudrate(9600)
                        .setDevicePath("dev/ttyS1")
                        .setDataBits(8)
                        .setStopBits(1)
                        .setParity(0)
                        .setPkgLength(18)
                        .createServiceFill();
                new Thread(serialPortService).start();
                serialPortService.isOutputLog(true);
