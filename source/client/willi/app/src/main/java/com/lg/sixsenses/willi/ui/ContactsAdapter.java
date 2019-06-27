package com.lg.sixsenses.willi.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lg.sixsenses.willi.repository.UserInfo;
import com.lg.sixsenses.willi.repository.DataManager;

import java.util.ArrayList;

public class ContactsAdapter extends BaseAdapter {

    ArrayList<UserInfo> example = DataManager.getInstance().getContactList();


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
        /*
        String p = null;
            ArrayList<UserInfo> contactList = (ArrayList<UserInfo>)(data.getData());
            for(UserInfo userInfo : contactList)
            {
                p += userInfo.toString();
            }
        */
        String gName = null;
        String gEmail = null;
        String gNum = null;
        String gState = null;
        ContactList listItem = new ContactList(parent.getContext());
        //   UserInfo Name;
        //   listItem.setText1(example[position]);
        for(UserInfo userInfo : example)
        {
            gName = userInfo.getName();
            gEmail = userInfo.getEmail();
            gNum = userInfo.getPhoneNum();
            gState = userInfo.getLoginStatus();
        }
        listItem.setText1(gName);
        listItem.setText2(gEmail);
        listItem.setText3(gNum);
        listItem.setText4(gState);

        listItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //     Toast.makeText(this,"Test",Toast.LENGTH_LONG);
                // Intent intent = new Intent(getActivity(),ContactModifyActivity.class);
                // startActivity(intent);
            }
        });

        return listItem;

    }
}
