package com.lg.sixsenses.willi.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.logic.callmanager.CallHandler;
import com.lg.sixsenses.willi.repository.UserInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link messageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link messageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class messageFragment extends Fragment {
    public static final String TAG = ContactsFragment.class.getName().toString();
    Button buttonContactCall;
    TextView textViewContactResult;

    ArrayList<UserInfo> selected;
    MessageAdapter adapter;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        textViewContactResult = (TextView)view.findViewById(R.id.textViewContactResult);

        listView = (ListView) view.findViewById(R.id.conference_list);
        adapter = new MessageAdapter(getActivity());
        listView.setAdapter(adapter);
        selected = new ArrayList<UserInfo>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo userInfo =(UserInfo) adapter.getItem(position);
                if(selected.contains(userInfo))
                {
                    view.setBackgroundColor(Color.WHITE);
                    selected.remove(userInfo);
                }
                else
                {
                    view.setBackgroundColor(Color.GRAY);
                    selected.add(userInfo);
                }
                textViewContactResult.setText("");
            }
        });

        // Inflate the layout for this fragment

        buttonContactCall = (Button)view.findViewById(R.id.buttonContactCall);
        buttonContactCall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(selected.size() != 1)
                {
                    textViewContactResult.setText("Please select only one!");
                }
                else
                {
                    CallHandler.getInstance().callRequest(selected.get(0).getPhoneNum());
                    Intent intent = new Intent(getActivity(),CallStateActivity.class);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();
        if(selected != null) selected.clear();
    }

}
