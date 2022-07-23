package com.htk.consumerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AppointmentFragment extends Fragment {
    public  AppointmentFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointment, container, false);

        Button doctor = (Button) rootView.findViewById(R.id.doc);
        Button lab = (Button) rootView.findViewById(R.id.lab);
        Button pysio = (Button) rootView.findViewById(R.id.pysio);
        Button nurse = (Button) rootView.findViewById(R.id.nurse);
        Button wellness = (Button) rootView.findViewById(R.id.well);
        Button offer = (Button) rootView.findViewById(R.id.offer);
        TextView doc = (TextView) rootView.findViewById(R.id.dvisit) ;
        TextView labv = (TextView) rootView.findViewById(R.id.lvisit) ;
        TextView nursev = (TextView) rootView.findViewById(R.id.nvisit) ;
        TextView physiov = (TextView) rootView.findViewById(R.id.pvisit) ;
        TextView wellv = (TextView) rootView.findViewById(R.id.wvisit) ;
        TextView offerv = (TextView) rootView.findViewById(R.id.ovisit) ;

//        doctor.setOnClickListener(this);
//        lab.setOnClickListener(this);
//        pysio.setOnClickListener(this);
//        nurse.setOnClickListener(this);
//        wellness.setOnClickListener(this);
//        offer.setOnClickListener(this)

        doctor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), DoctorVisit.class);
                startActivity(myIntent);


            }
        });
        doc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), DoctorVisit.class);
                startActivity(myIntent);


            }
        });
        lab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), LaboratoryVisit.class);
                startActivity(myIntent);
            }
        });
        labv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), LaboratoryVisit.class);
                startActivity(myIntent);
            }
        });
        pysio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), PhysiotherapistVisit.class);
                startActivity(myIntent);
            }
        });
        physiov.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), PhysiotherapistVisit.class);
                startActivity(myIntent);
            }
        });
        nurse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), NursingCare.class);
                startActivity(myIntent);
            }
        });
        nursev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), NursingCare.class);
                startActivity(myIntent);
            }
        });
        wellness.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), WellnessTreatments.class);
                startActivity(myIntent);
            }
        });
        wellv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), WellnessTreatments.class);
                startActivity(myIntent);
            }
        });
        offer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), OffersPackages.class);
                startActivity(myIntent);
            }
        });
        offerv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(), OffersPackages.class);
                startActivity(myIntent);
            }
        });





        return rootView;
    }



//        return rootView;
//    }
//
//    @Override
//    public void onClick(View view) {
//        Fragment selectedFragment = null;
//
//        switch (view.getId()) {
//            case R.id.doc:
//                Doctor_Fragment doctor_fragment = new Doctor_Fragment();
//                FragmentTransaction transaction=getFragmentManager().beginTransaction();
//                transaction.replace(R.id.appointment_fragment,doctor_fragment);
//                transaction.commit();
//                break;
//            case R.id.nav_appointment:
//                selectedFragment = new AppointmentFragment();
//                break;
//            case R.id.nav_account:
//                selectedFragment = new AccountFragment();
//                break;
//        }
//
//    };
}

