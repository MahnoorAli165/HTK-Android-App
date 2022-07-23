package com.htk.consumerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Collections;

public class ResultFragment extends Fragment {
    public ResultFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result,container,false);
        ListView listView = view.findViewById(R.id.reportsView);
        String heading = "Serial #"+" | " + "Test Name" +" | "+"Appointment Date"+" | "+"File";

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, Collections.singletonList(heading));
        listView.setAdapter(listViewAdapter);
        return view;
    }
}
