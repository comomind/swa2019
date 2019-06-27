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
    ArrayList<UserInfo> example = DataManager.getInstance().getContactList();

    public ContactsAdapter(Context con)
    {
        this.context = con;
    }

    @Override
    public int getCount() {
        //    return example.length;
        return example.size();
    }

    @Override
    public Object getItem(int position) {
        //    return example[position];
        return example.toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String gName = null;
        String gEmail = null;
        String gNum = null;
        String gState = null;
        ContactList listItem = new ContactList(parent.getContext());
        Log.d(TAG, "example list size:"+example.size());
        UserInfo userInfo = example.get(position);
        listItem.setText1(userInfo.getName());
        listItem.setText2(userInfo.getEmail());
        listItem.setText3(userInfo.getPhoneNum());
        listItem.setText4(userInfo.getLoginStatus());


        listItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "setOnClickListener");
                 Intent intent = new Intent(context,ContactModifyActivity.class);
                 context.startActivity(intent);
            }
        });


        return listItem;

    }
}
