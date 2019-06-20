package com.lg.sixsenses.willi.UserInterface;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lg.sixsenses.willi.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link dialpadFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link dialpadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dialpadFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialpad, container, false);
    }

}
