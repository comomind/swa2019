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

public class MessageAdapter extends BaseAdapter{
    public static final String TAG = MessageAdapter.class.getName().toString();
    Context context;
    ArrayList<CcInfo> example;
    int duration_display;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public MessageAdapter(Context con)
    {
        this.context = con;
    }

    @Override
    public int getCount() {
        //    return example.length;
        example = DataManager.getInstance().getCcList();
        return example.size();
    }

    @Override
    public Object getItem(int position) {
        //    return example[position];
        example = DataManager.getInstance().getCcList();
        return example.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        example = DataManager.getInstance().getCcList();
        ConferenceList listItem = new ConferenceList(parent.getContext());

        CcInfo ccInfo = example.get(position);

        listItem.setText1(mFormat.format(ccInfo.getStartDate()));
        listItem.setText2(""+ccInfo.getDuration());
        listItem.setText3(ccInfo.getCcNumber());

       // Log.d(TAG, "mFormat.format(ccInfo) : !!!"+mFormat.format(ccInfo));
        Log.d(TAG, "getStartDate() : !!!"+ccInfo.getStartDate());
        Log.d(TAG, "getCcNumber() : !!!"+ccInfo.getCcNumber());
        Log.d(TAG, "getaList() : !!!"+ccInfo.getaList());
        Log.d(TAG, "getDuration() : !!!"+ccInfo.getDuration());
        Log.d(TAG, "DataManager.getInstance().getCcList() : !!!"+DataManager.getInstance().getCcList());
        Log.d(TAG, "example.size() : !!!"+example.size());


        return listItem;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
