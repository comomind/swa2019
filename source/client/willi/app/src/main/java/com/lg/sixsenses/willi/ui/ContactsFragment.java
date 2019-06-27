package com.lg.sixsenses.willi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.logic.callmanager.CallHandler;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    Button AddButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        ListView listView = (ListView) view.findViewById(R.id.contact_list);
        ContactsAdapter adapter = new ContactsAdapter(getActivity());
        listView.setAdapter(adapter);

        AddButton = (Button)view.findViewById(R.id.AddButton);
        AddButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent(getActivity(),ContactModifyActivity.class);
               // Intent intent = new Intent(getActivity(),RegisterActivity.class);
                Intent intent = new Intent(getActivity(),ContactAddActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
