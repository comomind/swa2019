package com.lg.sixsenses.willi.UserInterface;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lg.sixsenses.willi.Logic.CallManager.CallHandler;
import com.lg.sixsenses.willi.R;

import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lg.sixsenses.willi.DataRepository.ConstantsWilli;
import com.lg.sixsenses.willi.DataRepository.LoginInfo;
import com.lg.sixsenses.willi.Logic.CallManager.CallHandler;
import com.lg.sixsenses.willi.Logic.CallManager.CallStateMachine;
import com.lg.sixsenses.willi.Logic.ServerCommManager.RestManager;
import com.lg.sixsenses.willi.DataRepository.DataManager;
import com.lg.sixsenses.willi.Logic.ServerCommManager.TcpRecvCallManager;
import com.lg.sixsenses.willi.Logic.ServerCommManager.TcpSendCallManager;
import com.lg.sixsenses.willi.R;
//import com.lg.sixsenses.willi.DataRepository.RegisterInfo;
import com.lg.sixsenses.willi.DataRepository.UpdatedData;
import com.lg.sixsenses.willi.DataRepository.UserInfo;
import com.lg.sixsenses.willi.Util;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link dialpadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link dialpadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dialpadFragment extends Fragment {

    String data = "";
    TextView InputNum;
    ImageButton CallButton;

    ImageButton dial1;
    ImageButton dial2;
    ImageButton dial3;
    ImageButton dial4;
    ImageButton dial5;
    ImageButton dial6;
    ImageButton dial7;
    ImageButton dial8;
    ImageButton dial9;
    ImageButton dial0;
    ImageButton dialstar;
    ImageButton dialshap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialpad, container, false);

        InputNum = (TextView)view.findViewById(R.id.InputNum);
        CallButton = (ImageButton)view.findViewById(R.id.CallButton);
        dial1 = (ImageButton)view.findViewById(R.id.dial1);
        dial2 = (ImageButton)view.findViewById(R.id.dial2);
        dial3 = (ImageButton)view.findViewById(R.id.dial3);
        dial4 = (ImageButton)view.findViewById(R.id.dial4);
        dial5 = (ImageButton)view.findViewById(R.id.dial5);
        dial6 = (ImageButton)view.findViewById(R.id.dial6);
        dial7 = (ImageButton)view.findViewById(R.id.dial7);
        dial8 = (ImageButton)view.findViewById(R.id.dial8);
        dial9 = (ImageButton)view.findViewById(R.id.dial9);
        dial0 = (ImageButton)view.findViewById(R.id.dial0);
        dialstar = (ImageButton)view.findViewById(R.id.dialstar);
        dialshap = (ImageButton)view.findViewById(R.id.dialshap);


        CallButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                CallHandler.getInstance().callRequest("1001");
                Intent intent = new Intent(getActivity(),CallStateActivity.class);
                startActivity(intent);
                data = "";
            }
        });

        dial1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "1";
                InputNum.setText(data);
            }
        });

        dial2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "2";
                InputNum.setText(data);
            }
        });

        dial3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "3";
                InputNum.setText(data);
            }
        });

        dial4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "4";
                InputNum.setText(data);
            }
        });

        dial5.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "5";
                InputNum.setText(data);
            }
        });

        dial6.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "6";
                InputNum.setText(data);
            }
        });

        dial7.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "7";
                InputNum.setText(data);
            }
        });

        dial8.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "8";
                InputNum.setText(data);
            }
        });

        dial9.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "9";
                InputNum.setText(data);
            }
        });

        dial0.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "0";
                InputNum.setText(data);
            }
        });

        dialstar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "*";
                InputNum.setText(data);
            }
        });

        dialshap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                data += "#";
                InputNum.setText(data);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
