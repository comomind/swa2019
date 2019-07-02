package com.lg.sixsenses.willi.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lg.sixsenses.willi.logic.servercommmanager.CCRegisterBody;
import com.lg.sixsenses.willi.logic.servercommmanager.RestManager;
import com.lg.sixsenses.willi.repository.CcInfo;
import com.lg.sixsenses.willi.repository.UserInfo;
import com.lg.sixsenses.willi.repository.DataManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CCAdapter extends BaseAdapter{
    public static final String TAG = CCAdapter.class.getName().toString();
    Context context;
    ArrayList<CcInfo> example;
    int duration_display;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public CCAdapter(Context con)
    {
        this.context = con;
    }

    @Override
    public int getCount() {
        int result = 0;
        example = DataManager.getInstance().getCcList();
        if (example != null) {
            result = example.size();
        }
        return result;
    }

    @Override
    public Object getItem(int position) {
        Object result = null;
        example = DataManager.getInstance().getCcList();
        if (example != null) {
            result = example.get(position);
        }
        return result;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CCList listItem = new CCList(parent.getContext());

        example = DataManager.getInstance().getCcList();
        if (example != null) {
            CcInfo ccInfo = example.get(position);

            listItem.setText1(mFormat.format(ccInfo.getStartDate()));
            listItem.setText2(""+ccInfo.getDuration());
            listItem.setText3(ccInfo.getCcNumber());
        }

        return listItem;
    }
}
