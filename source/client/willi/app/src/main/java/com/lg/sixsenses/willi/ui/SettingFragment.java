package com.lg.sixsenses.willi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.logic.callmanager.CallHandler;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.RegisterInfo;
import com.lg.sixsenses.willi.repository.UpdatedData;
import com.lg.sixsenses.willi.logic.servercommmanager.RestManager;
import com.lg.sixsenses.willi.repository.UserInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    public static final String TAG = SettingFragment.class.getName().toString();
    private Spinner spinnerAudioOutput;
    private Spinner spinnerSound;
    private Button buttonSave;
    private Button myInfobutton;
    private Button logoutbutton;
    UserInfo userInfo = new UserInfo();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Audio Output Setting
        spinnerAudioOutput = (Spinner)view.findViewById(R.id.spinnerAudioOutput);
        String[] items = new String[]{  DataManager.AudioOutput.BLUETOOTH.toString(),
                                        DataManager.AudioOutput.SPEAKER.toString(),
                                        DataManager.AudioOutput.EARPIECE.toString()};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,items);
        spinnerAudioOutput.setAdapter(adapter);

        DataManager.AudioOutput ao = DataManager.getInstance().getAudioOutput();

        int index = 0;
        if(ao == DataManager.AudioOutput.BLUETOOTH) index = 0;
        else if (ao == DataManager.AudioOutput.SPEAKER) index = 1;
        else if (ao == DataManager.AudioOutput.EARPIECE) index = 2;
        spinnerAudioOutput.setSelection(index);

        // Sounf Setting
        spinnerSound = (Spinner)view.findViewById(R.id.spinnerSound);
        String[] itemsSound = new String[]{  DataManager.Sound.BELL.toString(),
                DataManager.Sound.VIBRATE.toString(),
                DataManager.Sound.MUTE.toString()};
        ArrayAdapter<String> adapterSound = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,itemsSound);
        spinnerSound.setAdapter(adapterSound);

        DataManager.Sound so = DataManager.getInstance().getSound();

        int indexSound = 0;
        if(so == DataManager.Sound.BELL) indexSound = 0;
        else if (so == DataManager.Sound.VIBRATE) indexSound = 1;
        else if (so == DataManager.Sound.MUTE) indexSound = 2;
        spinnerSound.setSelection(indexSound);

        myInfobutton = (Button)view.findViewById(R.id.myInfobutton);
        myInfobutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MyInfoActivity.class);
                startActivity(intent);
            }
        });

        logoutbutton = (Button)view.findViewById(R.id.logoutbutton);
        logoutbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "logoutclic!!!!");
                Intent intent = new Intent(getActivity(),LogoutActivity.class);
                startActivity(intent);

                RestManager rest = new RestManager();
                rest.sendLogout(userInfo);
            }

        });

        buttonSave = (Button)view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                DataManager.AudioOutput selected = DataManager.AudioOutput.values()[spinnerAudioOutput.getSelectedItemPosition()];
                if(selected != DataManager.getInstance().getAudioOutput()) {
                    DataManager.getInstance().setAudioOutput(selected);
                    UpdatedData data = new UpdatedData();
                    data.setType("AudioOutput");
                    data.setData(selected);
                    DataManager.getInstance().NotifyUpdate(data);
                }

                DataManager.Sound selectedSound = DataManager.Sound.values()[spinnerSound.getSelectedItemPosition()];
                if(selectedSound != DataManager.getInstance().getSound()) {
                    DataManager.getInstance().setSound(selectedSound);
                    UpdatedData data = new UpdatedData();
                    data.setType("Sound");
                    data.setData(selectedSound);
                    DataManager.getInstance().NotifyUpdate(data);
                }
            }
        });

        return view;
    }
}
