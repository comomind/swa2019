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
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UserInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConferenceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConferenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConferenceFragment extends Fragment {
    public static final String TAG = ConferenceFragment.class.getName().toString();
    Button buttonCdCall;
    TextView textViewContactResult;

    ArrayList<CcInfo> selected;
    CCAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conference, container, false);

        ListView listView = (ListView) view.findViewById(R.id.cc_xml_list);
        adapter = new CCAdapter(getActivity());
        listView.setAdapter(adapter);
        selected = new ArrayList<CcInfo>();

        // Inflate the layout for this fragment

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CcInfo ccInfo =(CcInfo) adapter.getItem(position);
                if(selected.contains(ccInfo))
                {
                    view.setBackgroundColor(Color.WHITE);
                    selected.remove(ccInfo);

                }
                else
                {
                    view.setBackgroundColor(Color.GRAY);
                    selected.add(ccInfo);

                }

            }

        });

        buttonCdCall = (Button)view.findViewById(R.id.cccall);
        buttonCdCall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(selected.size() != 1)
                {
                    textViewContactResult.setText("Please select only one!");
                }
                else
                {

                    Intent intent = new Intent(getActivity(),CcActivity.class);
                    intent.putExtra("ccNumber", selected.get(0).getCcNumber());
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
