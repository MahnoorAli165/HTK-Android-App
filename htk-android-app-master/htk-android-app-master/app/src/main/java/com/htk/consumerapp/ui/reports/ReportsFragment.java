package com.htk.consumerapp.ui.reports;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.htk.consumerapp.AdapterReports;
import com.htk.consumerapp.HomeActivity;
import com.htk.consumerapp.LoginActivity;
import com.htk.consumerapp.R;
import com.htk.consumerapp.SignupActivity;

import java.io.File;
import java.util.ArrayList;

public class ReportsFragment extends Fragment {

    private ListView list;
    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<String> dates = new ArrayList<String>();
    private ArrayList<String> appointments = new ArrayList<String>();
    ArrayList<String> reports = new ArrayList<String>();
    private ProgressBar progressBar;
    private String Uid, username, title, date, appointment;
    private final String TAG = "Reports Fragment";
    private int reports_count, numberOfExistingReports;
    int i = 1;
    Uri uri;

    ImageView notFoundImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_reports, container, false);
        list = (ListView)root.findViewById(R.id.reportsListView);
        progressBar = root.findViewById(R.id.progressBarReports);
        notFoundImage = root.findViewById(R.id.not_found_imageR);

        showProgressBar();
        Uid = ((HomeActivity)getActivity()).Uid;
        Log.e(TAG, "Uid is: " + Uid);
        getUsername();

        new HomeActivity().printFragmentStack();

        return root;
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void getUsername() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("name").getValue(String.class);
                Log.e(TAG, "User is: " + username);
                checkReportsAvailability();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkReportsAvailability() {
        try{
            Log.e(TAG, "Inside Try block");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("reports").child(username);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showProgressBar();
                    reports_count = (int) dataSnapshot.getChildrenCount();
                    Log.e(TAG, "Total " + reports_count + " reports found  in database");
                    if (dataSnapshot.getChildrenCount()>0){
                        hideProgressBar();

                        for (int i = 0; i < reports_count; i++) {
                            reports.add(dataSnapshot.child(String.valueOf(i)).child("filename").getValue(String.class));
                        }
                        getReportsData();


                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("No reports found");
                        builder.setMessage("All reports shared by you, will be shown here");
                        hideProgressBar();
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notFoundImage.setVisibility(View.VISIBLE);
                                hideProgressBar();
                            }
                        });
                        builder.show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void populateArrays() {

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("reports").child(username);
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberOfExistingReports = (int) dataSnapshot.getChildrenCount();
                Log.e(TAG, "Total reports: " + numberOfExistingReports);

                title = dataSnapshot.child(String.valueOf(i)).child("filename").getValue(String.class);
                appointment = dataSnapshot.child(String.valueOf(i)).child("appointment").getValue(String.class);
                date = dataSnapshot.child(String.valueOf(i)).child("date").getValue(String.class);
                titles.add(title);
                appointments.add(appointment);
                dates.add(date);
                i++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getReportsData() {
        Log.e(TAG, "All reports: " + reports.toString());
        try{
            Log.e(TAG, "Reports.size() is: " + reports.size());
            for (int i = 0; i < reports.size(); i++) {
                populateArrays();
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "All records in Array List: " + titles.toString());
                    titles.add("");
                    dates.add("");
                    appointments.add("");
                    try {
                        AdapterReports adapter = new AdapterReports(getActivity(), titles, dates, appointments);
                        list.setAdapter(adapter);
                    }catch (Exception e){
//                        Toast.makeText(getActivity(), "Unknown error", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error: "+e);
                    }
                }
            }, 3000);
            hideProgressBar();
//            downloadFile();
        }
        catch(Exception e){
            Log.e(TAG, e.getMessage());
        }


    }


}
