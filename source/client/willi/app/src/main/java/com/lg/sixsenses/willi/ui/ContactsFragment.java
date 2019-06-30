package com.lg.sixsenses.willi.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.lg.sixsenses.willi.R;
import com.lg.sixsenses.willi.logic.callmanager.CallHandler;
import com.lg.sixsenses.willi.logic.servercommmanager.CCRegisterBody;
import com.lg.sixsenses.willi.logic.servercommmanager.RestManager;
import com.lg.sixsenses.willi.repository.CcInfo;
import com.lg.sixsenses.willi.repository.DataManager;
import com.lg.sixsenses.willi.repository.UserInfo;

import java.util.ArrayList;
import java.util.Date;

public class ContactsFragment extends Fragment {
    public static final String TAG = ContactsFragment.class.getName().toString();

    Button buttonContactAdd;
    Button buttonContactDel;
    Button buttonContactUpdate;
    Button buttonContactCall;
    Button buttonConferenceCall;
    Button buttonCCStart;
    TextView textViewContactResult;

    ArrayList<UserInfo> selected;
    ContactsAdapter adapter;
    int size = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        textViewContactResult = (TextView)view.findViewById(R.id.textViewContactResult);

        ListView listView = (ListView) view.findViewById(R.id.contact_list);
        adapter = new ContactsAdapter(getActivity());
        listView.setAdapter(adapter);


        selected = new ArrayList<UserInfo>();

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
                // adapter.getItem(position)의 return 값은 Object 형
                // 실제 Item의 자료형은 CustomDTO 형이기 때문에
                // 형변환을 시켜야 getResId() 메소드를 호출할 수 있습니다.
                //int imgRes = ((adapter.getItem(position)).getResId());
                Log.d(TAG, "Listview click : "+ position );
                UserInfo userInfo =(UserInfo) adapter.getItem(position);
                if(selected.contains(userInfo))
                {
                    view.setBackgroundColor(Color.WHITE);
                    selected.remove(userInfo);
                    Log.d(TAG, "Listview Unselected : "+ position + " size : "+selected.size());

                }
                else
                {
                    view.setBackgroundColor(Color.GRAY);
                    selected.add(userInfo);
                    Log.d(TAG, "Listview Selected : "+ position + " size : "+selected.size());

                }

                textViewContactResult.setText("");

                // new Intent(현재 Activity의 Context, 시작할 Activity 클래스)
//                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//                // putExtra(key, value)
//                intent.putExtra("imgRes", imgRes);
//
//                startActivity(intent);
            }
        });

        buttonContactAdd = (Button)view.findViewById(R.id.buttonContactAdd);
        buttonContactAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactAddActivity.class);
                startActivity(intent);

                getActivity().finish();
            }
        });

        buttonContactUpdate = (Button)view.findViewById(R.id.buttonContactUpdate);
        buttonContactUpdate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if(selected.size() != 1)
                {
                    textViewContactResult.setText("Please select only one!");
                }
                else {
                    Intent intent = new Intent(getActivity(), ContactModifyActivity.class);
                    intent.putExtra("Email", selected.get(0).getEmail());
                    startActivity(intent);

                    getActivity().finish();
                }
            }
        });


        buttonContactCall= (Button)view.findViewById(R.id.buttonContactCall);
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

        buttonContactDel = (Button)view.findViewById(R.id.buttonContactDel);
        buttonContactDel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(selected.size() != 1)
                {
                    textViewContactResult.setText("Please select only one!");
                }
                else {
                    Intent intent = new Intent(getActivity(), ContactDelActivity.class);
                    intent.putExtra("Email", selected.get(0).getEmail());
                    intent.putExtra("Name",selected.get(0).getName());
                    startActivity(intent);

                    getActivity().finish();

                }
            }
        });


        buttonConferenceCall = (Button)view.findViewById(R.id.buttonConferenceCall);
        buttonConferenceCall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(selected.size() < 2 || selected.size() > 3)
                {
                    textViewContactResult.setText("Please select 2 or 3 people!");
                }
                else
                {
                    // for test
                    ArrayList<String> emailList = new ArrayList<String>();
                    Intent intent = new Intent(getActivity(), ContactConferenceCallActivity.class);
                    for(UserInfo info : selected)
                    {
                        intent.putExtra("Email"+size, selected.get(size).getEmail());
                        size += 1;
                     //   emailList.add(info.getEmail());
                    }
                    startActivity(intent);

                    // For test

                    /*
                    CCRegisterBody ccRegisterBody = new CCRegisterBody();
                    Date date = new Date();
                    date.setTime(System.currentTimeMillis());
                    ccRegisterBody.setStartDate(date);
                    ccRegisterBody.setDuration(60);
                    ArrayList<String> emailList = new ArrayList<String>();
                    for(UserInfo info : selected)
                    {
                        emailList.add(info.getEmail());
                    }
                    ccRegisterBody.setaList(emailList);

                    RestManager rest = new RestManager();
                    rest.sendCCRegister(ccRegisterBody);
                    */

                }
            }
        });

        // TEST CODE
        buttonCCStart = (Button)view.findViewById(R.id.buttonCCStart);
        buttonCCStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Test Code
                //
                Intent intent = new Intent(getActivity(),CcActivity.class);
                startActivity(intent);
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

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
////        if (isVisibleToUser) {
//            adapter.notifyDataSetChanged();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                getFragmentManager().beginTransaction().detach(this).commitNow();
//                getFragmentManager().beginTransaction().attach(this).commitNow();
//            } else {
//                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
//            }
//            Log.d("IsRefresh", "Yes");
// //       }
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        if (Build.VERSION.SDK_INT >= 26) {
//            ft.setReorderingAllowed(false);
//        }
//        ft.detach(this).attach(this).commit();
//    }
}
