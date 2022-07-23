package com.htk.consumerapp;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity {
    public String Uid = "";
    int backButtonCount = 0;
    static final String TAG = "Home Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_appointments, R.id.navigation_reports, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        Intent intent = getIntent();
        Uid = intent.getStringExtra("id");
    }

    public void onImageClick(View v) {

        switch(v.getId()){

            case R.id.view1:
                startActivity(new Intent(HomeActivity.this, DoctorVisit.class).putExtra("id",Uid));
                break;

            case R.id.view2:
                startActivity(new Intent(HomeActivity.this, NursingCare.class).putExtra("id",Uid));
                break;

            case R.id.view3:
                startActivity(new Intent(HomeActivity.this, LaboratoryVisit.class).putExtra("id",Uid));
                break;

            case R.id.view4:
                startActivity(new Intent(HomeActivity.this, WellnessTreatments.class).putExtra("id",Uid));
                break;

            case R.id.view5:
                startActivity(new Intent(HomeActivity.this, OffersPackages.class).putExtra("id",Uid));
                break;

            case R.id.view6:
                startActivity(new Intent(HomeActivity.this, PhysiotherapistVisit.class).putExtra("id",Uid));
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (backButtonCount >= 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    public void printFragmentStack() {
        FragmentManager fm = getFragmentManager();
        for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
            Log.e(TAG, "Found fragment: " + fm.getBackStackEntryAt(entry).getId());
        }
    }


}
