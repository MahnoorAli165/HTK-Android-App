package com.htk.consumerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{
    TextView login;
    Button createButton;
    Intent intent;
    Spinner spinner;
    FirebaseAuth auth;
    FirebaseUser user;
    EditText nameT, emailT, passwordT, allergiesT, mobileT;
    private final String TAG = "SignupActivity";
    String name, email, password, mobile, allergies = "";
    String customer_id = "";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        login= (TextView) findViewById(R.id.loginText);
        createButton =(Button)findViewById(R.id.createAccountButtom);
        nameT = findViewById(R.id.nameText);
        emailT = findViewById(R.id.emailText);
        passwordT = findViewById(R.id.passwordText);
        allergiesT = findViewById(R.id.allergiesText);
        mobileT = findViewById(R.id.mobileText);
        progressBar = findViewById(R.id.progressBar11);

        login.setOnClickListener(this);
        createButton.setOnClickListener(this);

        spinner = findViewById(R.id.spinner);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==login.getId()){
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        if(view.getId()== createButton.getId()){
            showProgressBar();
            if (isConnectedToInternet()) {
                name = nameT.getText().toString();
                email = emailT.getText().toString();
                password = passwordT.getText().toString();
                mobile = mobileT.getText().toString();
                allergies = allergiesT.getText().toString();


                if (emailT.getText().toString().equals("") || passwordT.getText().toString().equals("") || nameT.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "Please enter all details.", Toast.LENGTH_SHORT).show();
                } else if (allergiesT.getText().toString().equals("")) {
                    Toast.makeText(SignupActivity.this, "Please enter any allergies you may have\nIf you don't have any allergy, please type none", Toast.LENGTH_SHORT).show();
                } else {
                    auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.e(TAG, "UID is: " + authResult.getUser().getUid());
                            createNewUser(authResult.getUser().getUid());
                            hideProgressBar();
                            Toast.makeText(SignupActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            ///authentication error
                            Toast.makeText(SignupActivity.this, "Error while creating account" + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Internet not found", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }

        }

    }

    public void createNewUser(final String Uid){
        final DatabaseReference countReference = FirebaseDatabase.getInstance().getReference().child("users");
        countReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                generateAppointmentID(count);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child(Uid).child("name").setValue(name);
                reference.child(Uid).child("mobile").setValue(mobile);
                reference.child(Uid).child("email").setValue(email);
                reference.child(Uid).child("allergies").setValue(allergies);
                reference.child(Uid).child("customer_id").setValue(customer_id);
                reference.child(Uid).child("appointments").child("0").setValue("none").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("check", "Child of users collection created: "+Uid);
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignupActivity.this, "Error.\n"+databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void generateAppointmentID(int count){
        if (count<10){
            count += 1;
            customer_id = "htk-uae-00"+count;
        }
        else if (count>=10 && count<100){
            count += 1;
            customer_id = "htk-uae-0"+count;
        }
        else{
            count += 1;
            customer_id = "htk-uae-"+count;
        }
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
    }


    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

}
