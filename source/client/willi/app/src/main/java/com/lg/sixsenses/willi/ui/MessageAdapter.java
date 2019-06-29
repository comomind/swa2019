package com.lg.sixsenses.willi.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lg.sixsenses.willi.repository.CcInfo;
import com.lg.sixsenses.willi.repository.UserInfo;
import com.lg.sixsenses.willi.repository.DataManager;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter{
    public static final String TAG = MessageAdapter.class.getName().toString();
    Context context;
    ArrayList<CcInfo> example;


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
        Log.d(TAG, "example list size:"+example.size());
        CcInfo ccInfo = example.get(position);
        listItem.setText1(ccInfo.getStartDate());
        listItem.setText2(ccInfo.getCcNumber());

        return listItem;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
