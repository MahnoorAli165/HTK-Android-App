package com.htk.consumerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;

public class OffersPackages extends AppCompatActivity {

    private RadioGroup radioGroup;
    private CheckBox receiveEmail;
    private Button takeOfferButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_packages);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initializViews();
    }

    public void initializViews(){
        radioGroup = findViewById(R.id.offersRadioGroup);
        receiveEmail = findViewById(R.id.offersCheckbox);
        takeOfferButton = findViewById(R.id.offersButton);
    }

//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(OffersPackages.this, HomeActivity.class));
//    }
}
