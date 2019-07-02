package com.lg.sixsenses.willi.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
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
import com.lg.sixsenses.willi.repository.ConstantsWilli;
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
    private Spinner spinnerResolution;
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

                getActivity().finish();
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
                    SharedPreferences sp = getActivity().getSharedPreferences(ConstantsWilli.PREFERENCE_FILENAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(ConstantsWilli.PREFERENCE_KEY_AUDIOOUTPUT, selected.toString());
                    editor.commit();

                }

                DataManager.Sound selectedSound = DataManager.Sound.values()[spinnerSound.getSelectedItemPosition()];
                if(selectedSound != DataManager.getInstance().getSound()) {
                    DataManager.getInstance().setSound(selectedSound);
                    UpdatedData data = new UpdatedData();
                    data.setType("Sound");
                    data.setData(selectedSound);
                    DataManager.getInstance().NotifyUpdate(data);

                    SharedPreferences sp = getActivity().getSharedPreferences(ConstantsWilli.PREFERENCE_FILENAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(ConstantsWilli.PREFERENCE_KEY_SOUND, selectedSound.toString());
                    editor.commit();


                }

                DataManager.Resolution selectedResolution = DataManager.Resolution.values()[spinnerResolution.getSelectedItemPosition()];
                if(selectedResolution != DataManager.getInstance().getResolution()) {
                    DataManager.getInstance().setResolution(selectedResolution);
                    if(selectedResolution == DataManager.Resolution.LOW)
                    {
                        DataManager.getInstance().setCamWidth(144);
                        DataManager.getInstance().setCamHeight(176);
                        DataManager.getInstance().setComRate(15);
                        Log.d(TAG, "Resolution : LOW");
                    }
                    else if(selectedResolution == DataManager.Resolution.MID)
                    {
                        DataManager.getInstance().setCamWidth(144);
                        DataManager.getInstance().setCamHeight(176);
                        DataManager.getInstance().setComRate(20);
                        Log.d(TAG, "Resolution : MID");
                    }
                    else if(selectedResolution == DataManager.Resolution.HIGH)
                    {
                        DataManager.getInstance().setCamWidth(240);
                        DataManager.getInstance().setCamHeight(320);
                        DataManager.getInstance().setComRate(25);
                        Log.d(TAG, "Resolution : HIGH");
                    }

                    SharedPreferences sp = getActivity().getSharedPreferences(ConstantsWilli.PREFERENCE_FILENAME, Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(ConstantsWilli.PREFERENCE_KEY_RESOLUTION, selectedResolution.toString());
                    editor.commit();
//                    UpdatedData data = new UpdatedData();
//                    data.setType("Sound");
//                    data.setData(selectedSound);
//                    DataManager.getInstance().NotifyUpdate(data);
                }
            }
        });

        spinnerResolution = (Spinner)view.findViewById(R.id.spinnerResolution);
        String[] itemsResolution = new String[]{ "LOW","MID","HIGH"};
        ArrayAdapter<String> adapterResolution = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,itemsResolution);
        spinnerResolution.setAdapter(adapterResolution);


        SharedPreferences sp = getActivity().getSharedPreferences(ConstantsWilli.PREFERENCE_FILENAME, Activity.MODE_PRIVATE);
        String resolution = sp.getString(ConstantsWilli.PREFERENCE_KEY_RESOLUTION, "");
        if(resolution.equals("LOW")) DataManager.getInstance().setResolution(DataManager.Resolution.LOW);
        else if(resolution.equals("MID")) DataManager.getInstance().setResolution(DataManager.Resolution.MID);
        else if(resolution.equals("HIGH")) DataManager.getInstance().setResolution(DataManager.Resolution.HIGH);

        DataManager.Resolution re = DataManager.getInstance().getResolution();
        int indexSolution = 0;
        if(re == DataManager.Resolution.LOW) indexSolution = 0;
        else if (re == DataManager.Resolution.MID) indexSolution = 1;
        else if (re == DataManager.Resolution.HIGH) indexSolution = 2;
        spinnerResolution.setSelection(indexSolution);

        return view;
    }
}
