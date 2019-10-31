package android.serialport;

import android.util.Log;

import com.serialportlibrary.util.LogUtil;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 串口通信，打开串口，读写数据
 */
public class SerialPort {

    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    static {
        System.loadLibrary("serial_port");
    }

    /**
     * 打开串口
     *
     * @param device
     * @param baudrate
     */
    public SerialPort(File device, int baudrate) throws IOException {
        if (!device.canRead() || !device.canWrite()) {
            try {
                Process su;
                su = Runtime.getRuntime().exec("su");
                String cmd = "chmod 777 " + device.getAbsolutePath();
                su.getOutputStream().write(cmd.getBytes());
                su.getOutputStream().flush();
                int waitFor = su.waitFor();
                boolean canRead = device.canRead();
                boolean canWrite = device.canWrite();
                if (waitFor != 0 || !canRead || !canWrite) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mFd = open(device.getAbsolutePath(), baudrate);

        if (mFd == null) {
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }




    /***
     * 构造方法
     * @param device 串口文件
     * @param baudrate 波特率
     * @param dataBits 数据位
     * @param stopBits 停止位
     * @param parity   校验位
     * @throws SecurityException
     * @throws IOException
     */
    public SerialPort(File device, int baudrate, int dataBits,int stopBits,int parity)
            throws SecurityException, IOException {
        LogUtil.e("SerialPort native open");
        /* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }
        mFd = openFull(device.getAbsolutePath(), baudrate, parity, dataBits, stopBits);
        if (mFd == null) {
            LogUtil.e("SerialPort native open returns null");
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    /**
     * 关闭串口
     */
    public void closePort() {
        if (this.mFd != null) {
            try {
                this.close();
                this.mFd = null;
                this.mFileInputStream = null;
                this.mFileOutputStream = null;
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }
    }

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    // 调用JNI中 打开方法的声明
    private native static FileDescriptor openFull(String path, int baudrate, int parity, int dataBits, int stopBit);
    /**
     * JNI，设备地址和波特率
     */
    private native static FileDescriptor open(String path, int baudrate);

    private native void close();


}
