package com.htk.consumerapp.ui.appointments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import static androidx.appcompat.app.AlertDialog.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.htk.consumerapp.AdapterAppointments;
import com.htk.consumerapp.HomeActivity;
import com.htk.consumerapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AppointmentsFragment extends Fragment {

    private AppointmentsViewModel appointmentsViewModel;
    private ListView list;
    private ArrayList<String> categories = new ArrayList<String>();
    private ArrayList<String> types = new ArrayList<String>();
    private ArrayList<String> dates = new ArrayList<String>();
    private ArrayList<String> times = new ArrayList<String>();
    private String category, type, date, time, Uid = "";
    private int appointments_count = 0;
    private final String TAG = "Appointments Fragment";
    private static ProgressBar progressBar;
    ArrayList<String> appointments = new ArrayList<String>();

    ImageView notFoundImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        new HomeActivity().printFragmentStack();

        appointmentsViewModel =
                ViewModelProviders.of(this).get(AppointmentsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_appointments, container, false);
        list = (ListView) root.findViewById(R.id.appointmentsListView);
        progressBar = root.findViewById(R.id.progressBar3);
        notFoundImage = root.findViewById(R.id.not_found_imageA);
        showProgressBar();

        Uid = ((HomeActivity) getActivity()).Uid;
//        String[] titles = {"Haemoglobin", "Blood Test", "Liver Function", "Haemoglobin", "Blood Test", "Liver Function", "Haemoglobin", "Blood Test", "Liver Function", "Haemoglobin", "Blood Test", "Liver Function", "Haemoglobin", "Blood Test", "Liver Function", ""};
//        String[] dates = {"01-01-2020", "03-01-2020", "09-01-2020", "12-01-2020", "15-01-2020", "18-01-2020", "21-01-2020", "24-01-2020", "27-01-2020", "28-01-2020", "29-01-2020", "01-02-2020","02-01-2020", "03-01-2020", "04-02-2020", ""};

        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(Uid).child("appointments");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showProgressBar();
                    appointments_count = (int) dataSnapshot.getChildrenCount();
                    Log.e(TAG, "Appointments Count: " + appointments_count);
                    if (dataSnapshot.getChildrenCount() > 0) {
                        hideProgressBar();
                        if (appointments_count == 1) {
                            Builder builder = new Builder(getActivity());
                            builder.setTitle("No appointments found");
                            builder.setMessage("All appointments booked by you, will be shown here");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    notFoundImage.setVisibility(View.VISIBLE);
                                }
                            });
                            builder.show();
                        } else {
                            for (int i = 1; i < appointments_count; i++) {
                                appointments.add(dataSnapshot.child(String.valueOf(i)).getValue(String.class));
                            }
                            getAppointmentsData();
//                        AppointmentsListAdapter adapter = new AppointmentsListAdapter(getActivity(), categories, types, dates, times);
//                        list.setAdapter(adapter);
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }


        return root;
    }

    public void getAppointmentsData() {
        Log.e(TAG, "All appointments: " + appointments.toString());
        try {
            for (int i = 0; i < appointments.size(); i++) {
                populateArrays(appointments.get(i));
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "All records in Array List: " + categories.toString());
                    categories.add("null");
                    types.add("");
                    dates.add("");
                    times.add("");
                    try {
                        AdapterAppointments adapter = new AdapterAppointments(getActivity(), categories, types, dates, times);
                        list.setAdapter(adapter);
                        hideProgressBar();
                    } catch (Exception e) {
//                        Toast.makeText(getActivity(), "Unknown error", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error: " + e);
                    }
                }
            }, 3000);
            hideProgressBar();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }


    }

    public void populateArrays(String appointment) {
        Log.e(TAG, "Inside populateArrays()");
        final String typeA = appointment.substring(25, 28);//type
        final String key = appointment.substring(0, 20);
        Log.e(TAG, "Appointment type: " + typeA);
        Log.e(TAG, "Appointment key: " + key);

        switch (typeA) {
            case "lab": {
                Log.e(TAG, "Inside Lab case");
                DatabaseReference referenceA = FirebaseDatabase.getInstance().getReference().child("appointments").child("laboratory_oncall").child(key);
                referenceA.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            category = "Laboratory on call";
                            type = dataSnapshot.child("test").getValue(String.class);
                            date = dataSnapshot.child("appointment_date").getValue(String.class);
                            time = dataSnapshot.child("appointment_time").getValue(String.class);
                            Log.e(TAG, "Data recieved: " + category + "," + type + "," + date + "," + time);
                            categories.add(category);
                            types.add(type);
                            dates.add(date);
                            times.add(time);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;

            }
            case "doc": {
                Log.e(TAG, "Inside Doc case");
                DatabaseReference referenceD = FirebaseDatabase.getInstance().getReference().child("appointments").child("doctor_visit").child(key);
                referenceD.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e(TAG, "dataSnapshot count: " + dataSnapshot.getChildrenCount());
                        if (dataSnapshot.getChildrenCount() > 0) {
                            category = "Doctor visit";
                            type = dataSnapshot.child("symptom").getValue(String.class);
                            date = dataSnapshot.child("appointment_date").getValue(String.class);
                            time = dataSnapshot.child("appointment_time").getValue(String.class);
                            Log.e(TAG, "Data recieved: " + category + "," + type + "," + date + "," + time);
                            categories.add(category);
                            types.add(type);
                            dates.add(date);
                            times.add(time);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            }

            case "nur": {
                Log.e(TAG, "Inside Nur case");
                DatabaseReference referenceN = FirebaseDatabase.getInstance().getReference().child("appointments").child("nursing_care").child(key);
                referenceN.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            category = "Nursing Care";
                            type = dataSnapshot.child("nursing_type").getValue(String.class);
                            date = dataSnapshot.child("appointment_date").getValue(String.class);
                            time = dataSnapshot.child("appointment_time").getValue(String.class);
                            Log.e(TAG, "Data recieved: " + category + "," + type + "," + date + "," + time);
                            categories.add(category);
                            types.add(type);
                            dates.add(date);
                            times.add(time);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;

            }
            case "phy": {
                Log.e(TAG, "Inside Phy case");
                DatabaseReference referenceP = FirebaseDatabase.getInstance().getReference().child("appointments").child("physio_visit").child(key);
                referenceP.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            category = "Physiotherapist Visit";
                            type = dataSnapshot.child("symptom").getValue(String.class);
                            date = dataSnapshot.child("appointment_date").getValue(String.class);
                            time = dataSnapshot.child("appointment_time").getValue(String.class);
                            Log.e(TAG, "Data recieved: " + category + "," + type + "," + date + "," + time);
                            categories.add(category);
                            types.add(type);
                            dates.add(date);
                            times.add(time);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            }

            case "wel": {
                Log.e(TAG, "Inside Wel case");
                DatabaseReference referenceW = FirebaseDatabase.getInstance().getReference().child("appointments").child("wellness_treatments").child(key);
                referenceW.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            category = "Wellness Treatments";
                            type = dataSnapshot.child("treatment_type").getValue(String.class);
                            date = dataSnapshot.child("appointment_date").getValue(String.class);
                            time = dataSnapshot.child("appointment_time").getValue(String.class);
                            Log.e(TAG, "Data recieved: " + category + "," + type + "," + date + "," + time);
                            categories.add(category);
                            types.add(type);
                            dates.add(date);
                            times.add(time);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
            }
        }
    }

    public static void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }
}