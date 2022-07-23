package com.htk.consumerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Button loginButton, fbLoginButton;
    TextView forgotPasswordLabel, createAccountLabel;
    EditText emailInput, passwordInput;
    FirebaseAuth auth;
    FirebaseUser user;
    String email, password, ID, customerID = "";
    private final String TAG = "LoginActivity";
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    SQLiteDatabase db;
    boolean userHasLoggedInForTheFirstTime;

    int backButtonCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        getHashKey();
//        updateUI(mAuth.getCurrentUser());
    }

    @Override
    public void onClick(View view) {

        if(view.getId()== loginButton.getId()){
            if (isConnectedToInternet()) {
                showProgressBar();
                if (emailInput.getText().toString().equalsIgnoreCase("") || passwordInput.getText().toString().equalsIgnoreCase("")) {
                    hideProgressBar();
                    Toast.makeText(this, "Please enter registered email and password", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "User pressed Email Login button");
                    email = emailInput.getText().toString();
                    password = passwordInput.getText().toString();

                    auth = FirebaseAuth.getInstance();
                    auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            ID = authResult.getUser().getUid();
                            user = authResult.getUser();
                            Log.e(TAG, "ID: " + ID);
                            SaveUser.setUserName(LoginActivity.this, ID);
                            SaveUser.setLoginType(LoginActivity.this, "email");
                            hideProgressBar();
//                            saveLoginTypeInDB("email");
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class).putExtra("id", ID));


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Login failed\nPlease recheck email/password and try again", Toast.LENGTH_SHORT).show();
                            hideProgressBar();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "No Internet found", Toast.LENGTH_SHORT).show();
            }

        }

        else if(view.getId()== createAccountLabel.getId()){
            startActivity(new Intent(this, SignupActivity.class));
        }

        else if (view.getId() == forgotPasswordLabel.getId()){
            showProgressBar();
            if (emailInput.getText().toString().equals("")){
                hideProgressBar();
                Toast.makeText(this, "Please enter your registered email first", Toast.LENGTH_SHORT).show();
            }
            else{
                email = emailInput.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            hideProgressBar();
                            Toast.makeText(LoginActivity.this, "Password reset email has been sent\nPlease check your email", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Email sent.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error: No user found for this email\nPlease create account", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Failure: "+e);
                        hideProgressBar();
                    }
                });
            }
        }
    }

    void initViews(){
        loginButton = (Button) findViewById(R.id.loginText);
        forgotPasswordLabel = (TextView) findViewById(R.id.forgotPasswordText);
        createAccountLabel = (TextView) findViewById(R.id.createAccountText);
        emailInput = findViewById(R.id.usernameText);
        passwordInput = findViewById(R.id.passwordText);
        loginButton.setOnClickListener(this);
        forgotPasswordLabel.setOnClickListener(this);
        createAccountLabel.setOnClickListener(this);
        fbLoginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgressBar();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.e(TAG, "Name: " + user.getDisplayName() + "\nEmail: "+user.getEmail() + "\nPhone: "+user.getPhoneNumber());
//                            startActivity(new Intent(LoginActivity.this, HomeActivity.class).putExtra("id",user.getUid()));
//                            saveLoginTypeInDB("facebook");
                            ID = mAuth.getUid();
                            SaveUser.setUserName(LoginActivity.this, ID);
                            SaveUser.setLoginType(LoginActivity.this, "facebook");
                            Log.e(TAG, "User id is: " + ID);
//                            if(userHasLoggedInForTheFirstTime(ID)) {
                            generateCustomerIDAndCreateUpdateUser(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhoneNumber());
//                            }
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_facebook]

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void loginWithFacebook(View view){
        Log.e(TAG, "User pressed Facebook Login button");
//        Toast.makeText(this, "Please login with email instead", Toast.LENGTH_SHORT).show();
        fbLoginButton.performClick();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
//                Toast.makeText(LoginActivity.this, "Success "+loginResult, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
    }

    public void createNewUser(final String Uid, final String name, final String email, final String phone){
        Log.e(TAG, "createNewUser() launched");
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.e(TAG, "Adding data for the first time");
                reference.child(Uid).child("name").setValue(name);
                if (dataSnapshot.child(Uid).child("mobile").getValue(String.class) == null) {// if there is no mobile number
                    reference.child(Uid).child("mobile").setValue(phone);
                }
                if (dataSnapshot.child(Uid).child("email").getValue(String.class) == null) {//if there is no email
                    reference.child(Uid).child("email").setValue(email);
                }
                if (dataSnapshot.child(Uid).child("allergies").getValue(String.class) == null) {//if there are no allergies
                    reference.child(Uid).child("allergies").setValue("");
                }
                if (dataSnapshot.child(Uid).child("appointments").child("0").getValue(String.class) == null) {//if there are no appointments
                    reference.child(Uid).child("appointments").child("0").setValue("none");
                }
                reference.child(Uid).child("customer_id").setValue(customerID).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "User created: " + Uid);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Error.\n"+databaseError, Toast.LENGTH_SHORT).show();
            }
        });
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

    public void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.htk.consumerapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    public void loginWithTwitter(View v) {
        Log.e(TAG, "User pressed Twitter Login button");
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                    // The OAuth secret can be retrieved by calling:
                                    // authResult.getCredential().getSecret().
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                }
                            });
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
        }

        mAuth
                .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
