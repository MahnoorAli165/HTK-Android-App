package com.htk.consumerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class AccountFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        ListView listView = view.findViewById(R.id.reportsView);
        String[] heading = {"Account Setting", "Logout"};

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, heading);
        listView.setAdapter(listViewAdapter);
        return view;
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
            // Getting the Container Layout of the ListView
            int itm = (int) parent.getItemAtPosition(position);
            switch (itm) {
                case 1:
                    Fragment fragment = new AccountSettings();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.account_fragment, fragment).addToBackStack(null).commit();
                    break;
                case 2:
                    Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent1);
                    break;
//
//            if(position == 0/*or any other position*/){
//                Fragment fragment = new AccountSettings();
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.account_fragment, fragment).addToBackStack(null).commit();
//            }
//            else if(position == 1){
//                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
//                startActivity(intent1);
//
//            }
            }

        }
    };
}
