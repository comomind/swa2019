package com.lg.sixsenses.willi.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lg.sixsenses.willi.R;

public class CCList extends LinearLayout{
    TextView text1;
    TextView text2;
    TextView text3;

    public CCList(Context context) {
        super(context);
        inflation_init(context);

        text1 = (TextView)findViewById(R.id.textName);
        text2 = (TextView)findViewById(R.id.textEmail);
        text3 = (TextView)findViewById(R.id.textNum);
    }

    private void inflation_init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_cclist_layout, this, true);
    }

    public void setText1(String example){
        text1.setText(example);
    }
    public void setText2(String example){
        text2.setText(example);
    }
    public void setText3(String example){
        text3.setText(example);
    }
}
