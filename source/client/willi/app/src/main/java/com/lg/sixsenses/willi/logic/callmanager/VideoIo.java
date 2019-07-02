package com.lg.sixsenses.willi.logic.callmanager;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UdpPort;
import com.lg.sixsenses.willi.ui.CcActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class VideoIo implements Camera.PreviewCallback {
  private static final String TAG = VideoIo.class.getSimpleName();

  private static final int TEX_NAME = 10;
//  private static final int PREVIEW_WIDTH = 480;
//  private static final int PREVIEW_HEIGHT = 640;
//  private static final int MAX_VIDEO_FRAME_SIZE =PREVIEW_HEIGHT*PREVIEW_WIDTH*4;
//  private static final int COMPRESS_QUALITY = 40;
  private int PREVIEW_WIDTH = 240;
  private int PREVIEW_HEIGHT = 320;
  private int COMPRESS_QUALITY = 25;

  private int MAX_VIDEO_FRAME_SIZE =PREVIEW_HEIGHT*PREVIEW_WIDTH*4;


  private static final int VIDEO_BUFFER_SIZE = 65507;
  private static final int ROTATE_DEGREE = -90;

  private Context context;
  private Handler handler;
  private int viewId;

  private int myViewId = -1;

  private Camera camera;
  private SurfaceTexture texture;
  private int frame = 0;

//  private Thread sendThread = null;
  private Thread receiveThread = null;

  private DatagramSocket receiveSocket;
  private DatagramSocket sendSocket;

  private InetAddress remoteIp;
  private int remotePort = 0;
  private int myPort = 0;

  private boolean isStartReceive = false;
  private boolean isStartSend = false;

  private boolean isReceiveThreadRun = false;
//  private boolean isSendThreadRun = false;


  private boolean isRealSender = false;

  private ArrayList<UdpPort> udpPortList;


  public VideoIo(Context context) {
    this.context = context;
    udpPortList = new ArrayList<UdpPort>();



  }

  public void setViewId(int viewId) {
    Log.d(TAG, "viewId: " + viewId);
    this.viewId = viewId;
  }

  public ArrayList<UdpPort> getUdpPortList() {
    return udpPortList;
  }

  public void setUdpPortList(ArrayList<UdpPort> udpPortList) {
    this.udpPortList = udpPortList;
  }

  public VideoIo(Context context, Handler handler, int viewId) {
    this.context = context;
    this.handler = handler;
    this.viewId = viewId;
  }

  public int getMyViewId() {
    return myViewId;
  }

  public void setMyViewId(int myViewId) {
    this.myViewId = myViewId;
  }

  public void setHandler(Handler handler) {
    this.handler = handler;
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

    Log.d(TAG, "VideoIo start receive request: local: " + port);


    receiveSocket = bindSocket(port);

    // start receive thread
    startReceiveThread();

    isStartReceive = true;

    Log.d(TAG, "VideoIo start receive done: local: " + port);

    return false;
  }

  public synchronized boolean startSend(InetAddress remoteIp, int remotePort) {
    if (isStartSend) {
      return true;
    }

    PREVIEW_WIDTH = DataManager.getInstance().getCamWidth();
    PREVIEW_HEIGHT = DataManager.getInstance().getCamHeight();
    COMPRESS_QUALITY = DataManager.getInstance().getComRate();
    Log.d(TAG,"Video Quality !!!!!!!!!!!!!!!!!!!!1 "+PREVIEW_WIDTH +" x "+ PREVIEW_HEIGHT+" / "+COMPRESS_QUALITY);
    Log.d(TAG, "VideoIo start send request: isRealSender: "+isRealSender+" remote: " + remoteIp + " " + remotePort);

    this.remoteIp = remoteIp;
    this.remotePort = remotePort;

    // start send thread
    if(isRealSender)
      startSendThread();

    isStartSend = true;

    Log.d(TAG, "VideoIo start send done: remote: " + remoteIp + " " + remotePort);

    return false;
  }

  public synchronized boolean stopAll() {
    if (!isStartReceive && !isStartSend) {
      Log.d(TAG, "VideoIo already stopped");
      return true;
    }

    Log.d(TAG, "VideoIo stop request: isRealSender : "+isRealSender+" remote:  " + remoteIp + " " + remotePort);

    sendMessage(CcActivity.CcActivityHandler.CMD_VIEW_CLEAR, null);

    // terminate receive thread
    stopReceiveThread();

    // terminate send thread
    if(isRealSender)
      stopSendThread();

    Log.d(TAG, "VideoIo stop done: remote: " + remoteIp + " " + remotePort);

    return false;
  }

  public void startReceiveThread() {
    isReceiveThreadRun = true;
    receiveThread = new Thread(new Runnable() {
      @Override
      public void run() {
        Log.d(TAG, "receiveThread started, tid: " + Thread.currentThread().getId());

        try {
          while(isReceiveThreadRun) {
            byte[] packetBuffer = new byte[VIDEO_BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(packetBuffer, VIDEO_BUFFER_SIZE);

            receiveSocket.receive(packet);
            if (packet.getLength() > 0) {

              // send message to handler
              sendMessage(CcActivity.CcActivityHandler.CMD_VIEW_UPDATE, packet.getData());
            }
          }
        } catch (SocketException e) {
          isReceiveThreadRun = false;
          Log.e(TAG, "SocketException: " + e.toString());
        } catch (IOException e) {
          isReceiveThreadRun = false;
          Log.e(TAG, "IOExceptino: " + e.toString());
        }

        // close socket
        receiveSocket.disconnect();
        receiveSocket.close();
      }
    });
    receiveThread.start();
  }

  public void sendMessage(int cmd, byte[] imageBytes) {
    if (handler == null) {
      Log.d(TAG, "sendMessage, handler is null");
      return;
    }
    Message message = handler.obtainMessage();

    Bundle bundle = new Bundle();
    bundle.putInt(CcActivity.CcActivityHandler.KEY_IMAGE_VIEW_ID, viewId);
    bundle.putByteArray(CcActivity.CcActivityHandler.KEY_IMAGE_BYTES, imageBytes);

    message.setData(bundle);
    message.what = cmd;
    handler.sendMessage(message);
  }

   public void sendMessageForMyView(int cmd, byte[] imageBytes) {
    if (handler == null) {
      Log.d(TAG, "sendMessage, handler is null");
      return;
    }
    Message message = handler.obtainMessage();

    Bundle bundle = new Bundle();
    bundle.putInt(CcActivity.CcActivityHandler.KEY_IMAGE_VIEW_ID, myViewId);
    bundle.putByteArray(CcActivity.CcActivityHandler.KEY_IMAGE_BYTES, imageBytes);

    message.setData(bundle);
    message.what = cmd;
    handler.sendMessage(message);
  }


  public void stopReceiveThread() {
    if (receiveThread != null && receiveThread.isAlive()) {
      isReceiveThreadRun = false;
      receiveSocket.close();
      Log.d(TAG, "receiveThread join started");
      isReceiveThreadRun = false;
      try {
        receiveThread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
        Log.d(TAG, "receiveThread join interrupted");
      }
      Log.d(TAG, "receiveThread join success");
    }

    receiveThread = null;
    receiveSocket = null;

    isStartReceive = false;
  }

  public void startSendThread() {
    try {
      sendSocket = new DatagramSocket();
    } catch (SocketException e) {
      e.printStackTrace();
    }

    openCamera();
  }

  public void stopSendThread() {
    closeCamera();
    if(sendSocket != null) {
      sendSocket.disconnect();
      sendSocket.close();
      sendSocket = null;
    }
    isStartSend = false;
  }

  private void openCamera() {
    if (camera != null) {
      return;
    }

    frame = 0;
    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    int cameraCount = Camera.getNumberOfCameras();
    for (int i=0 ; i<cameraCount ; i++) {
      Camera.getCameraInfo(i, cameraInfo);
      if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        try {
          camera = Camera.open(i);

        } catch (RuntimeException e) {
          Log.e(TAG, "camera failed to open: " + e.getLocalizedMessage());
        }
      }
    }

    texture = new SurfaceTexture(TEX_NAME);
    try {
      camera.setPreviewTexture(texture);
    } catch (IOException e) {
      Log.e(TAG, e.getMessage());
    }
    Camera.Parameters params = camera.getParameters();
    //params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
    params.setPreviewSize(240, 320);
    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

//    List<Camera.Size> sizeList = params.getSupportedPreviewSizes();
//    for(Camera.Size size : sizeList)
//      Log.d(TAG,"Support resolution : " +size.width+ " x "+size.height);

    Log.d(TAG," @@@@@@@@@@@@@@@@@@@@@@@ Set Video Quality : " +PREVIEW_WIDTH+ " x "+PREVIEW_HEIGHT+" : "+COMPRESS_QUALITY);

    camera.setParameters(params);
    camera.setPreviewCallbackWithBuffer(this);

    camera.addCallbackBuffer(new byte[MAX_VIDEO_FRAME_SIZE]);
    camera.addCallbackBuffer(new byte[MAX_VIDEO_FRAME_SIZE]);
    camera.addCallbackBuffer(new byte[MAX_VIDEO_FRAME_SIZE]);
    camera.startPreview();
  }

  private void closeCamera() {
    if (camera == null) {
      return;
    }
    camera.stopPreview();
    camera.setPreviewCallbackWithBuffer(null);
    camera.release();
    camera = null;
  }

  private DatagramSocket bindSocket(int port) {
    boolean isBind = false;
    DatagramSocket socket = null;
    Log.d(TAG, "bindSocket try, port: " + port);
    while (!isBind) {
      try {
        socket = new DatagramSocket(null);
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(port));
        isBind = true;
      } catch (SocketException e) {
        e.printStackTrace();
        port++;
      }
    }
    myPort = port;
    Log.d(TAG, "bindSocket success, port: " + myPort);
    return socket;
  }

  @Override
  public void onPreviewFrame(byte[] data, Camera camera) {
    frame++;
    if ((frame % 2) != 0) {
      camera.addCallbackBuffer(data);
      return;
    }
    Camera.Parameters params = camera.getParameters();
    int format = params.getPreviewFormat();

    // YUV formats require more conversion
    if (format == ImageFormat.NV21 || format == ImageFormat.YUY2 || format == ImageFormat.NV16) {
      int w = params.getPreviewSize().width;
      int h = params.getPreviewSize().height;

      // Get the YUV image
      YuvImage yuvImage = new YuvImage(data, format, w, h, null);

      // Convert YUV to Jpeg
      Rect rect = new Rect(0, 0, w, h);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      //yuvImage.compressToJpeg(rect, COMPRESS_QUALITY, outputStream);
      yuvImage.compressToJpeg(rect, 25, outputStream);
      byte[] bytes = outputStream.toByteArray();


      // send message to handler for update myView
      if(myViewId > 0) sendMessageForMyView(CcActivity.CcActivityHandler.CMD_VIEW_UPDATE, bytes);

      udpSend(bytes);
    }
    camera.addCallbackBuffer(data);
  }

  private void udpSend(final byte[] bytes) {
    Thread sendThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
            for(UdpPort udpPort : udpPortList) {
            Log.d(TAG, "Video send to " + udpPort.getIp() + " " + udpPort.getVideoPort());
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(udpPort.getIp()), udpPort.getVideoPort());
            sendSocket.send(packet);
          }
        } catch (SocketException e) {
          Log.e(TAG, "Failed, SocketException: " + e);
        } catch (IOException e) {
          Log.e(TAG, "Failed, IOException: " + e);
        }
      }
    });
    sendThread.start();
  }

  public boolean isRealSender() {
    return isRealSender;
  }

  public void setRealSender(boolean realSender) {
    isRealSender = realSender;
  }
}
