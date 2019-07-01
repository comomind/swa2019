package com.lg.sixsenses.willi.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.logic.servercommmanager.CCRegisterBody;
import com.lg.sixsenses.willi.logic.servercommmanager.RestManager;
import com.lg.sixsenses.willi.repository.CcInfo;
import com.lg.sixsenses.willi.repository.RegisterInfo;
import com.lg.sixsenses.willi.repository.UserInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ContactConferenceCallActivity extends AppCompatActivity {
    public static final String TAG = ConferenceFragment.class.getName().toString();
 //   SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private RestManager restManager;
    private Button Confirm;
    DatePicker mDate;
    TextView mTxtDate;
    TextView hour;
    TextView min;
    TextView Duration;
    TextView textViewResult;
    int nhour;
    int nmin;
    int nduration;
    private String email0;
    private String email1;
    private String email2;
    ArrayList<CcInfo> selected;
    private String email;
    private TextView textViewContactUpdateEmail;

    public void close(View view)
    {
        finish();
    }

    public void Confirm(View view) {
        selected = new ArrayList<CcInfo>();

        if (hour.getText().toString().length() == 0) {
            //Toast.makeText(getApplicationContext(),"Please enter Email!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter hour!");
            return;
        }
        if (min.getText().toString().length() == 0) {
            //Toast.makeText(getApplicationContext(),"Please enter Name!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter min!");
            return;
        }
        if (Duration.getText().toString().length() == 0) {
            //Toast.makeText(getApplicationContext(),"Please enter Name!",Toast.LENGTH_SHORT).show();
            textViewResult.setText("Please enter duration!");
            return;
        }

        CCRegisterBody ccRegisterBody = new CCRegisterBody();
        Date date = new Date();

        Log.d(TAG, "mDate.getYear() !!!"+mDate.getYear());
        Log.d(TAG, "mDate.getMonth() !!!"+mDate.getMonth()+1);
        Log.d(TAG, "mDate.getDayOfMonth() !!!"+mDate.getDayOfMonth());

        nhour = Integer.parseInt(hour.getText().toString());
        nmin = Integer.parseInt(min.getText().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.set(mDate.getYear(),mDate.getMonth(),mDate.getDayOfMonth()+1,nhour,nmin);
        date = calendar.getTime();


        Log.d(TAG, "nhour !!!"+nhour);
        Log.d(TAG, "nmin !!!"+nmin);
        nduration = Integer.parseInt(Duration.getText().toString());
        Log.d(TAG, "nduration!!!"+nduration);

        ccRegisterBody.setStartDate(date);
        ccRegisterBody.setDuration(nduration);

        Intent intent = getIntent();
        ArrayList<String> emailList = new ArrayList<String>();
        int size = intent.getIntExtra("size",0);
        for(int i=0;i<size;i++)
        {
            String key = "Email"+(i+1);
            String email = intent.getStringExtra(key);
            Log.d(TAG, "Key:" + key+"email!!!"+email);
            emailList.add(email);
        }
        ccRegisterBody.setaList(emailList);

        RestManager rest = new RestManager();
        rest.sendCCRegister(ccRegisterBody);

        textViewResult = (TextView)findViewById(R.id.textViewResult);
        textViewResult.setText("Success video conference reservation!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_conference_call);

        Confirm = (Button)findViewById(R.id.AddButton);
        mDate = (DatePicker)findViewById(R.id.datepicker);
        mTxtDate = (TextView)findViewById(R.id.txtdate);
        hour = (TextView)findViewById(R.id.hour);
        min = (TextView)findViewById(R.id.min);
        Duration = (TextView)findViewById(R.id.Duration);

        //처음 DatePicker를 오늘 날짜로 초기화한다.
        //그리고 리스너를 등록한다.
        mDate.init(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    //값이 바뀔때마다 텍스트뷰의 값을 바꿔준다.
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        //monthOfYear는 0값이 1월을 뜻하므로 1을 더해줌 나머지는 같다.
                        mTxtDate.setText(String.format("%d/%d/%d", year,monthOfYear + 1, dayOfMonth));
                    }
                });

        //  textViewResult = (TextView)findViewById(R.id.textViewResult);
        //  textViewResult.setText(null
        restManager = new RestManager();
    }
}
