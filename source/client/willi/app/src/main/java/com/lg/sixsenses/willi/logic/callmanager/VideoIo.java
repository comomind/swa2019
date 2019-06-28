package com.lg.sixsenses.willi.logic.callmanager;

import android.content.Context;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Observer;

public class VideoIo {
  private static final String TAG = VideoIo.class.getSimpleName();

  private Context context;

  private Thread sendThread = null;
  private Thread receiveThread = null;

  private DatagramSocket receiveSocket;

  private InetAddress remoteIp;
  private int remotePort = 0;
  private int myPort = 0;

  private boolean isStartReceive = false;
  private boolean isStartSend = false;

  private boolean isReceiveThreadRun = false;
  private boolean isSendThreadRun = false;

  public VideoIo(Context context) {
    this.context = context;
  }

  public int getMyPort() {
    return myPort;
  }

  public void setMyPort(int myPort) {
    this.myPort = myPort;
  }

  public synchronized boolean startReceive(int port) {
    if (isStartReceive) {
      return true;
    }

    bindSocket(port);

    // start receive thread
    startReceiveThread();

    isStartReceive = true;
    return false;
  }

  public synchronized boolean startSend(InetAddress remoteIp, int remotePort) {
    if (isStartSend) {
      return true;
    }

    this.remoteIp = remoteIp;
    this.remotePort = remotePort;

    // start send thread
    startSendThread();

    isStartSend = true;
    return false;
  }

  public synchronized boolean stopAll() {
    if (!isStartReceive && !isStartSend) {
      Log.d(TAG, "VideoIo already stopped");
      return true;
    }

    // terminate receive thread
    stopReceiveThread();

    // terminate send thread
    stopSendThread();

    return false;
  }

  public void startReceiveThread() {
    // TODO:


  }

  public void stopReceiveThread() {
    // TODO:


    isStartReceive = false;
  }

  public void startSendThread() {
    // TODO:


  }

  public void stopSendThread() {
    // TODO:


    isStartSend = false;
  }

  private void bindSocket(int port) {
    boolean isBind = false;
    Log.d(TAG, "bindSocket try, port: " + port);
    while (!isBind) {
      try {
        receiveSocket = new DatagramSocket(null);
        receiveSocket.setReuseAddress(true);
        receiveSocket.bind(new InetSocketAddress(port));
        isBind = true;
      } catch (SocketException e) {
        e.printStackTrace();
        port++;
      }
    }
    myPort = port;
    Log.d(TAG, "bindSocket success, port: " + myPort);
  }
}
