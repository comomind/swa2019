package com.lg.sixsenses.willi.logic.callmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.lg.sixsenses.willi.ui.TestActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class VideoIo implements Camera.PreviewCallback {
  private static final String TAG = VideoIo.class.getSimpleName();

  private static final int TEX_NAME = 10;
  private static final int PREVIEW_WIDTH = 480;
  private static final int PREVIEW_HEIGHT = 640;
  private static final int MAX_VIDEO_FRAME_SIZE =PREVIEW_HEIGHT*PREVIEW_WIDTH*4;
  private static final int COMPRESS_QUALITY = 40;
  private static final int VIDEO_BUFFER_SIZE = 65507;
  private static final int ROTATE_DEGREE = -90;

  private Context context;
  private Handler handler;
  private int viewId;

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

  public VideoIo(Context context) {
    this.context = context;
  }

  public void setViewId(int viewId) {
    this.viewId = viewId;
  }

  public VideoIo(Context context, Handler handler, int viewId) {
    this.context = context;
    this.handler = handler;
    this.viewId = viewId;
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

    receiveSocket = bindSocket(port);

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

    sendMessage(TestActivity.TestActivityHandler.CMD_VIEW_CLEAR, null);

    // terminate receive thread
    stopReceiveThread();

    // terminate send thread
    stopSendThread();

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
              sendMessage(TestActivity.TestActivityHandler.CMD_VIEW_UPDATE, packet.getData());
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
    bundle.putInt(TestActivity.TestActivityHandler.KEY_IMAGE_VIEW_ID, viewId);
    bundle.putByteArray(TestActivity.TestActivityHandler.KEY_IMAGE_BYTES, imageBytes);

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

    sendSocket.disconnect();
    sendSocket.close();
    sendSocket = null;

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
    params.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT);
    params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

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
      yuvImage.compressToJpeg(rect, COMPRESS_QUALITY, outputStream);
      byte[] bytes = outputStream.toByteArray();
      udpSend(bytes);
    }
    camera.addCallbackBuffer(data);
  }

  private void udpSend(final byte[] bytes) {
    Thread sendThread = new Thread(new Runnable() {
      @Override
      public void run() {

        try {
          DatagramPacket packet = new DatagramPacket(bytes, bytes.length, remoteIp, remotePort);
          sendSocket.send(packet);
        } catch (SocketException e) {
          Log.e(TAG, "Failed, SocketException: " + e);
        } catch (IOException e) {
          Log.e(TAG, "Failed, IOException: " + e);
        }
      }
    });
    sendThread.start();
  }

}