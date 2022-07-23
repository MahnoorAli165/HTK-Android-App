package com.htk.consumerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NursingCare extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerDragListener {

    private EditText appointmentDateText, appointmentTimeText, conditionText, specialNoteText, searchText;
    ;
    private String appointment_date, appointment_time, checkedRadio, condition, specialnote, customer_id, appointment_id = "";
    private RadioGroup radioGroup;
    private CheckBox receiveEmails;
    private Button addButton, locationButton, confirmLocationButton;
    private boolean checked = false;
    private final String TAG = "Nursing Care";
    private ProgressBar progressBar;
    private String Uid, new_appointment_id = "";
    private String line = "\n"+"\n-----------------------------\n";
    private int childrenCount = 0, appointments_count = 0;
    private MailService.RequestAsync request;

    //file attachment
    final static int PICK_PDF_CODE = 2342;
    ProgressBar progressBarUpload;
    private Button attachButton;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    Intent dataIntent = null;
    private String filename = "";
    private TextView filenameLabel;
    private Uri fileUri;
    private String username;
    private int numberOfExistingReports;

    //location
    private GoogleMap mGoogleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private SupportMapFragment mapFragment;
    private Location mLastLocation;
    private Marker currentUserLocationMarker;
    private FusedLocationProviderClient mFusedLocationClient;
    private double latitude, longitude = 0;
    private boolean isMapDisplayed = false;
    private static final int Request_User_Location_Code = 99;
    private int ProximityRadius = 10000;
    private String id, locationString = "";
    private View rel;
    private int count = 0;
    private LocationManager locationManager;
    private ImageView searchIcon;
    private TextView locationLabel;
    private ImageView center_pin;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursing_care);

        disableAutoLoadingOfKeyboard();
        initializViews();
        setListeners();

        customer_id = "htk-uae-001";
        Intent intent = getIntent();
        Uid = intent.getStringExtra("id");

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        getUsername();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapN);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        searchText.setText(extras.getString("loc"));

        Log.d(TAG, line + TAG + "Activity started" + line);

        displayGoogleMap(0);
    }

    public void initializViews(){
        appointmentDateText = findViewById(R.id.appointmentDateN);
        appointmentTimeText = findViewById(R.id.appointmentTimeN);
        conditionText = findViewById(R.id.nursingConditionText);
        specialNoteText = findViewById(R.id.nursingSpecialNote);
        radioGroup = findViewById(R.id.nursingRadiogroup);
        receiveEmails = findViewById(R.id.nursingCheckbox);
        addButton = findViewById(R.id.nursingButton);
        progressBar = findViewById(R.id.progressBar6);

        progressBarUpload = findViewById(R.id.progressbarUploadN);
        attachButton = findViewById(R.id.attachBN);
        filenameLabel = findViewById(R.id.filenameN);

        layout = findViewById(R.id.nursingLayout);
        locationLabel = findViewById(R.id.locationLabelN);
        locationButton = findViewById(R.id.locationButtonN);
        confirmLocationButton = findViewById(R.id.confirmLocationButtonN);
        searchText = findViewById(R.id.searchTextN);
        searchIcon = findViewById(R.id.searchIconN);
        center_pin = findViewById(R.id.center_pinN);
    }

    public void disableAutoLoadingOfKeyboard(){
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    public void setListeners(){
        appointmentDateText.setOnClickListener(this);
        appointmentTimeText.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
        addButton.setOnClickListener(this);
        layout.setOnClickListener(this);
        locationButton.setOnClickListener(this);
        confirmLocationButton.setOnClickListener(this);
        searchIcon.setOnClickListener(this);
        attachButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final Calendar myCalendar = Calendar.getInstance();

        if (view.getId() == layout.getId()) {
            if (isMapDisplayed) {
                displayGoogleMap(0);
            }
        }

        if (view.getId() == appointmentDateText.getId()){
            if (isMapDisplayed) {
                displayGoogleMap(0);
            } else {
                final DatePickerDialog.OnDateSetListener appointment_date_listener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                        appointment_date = sdf.format(myCalendar.getTime());
                        appointmentDateText.setText(appointment_date);
                    }

                };
                new DatePickerDialog(NursingCare.this, appointment_date_listener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        }

        else if (view.getId() == appointmentTimeText.getId()){
            if (isMapDisplayed) {
                displayGoogleMap(0);
            } else {
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(NursingCare.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        appointment_time = selectedHour + ":" + selectedMinute;
                        appointmentTimeText.setText(appointment_time);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        }

        else if(view.getId() == addButton.getId()){
            Log.d(TAG, "Button clicked");

            if (isConnectedToInternet()) {
                if (isMapDisplayed) {
                    displayGoogleMap(0);
                } else {
                    condition = conditionText.getText().toString();
                    specialnote = specialNoteText.getText().toString();
                    if (receiveEmails.isChecked()) {
                        checked = true;
                    }

                    if (radioGroup.getCheckedRadioButtonId() == -1 && appointmentDateText.getText().toString().equals("") && appointmentTimeText.getText().toString().equals("") && conditionText.getText().toString().equals("")) {
                        Toast.makeText(this, "You need to enter details to book appointment", Toast.LENGTH_SHORT).show();
                    } else {
                        if (radioGroup.getCheckedRadioButtonId() == -1) {
                            Toast.makeText(this, "Please select nursing type", Toast.LENGTH_SHORT).show();
                        } else if (appointmentDateText.getText().toString().equals("")) {
                            Toast.makeText(this, "Please choose appointment date", Toast.LENGTH_SHORT).show();
                        } else if (appointmentTimeText.getText().toString().equals("")) {
                            Toast.makeText(this, "Please choose appointment time", Toast.LENGTH_SHORT).show();
                        } else if (conditionText.getText().toString().equals("")) {
                            Toast.makeText(this, "Please describe your condition", Toast.LENGTH_SHORT).show();
                        } else if (locationString.equals("")) {
                            Toast.makeText(this, "Please set appointment location", Toast.LENGTH_SHORT).show();
                        } else {
//                    Toast.makeText(this, "Nursing type: "+checkedRadio+"\nCondition: "+condition+"\nDate: "+appointment_date+"\nTime: "+appointment_time+"\nSpecial note: "+specialnote+"\nReceive Email: "+checked, Toast.LENGTH_SHORT).show();
                            getAppointmentCount();
                        }
                    }
                }
            } else {
                Toast.makeText(this, "No Internet found", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == attachButton.getId()) {
            getPDF();
        } else if (view.getId() == locationButton.getId()) {
            Log.d(TAG, line + "Location button pressed" + line);
            if (checkLocation()) {
                displayGoogleMap(1);
                Log.d(TAG, line + "Check Location: true" + line);
            } else {
                Log.d(TAG, line + "Check Location: false" + line);
                askForGPS();
            }
        } else if (view.getId() == confirmLocationButton.getId()) {
            Log.d(TAG, line + "Confirm Location button pressed" + line);
            confirmLocationButton.setVisibility(View.INVISIBLE);
            displayGoogleMap(0);
        } else if (view.getId() == searchIcon.getId()) {
            String address = searchText.getText().toString();

            List<Address> addressList = null;
            MarkerOptions userMarkerOptions = new MarkerOptions();

            if (!TextUtils.isEmpty(address)) {
                Geocoder geocoder = new Geocoder(this);

                try {
                    addressList = geocoder.getFromLocationName(address, 6);

                    if (addressList != null) {
                        for (int i = 0; i < addressList.size(); i++) {
                            Address userAddress = addressList.get(i);
                            LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());

                            userMarkerOptions.position(latLng);
                            userMarkerOptions.title(address);
                            userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                            mGoogleMap.addMarker(userMarkerOptions);
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                        }
                    } else {
                        Toast.makeText(this, "Location not found...", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                searchText.setError("This field is required!!!");
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(i);
        boolean isChecked = checkedRadioButton.isChecked();
        if (isChecked) {
            checkedRadio = checkedRadioButton.getText().toString();
        }
    }

    public void bookAppointment(){
        Log.d(TAG, "bookAppointment() called");


        if (dataIntent != null) {
            uploadFile(dataIntent.getData());
        }


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("appointments").child("nursing_care").push();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child("appointment_date").setValue(appointment_date);
                reference.child("appointment_time").setValue(appointment_time);
                reference.child("customer_id").setValue(Uid);
                new_appointment_id = reference.getKey() + " " + appointment_id;
                reference.child("appointment_id").setValue(new_appointment_id);
                reference.child("get_email").setValue(checked);
                reference.child("special_note").setValue(specialnote);
                reference.child("nursing_type").setValue(checkedRadio);
                reference.child("location").setValue(locationString);
                reference.child("need_description").setValue(condition).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideProgressBar();
                        Toast.makeText(NursingCare.this, "Appointment booked", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, line+"Appointment booked\nID: "+appointment_id+"\nDate: "+appointment_date+"\nTime: "+appointment_time+line);
                        addAppointmentToParticularUser();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NursingCare.this, "Error.\n"+databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAppointmentCount(){
        final DatabaseReference countReference = FirebaseDatabase.getInstance().getReference().child("appointments").child("nursing_care");
        countReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childrenCount = (int) dataSnapshot.getChildrenCount();
                Log.e(TAG, "There are " + childrenCount + " Nursing Care appointments already");
                if (childrenCount < 10) {
                    childrenCount += 1;
                    appointment_id = "htk-nur-00" + childrenCount;
                } else if (childrenCount >= 10 && childrenCount < 100) {
                    childrenCount += 1;
                    appointment_id = "htk-nur-0" + childrenCount;
                }
                else{
                    childrenCount += 1;
                    appointment_id = "htk-nur-" + childrenCount;
                }
                Log.e(TAG, "Appointment ID: "+ appointment_id);
                showProgressBar();
                bookAppointment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addAppointmentToParticularUser(){
        countUserAppointments();
    }

    public void countUserAppointments(){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(Uid).child("appointments");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointments_count = (int) dataSnapshot.getChildrenCount();
                addAppointment();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Error adding Appointment added to user: "+databaseError);
            }
        });
    }

    public void addAppointment(){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(appointments_count == 1){
                    appointments_count = 1;
                }
                String email = dataSnapshot.child(Uid).child("email").getValue(String.class);

                reference.child(Uid).child("appointments").child(String.valueOf(appointments_count)).setValue(new_appointment_id).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Appointment added to user: "+Uid);
                    }
                });
                String subject = "HTK | " + TAG + " appointment booked";
                String body = "Dear customer,\nYour appointment has been booked.\nAppointment details:\n1. Type: " + TAG + "\n2. Date: " + appointment_date + "\n3. Time: " + appointment_time + "\n4. Customer ID: " + customer_id + "\n\nRegards,\nHTK Medical Center\nContact: 04 295 7776\nTimings: Saturday – Friday: 09:00 AM – 09:00 PM\nGround Floor, Doha Centre, Al Maktoum Street, Al Muraqqabat, Dubai";
                if (receiveEmails.isChecked()) {
                    sendEmail(email, subject, body);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Error adding Appointment added to user: "+databaseError);
            }
        });
    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void sendEmail(String email, String subject, String body){
        MailService.recipient = email;
        MailService.subject = subject;
        MailService.body = body;
        request = new MailService.RequestAsync();
        request.execute();
    }

    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        String[] mimeTypes = {"image/*", "application/pdf"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*|application/pdf")
                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Select Report file"), PICK_PDF_CODE);
    }

    private void uploadFile(final Uri uri) {
        progressBar.setVisibility(View.VISIBLE);
        final StorageReference sRef = mStorageReference.child(Constants.STORAGE_PATH_UPLOADS + filename + ".pdf");
        sRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "URI: " + fileUri.toString());
//                        Toast.makeText(WellnessTreatments.this, "File uploaded", Toast.LENGTH_SHORT).show();

                        Upload upload = new Upload(filename, sRef.getDownloadUrl().toString());
//                        Log.e(TAG, "URL: "+sRef.getDownloadUrl().toString());
//                        mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(upload);

                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                final String newReportIndex = String.valueOf(numberOfExistingReports + 1);
                                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("reports").child(username);

                                ref.child(newReportIndex).child("uri").setValue(fileUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            ref.child(newReportIndex).child("filename").setValue(filename);
                                            ref.child(newReportIndex).child("date").setValue(appointment_date);
                                            ref.child(newReportIndex).child("appointment").setValue(appointment_id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.e(TAG, "Attached file added to user database");
                                                    }

                                                }
                                            });
                                        }

                                    }
                                });
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                getReportCount();//placing this here to avoid any Null Pointer caused by its asynchronous nature
                findViewById(R.id.attachment_iconN).setVisibility(View.VISIBLE);
                attachButton.setText("File attached");
                dataIntent = data;

                Uri uri = fileUri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    filename = myFile.getName();
                }


                filenameLabel.setVisibility(View.VISIBLE);
                filenameLabel.setText(filename);

            } else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }


        }
    }

    public void getUsername() {
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    username = dataSnapshot.child("name").getValue(String.class);
                    Log.e(TAG, "User is: " + username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
//            Toast.makeText(this, "An error occured, Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void getReportCount() {

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("reports").child(username);
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberOfExistingReports = (int) dataSnapshot.getChildrenCount();
                Log.e(TAG, "Report count is: " + numberOfExistingReports);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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


    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearchText/json?");
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyAR9eq9FYfcnaY6d0tiXpFlm2oBOTt1s5k");

        Log.d(TAG, line + "url = " + googleURL.toString() + line);
        return googleURL.toString();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;
        Log.d(TAG, line + "onLocationChanged() triggered" + line);

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        } else {


            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Your current location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            currentUserLocationMarker = mGoogleMap.addMarker(markerOptions);
            Log.d(TAG, line + "onLocationChanged()\nCoordinates received: " + location.getLatitude() + ", " + location.getLongitude() + line);


            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomBy(12));

            if (googleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                Log.d(TAG, line + "Google API Client is not null" + line);
            }

        }

        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center = mGoogleMap.getCameraPosition().target;
                LatLng newPosition = null;
                if (currentUserLocationMarker != null) {
                    currentUserLocationMarker.remove();
                    currentUserLocationMarker = mGoogleMap.addMarker(new MarkerOptions().position(center).title("Desired location"));
                    newPosition = currentUserLocationMarker.getPosition();
                }
                locationString = getStringAddress(newPosition.latitude, newPosition.longitude);
                Log.d(TAG, line + locationString + line);
                locationLabel.setText("✓ Location is set");
                locationButton.setText("Update location");
                locationLabel.setTextColor(getResources().getColor(R.color.colorBrightGreen));


            }
        });
    }

    public String getStringAddress(Double lat, Double lng) {
        String address = "";
        String city = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
        } catch (IOException e) {
            Log.d(TAG, line + e.getMessage() + line);
        }
        return address + " " + city;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        mGoogleMap.addMarker(new MarkerOptions()
//                .position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
//                .draggable(true));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
            Log.d(TAG, line + "OnMapReady called\nGoogle Map Type: " + googleMap.getMapType() + line);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.e(TAG, line + "Inside onLocationResult()" + line);
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Log.e(TAG, line + "Locationlist size is greater than 0" + line);
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.e(TAG, line + "Location: " + location.getLatitude() + " " + location.getLongitude() + line);
                mLastLocation = location;
                if (currentUserLocationMarker != null) {
                    currentUserLocationMarker.remove();
                }


                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                markerOptions.draggable(true);
//                currentUserLocationMarker = mGoogleMap.addMarker(markerOptions);
                Log.e(TAG, line + "onLocationResult\nMarker added on coordiantes: " + location.getLatitude() + ", " + location.getLongitude() + line);

                //move map camera
//                Toast.makeText(DoctorVisit.this, "Location is: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
//                mGoogleMap.animateCamera(CameraUpdateFactory.zoomBy(12));


            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();

        Log.e(TAG, line + "onPause() triggered" + line);
        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }


    public void displayGoogleMap(int visibility) {
        if (visibility == 0) {
            isMapDisplayed = false;
            mapFragment.getView().setVisibility(View.INVISIBLE);
            center_pin.setVisibility(View.INVISIBLE);
            center_pin.setVisibility(View.INVISIBLE);
            searchText.setVisibility(View.INVISIBLE);
            searchIcon.setVisibility(View.INVISIBLE);
        } else {
            isMapDisplayed = true;
            mapFragment.getView().setVisibility(View.VISIBLE);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    center_pin.bringToFront();
                    searchText.bringToFront();
                    searchIcon.bringToFront();
                    center_pin.setVisibility(View.VISIBLE);
                    searchText.setVisibility(View.VISIBLE);
                    searchIcon.setVisibility(View.VISIBLE);
                    confirmLocationButton.setVisibility(View.VISIBLE);
                }
            }, 1200);
        }
    }

    public boolean checkLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        } else {
            return true;
        }
    }

    private void askForGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        displayGoogleMap(1);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.e(TAG, line + "onMarkerDragStart..." + marker.getPosition().latitude + "..." + marker.getPosition().longitude + line);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

        Log.e(TAG, line + "onMarkerDrag" + line);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.e(TAG, line + "onMarkerDragEnd..." + marker.getPosition().latitude + "..." + marker.getPosition().longitude + line);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(NursingCare.this, HomeActivity.class));
//    }


}