//                                Toast.makeText(LoginActivity.this, "Logged in: "+authResult.getUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                                ID = authResult.getUser().getUid();
                                Log.e(TAG, "Data recieved from Twitter: " + authResult.getUser().getDisplayName() + " " + mAuth.getCurrentUser().getEmail() + " " + mAuth.getCurrentUser().getPhoneNumber());
//                                if(userHasLoggedInForTheFirstTime(ID)) {
                                generateCustomerIDAndCreateUpdateUser(authResult.getUser().getUid(), authResult.getUser().getDisplayName(), authResult.getUser().getEmail(), authResult.getUser().getPhoneNumber());
//                                }
                                SaveUser.setUserName(LoginActivity.this, ID);
                                SaveUser.setLoginType(LoginActivity.this, "twitter");
                                updateUI(authResult.getUser());
                                // User is signed in.
                                // IdP data available in
                                // authResult.getAdditionalUserInfo().getProfile().
                                // The OAuth access token can also be retrieved:
                                // authResult.getCredential().getAccessToken().
                                // The OAuth secret can be retrieved by calling:
                                // authResult.getCredential().getSecret().
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure.
                            }
                        });
    }

    public boolean userHasLoggedInForTheFirstTime(final String Uid) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("customer_id").getValue(String.class) == null) {
                    userHasLoggedInForTheFirstTime = true;
                    Log.e(TAG, "userHasLoggedInForTheFirstTime: True");
                } else {
                    userHasLoggedInForTheFirstTime = false;
                    Log.e(TAG, "userHasLoggedInForTheFirstTime: False");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Error.\n" + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
        return userHasLoggedInForTheFirstTime;
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

    public void generateCustomerIDAndCreateUpdateUser(final String Uid, final String name, final String email, final String phone) {
        DatabaseReference referenceD = FirebaseDatabase.getInstance().getReference().child("users");
        referenceD.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount() + 1;
                Log.e(TAG, "Number of total users: " + (count - 1));
                if (dataSnapshot.getChildrenCount() > 0 && dataSnapshot.getChildrenCount() < 10) {
                    customerID = "htk-uae-00" + count;
                } else if (dataSnapshot.getChildrenCount() >= 10 && dataSnapshot.getChildrenCount() < 100) {
                    customerID = "htk-uae-0" + count;
                } else {
                    customerID = "htk-uae-" + count;
                }
                Log.e(TAG, "Next CustomerID will be: " + customerID);
                createNewUser(Uid, name, email, phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {
            Log.e(TAG, "User - " + user.getDisplayName() + ", " + user.getUid());
            startActivity(new Intent(LoginActivity.this, HomeActivity.class).putExtra("id", user.getUid()));
//            Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
        } else {

        }
    }

}
