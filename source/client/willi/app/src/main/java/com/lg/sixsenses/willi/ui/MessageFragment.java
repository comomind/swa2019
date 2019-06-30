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
import com.lg.sixsenses.willi.repository.CcInfo;
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
public class MessageFragment extends Fragment {
    public static final String TAG = MessageFragment.class.getName().toString();
    Button buttonContactCall;
    TextView textViewContactResult;

    ArrayList<CcInfo> selected;
    MessageAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        textViewContactResult = (TextView)view.findViewById(R.id.textViewContactResult);

        ListView listView = (ListView) view.findViewById(R.id.conference_list);
        adapter = new MessageAdapter(getActivity());
       // adapter = new MessageAdapter(this, R.layout.item, selected);
        listView.setAdapter(adapter);
        selected = new ArrayList<CcInfo>();
        Log.d(TAG, "onCreateView_conference !!!!: " );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * ListView의 Item을 Click 할 때 수행할 동작
             * @param parent 클릭이 발생한 AdapterView.
             * @param view 클릭 한 AdapterView 내의 View(Adapter에 의해 제공되는 View).
             * @param position 클릭 한 Item의 position
             * @param id 클릭 된 Item의 Id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Listview click : "+ position );
                CcInfo ccInfo =(CcInfo) adapter.getItem(position);
                if(selected.contains(ccInfo))
                {
                    view.setBackgroundColor(Color.WHITE);
                    selected.remove(ccInfo);
                    Log.d(TAG, "Listview Unselected : "+ position + " size : "+selected.size());
                }
                else
                {
                    view.setBackgroundColor(Color.GRAY);
                    selected.add(ccInfo);
                    Log.d(TAG, "Listview Selected : "+ position + " size : "+selected.size());
                }

            }
        });

        // Inflate the layout for this fragment

        buttonContactCall = (Button)view.findViewById(R.id.buttonContactCall);
        buttonContactCall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "selected.size() : !!!"+selected.size());
                if(selected.size() != 1)
                {
                    textViewContactResult.setText("Please select only one!");
                }
                else
                {
                    CallHandler.getInstance().callRequest(selected.get(0).getCcNumber());
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
