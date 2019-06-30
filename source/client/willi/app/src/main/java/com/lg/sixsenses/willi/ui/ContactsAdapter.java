package com.lg.sixsenses.willi.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lg.sixsenses.willi.repository.UserInfo;
import com.lg.sixsenses.willi.repository.DataManager;

import java.util.ArrayList;

public class ContactsAdapter extends BaseAdapter {

    public static final String TAG = ContactsAdapter.class.getName().toString();
    Context context;
    ArrayList<UserInfo> example;


    public ContactsAdapter(Context con)
    {
        this.context = con;
    }

    @Override
    public int getCount() {
        //    return example.length
        example = DataManager.getInstance().getContactList();
        if(example != null )return example.size();
        else return 0;
    }

    @Override
    public Object getItem(int position) {
        //    return example[position];
        example = DataManager.getInstance().getContactList();
        if(example != null) return example.get(position);
        else return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        example = DataManager.getInstance().getContactList();
        ContactList listItem = new ContactList(parent.getContext());
        Log.d(TAG, "example list size:"+example.size());
        UserInfo userInfo = example.get(position);
        listItem.setText1(userInfo.getName());
        listItem.setText2(userInfo.getEmail());
        listItem.setText3(userInfo.getPhoneNum());


        return listItem;

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
