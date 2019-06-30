package com.lg.sixsenses.willi.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.codec.audio.AbstractAudioCodecFactory;
import com.lg.sixsenses.willi.codec.audio.AudioCodecConst;
import com.lg.sixsenses.willi.codec.audio.AudioCodecFactory;
import com.lg.sixsenses.willi.logic.callmanager.AudioIo;
import com.lg.sixsenses.willi.logic.callmanager.CallHandler;
import com.lg.sixsenses.willi.logic.callmanager.CcHandler;
import com.lg.sixsenses.willi.logic.callmanager.VideoIo;
import com.lg.sixsenses.willi.repository.CcInfo;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UdpInfo;
import com.lg.sixsenses.willi.repository.UdpPort;

import java.util.ArrayList;

public class CcActivity extends AppCompatActivity {
    public static final String TAG = CcActivity.class.getName().toString();
    private String ccNumber;
    private ImageView imageViewCc1;
    private ImageView imageViewCc2;
    private ImageView imageViewCc3;
    private ImageView imageViewCc4;

    private CcActivityHandler handler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cc);

        imageViewCc1 = (ImageView)findViewById(R.id.imageViewCc1); // 나화면 보여 줄 곳
        imageViewCc2 = (ImageView)findViewById(R.id.imageViewCc2);
        imageViewCc3 = (ImageView)findViewById(R.id.imageViewCc3);
        imageViewCc4 = (ImageView)findViewById(R.id.imageViewCc4);

        Intent intent = getIntent();
        ccNumber = intent.getStringExtra("ccNumber");
        Log.d(TAG,"onCreate ccNumber:"+ccNumber);
        Log.d(TAG,"imageView2 :"+imageViewCc2.getId());
        Log.d(TAG,"imageView3 :"+imageViewCc3.getId());
        Log.d(TAG,"imageView4 :"+imageViewCc4.getId());

        handler = new CcActivity.CcActivityHandler();

        CcHandler.getInstance().startCcHandler();
        CcHandler.getInstance().setHandler(handler);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(imageViewCc2.getId());
        list.add(imageViewCc3.getId());
        list.add(imageViewCc4.getId());

        CcHandler.getInstance().setViewId(list);
        CcHandler.getInstance().setContext(getApplicationContext());
    }

    public void buttonCcStartClick(View view)
    {
        Log.d(TAG,"buttonCcStartClick ccNumber:"+ccNumber);
        CcHandler.getInstance().startCc(ccNumber);
    }

    public void buttonCcDisconnectClick(View view)
    {

        CcHandler.getInstance().rejectCc(ccNumber);
    }



    public class CcActivityHandler extends Handler {
        // for Message.what
        public static final int CMD_VIEW_UPDATE = 1;
        public static final int CMD_VIEW_CLEAR = 2;

        // for Message kdy
        public static final String KEY_IMAGE_VIEW_ID = "image_view_id";
        public static final String KEY_IMAGE_BYTES = "image_bytes";


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.d(TAG, "handleMessage: " + msg.what);

            switch (msg.what) {
                case CMD_VIEW_UPDATE: {
                    Bundle bundle = msg.getData();

                    int viewId = bundle.getInt(KEY_IMAGE_VIEW_ID);
                    Log.d(TAG, "viewId(msg)" + viewId);

                    byte[] imageBytes = bundle.getByteArray(KEY_IMAGE_BYTES);

                    final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    final Matrix matrix = new Matrix();
                    matrix.postRotate(-90);
                    final Bitmap rotator = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.getWidth(), bitmap.getHeight(), matrix,
                            true);

                    // TODO: get image view by id
                    ImageView imageView = (ImageView)findViewById(viewId);
                    imageView.setImageBitmap(rotator);
                }
                break;

                case CMD_VIEW_CLEAR: {
                    Bundle bundle = msg.getData();
                    int viewId = bundle.getInt(KEY_IMAGE_VIEW_ID);
                    ImageView imageView = (ImageView)findViewById(viewId);
                    imageView.setImageBitmap(null);
                }
                break;

                default: {
                    Log.d(TAG, "CcActivityHandler received suspicious msg!");
                }
                break;
            }


        }
    }


}
