package lg.dplakosh.lgvoipdemo;

import android.app.Application;
import android.util.Log;

import com.instacart.library.truetime.TrueTime;

import java.io.IOException;

public class MyApplication extends Application {
  private static final String TAG = MyApplication.class.getSimpleName();

  @Override
  public void onCreate() {
    super.onCreate();

    Log.d(TAG, "MyApplication created");

//    new Thread(new Runnable() {
//      @Override
//      public void run() {
//        try {
//          TrueTime.build().withNtpHost("10.0.1.240")
//              .initialize();
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      }
//    }).start();
  }
}
