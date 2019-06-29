package com.lg.sixsenses.willi.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lg.sixsenses.willi.R;

public class ConferenceList extends LinearLayout{
    TextView text1;
    TextView text2;

    public ConferenceList(Context context) {
        super(context);
        inflation_init(context);

        text1 = (TextView)findViewById(R.id.starttimelist);
        text2 = (TextView)findViewById(R.id.durationlist);
    }

    private void inflation_init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_messagelist_layout, this, true);
    }

    public void setText1(String example){
        text1.setText(example);
    }
    public void setText2(String example){
        text2.setText(example);
    }
}
