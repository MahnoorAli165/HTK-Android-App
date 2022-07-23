package com.htk.consumerapp.ui.profile;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.htk.consumerapp.DatabaseHelper;
import com.htk.consumerapp.HomeActivity;
import com.htk.consumerapp.LocationActivity;
import com.htk.consumerapp.LoginActivity;
import com.htk.consumerapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.htk.consumerapp.SaveUser;
import com.htk.consumerapp.SplashActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ProfileViewModel profileViewModel;
    private final String TAG = "ProfileFragment";
    private Button changePasswordButton, logoutButton, updateLocationButton;
    private FirebaseUser user;
    private String newpassword, newaddress, newblood, name, email, newemail, address, mobile, newmobile, bloodGroup = "";
    private EditText emailT, mobileT, addressT, bloodGroupT;
    private TextView nameL;
    private ProgressBar progressBar;
    private ImageView userImage;
    String Uid = "";
    boolean loggedInWithEmail = true;
    private boolean bloodGroupProvided = false;
    public static String latitude, longitude = "";
    public static boolean shouldUpdateLocation = false;

    private int Gallary_intent = 2;
    StorageReference imagePath;
    Uri uri;
    String fileUri;
    private String line = "\n" + "\n-----------------------------\n";

    //for uploading/updating user image
    // request code
    // Uri indicates, where the image will be picked from

    FirebaseStorage storage;
    StorageReference storageReference;

//    SQLiteDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        new HomeActivity().printFragmentStack();


        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        changePasswordButton = root.findViewById(R.id.changePasswordButton);
        logoutButton = root.findViewById(R.id.logoutButton);
        emailT = root.findViewById(R.id.emailTPRO);
        mobileT = root.findViewById(R.id.mobileTPRO);
        addressT = root.findViewById(R.id.addressTPRO);
        bloodGroupT = root.findViewById(R.id.bloodTPRO);
        nameL = root.findViewById(R.id.textViewPRO);
        userImage = root.findViewById(R.id.profileImage);
        progressBar = root.findViewById(R.id.progressBar2);
        updateLocationButton = root.findViewById(R.id.updateLocationButton);

        changePasswordButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        addressT.setOnClickListener(this);
        bloodGroupT.setOnClickListener(this);
        emailT.setOnClickListener(this);
        mobileT.setOnClickListener(this);
        userImage.setOnClickListener(this);
        updateLocationButton.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {//if for some reason, there is an issue with shared preferences associated with auto-redirection of homescreen
            SaveUser.setUserName(getActivity(), "");
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();//remove activity from stack
        } else {
            Uid = user.getUid();
            Log.e(TAG, line + "User: " + Uid + line);
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

            setTexts();

            if (shouldUpdateLocation == true) {
                addUpdateAddressToFirebaseCollection();
            }

//        DatabaseHelper helper = new DatabaseHelper(getActivity());
//        db = helper.getDB();

            Log.e(TAG, line + "getLoginType: " + SaveUser.getLoginType(getActivity()) + line);
            if (SaveUser.getLoginType(getActivity()).equalsIgnoreCase("email")) {
                changePasswordButton.setVisibility(View.VISIBLE);
            }

        }


        return root;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == changePasswordButton.getId()) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle("Enter new password");
//            final EditText input = new EditText(getActivity());
//            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            input.setPadding(75, 10, 0, 60);
//            builder.setView(input);
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    newpassword = input.getText().toString();
//                    user.updatePassword(newpassword).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(getActivity(), "Password changed", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getActivity(), "Password not changed", Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "Error changing password " + e);
//                        }
//                    });
//                }
//            });
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            builder.show();
            launchPasswordDialog("Enter new password", "Change", "Cancel");
        } else if (view.getId() == logoutButton.getId()) {
            Log.e(TAG, "User pressed Logout button");
            SaveUser.setUserName(getActivity(), "");
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();//remove activity from stack

        } else if (view.getId() == addressT.getId()) {
//            if(LocationActivity.shouldUpdateLocation == false){
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle("Update address");
//            final EditText input = new EditText(getActivity());
//            input.setInputType(InputType.TYPE_CLASS_TEXT);
//            input.setPadding(75, 10, 0, 60);
//            builder.setView(input);
//            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    newaddress = input.getText().toString();
//                    addressT.setText(newaddress);
//                    addUpdateAddressToFirebaseCollection();
//                }
//            });
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            builder.show();
            launchAlertDialog("Update address", "Update", "Cancel", "address");
//                Toast.makeText(getActivity(), "Please update location first", Toast.LENGTH_SHORT).show();
//            }
//            else{
//                addUpdateAddressToFirebaseCollection();
//            }
        } else if (view.getId() == bloodGroupT.getId()) {
            if (bloodGroupProvided == false) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Tell us your Blood group");
//                final EditText input = new EditText(getActivity());
//                input.setInputType(InputType.TYPE_CLASS_TEXT);
//                input.setPadding(75, 10, 0, 60);
//                builder.setView(input);
//                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        newblood = input.getText().toString();
//                        bloodGroupT.setText(newblood);
//                        addBloodToFirebaseCollection();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
                launchAlertDialog("Add Blood group", "Add", "Cancel", "blood");
            }
        } else if (view.getId() == userImage.getId()) {
            chooseImage();
        } else if (view.getId() == emailT.getId()) {
            if (emailT.getText().toString().equalsIgnoreCase("Click to add")) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Please enter your email");
//                final EditText input = new EditText(getActivity());
//                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//                input.setPadding(75, 10, 0, 60);
//                builder.setView(input);
//                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        newemail = input.getText().toString();
//                        emailT.setText(newemail);
//                        addEmailToFirebaseCollection();
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
                launchAlertDialog("Please enter your email", "Add", "Cancel", "email");
            }
        } else if (view.getId() == mobileT.getId()) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle("Please enter your mobile no");
//            final EditText input = new EditText(getActivity());
//            input.setInputType(InputType.TYPE_CLASS_PHONE);
//            input.setPadding(75, 10, 0, 60);
//            builder.setView(input);
//            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    newmobile = input.getText().toString();
//                    mobileT.setText(newmobile);
//                    addUpdateMobileToFirebaseCollection();
//                }
//            });
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            builder.show();
            launchAlertDialog("Update contact number", "Update", "Cancel", "mobile");
        } else if (view.getId() == updateLocationButton.getId()) {
//            startActivity(new Intent(getActivity(), LocationActivity.class));
        }
    }

    public void setTexts() {
        DatabaseReference referenceD = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
        referenceD.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "dataSnapshot count: " + dataSnapshot.getChildrenCount());
                if (dataSnapshot.getChildrenCount() > 0) {
                    name = dataSnapshot.child("name").getValue(String.class);
                    email = dataSnapshot.child("email").getValue(String.class);
                    mobile = dataSnapshot.child("mobile").getValue(String.class);
                    address = dataSnapshot.child("address").getValue(String.class);
                    bloodGroup = dataSnapshot.child("blood").getValue(String.class);
                    if (dataSnapshot.child("picture").getValue(String.class) != null) {
                        updateImage();
                    } else {
                        userImage.setImageResource(R.drawable.user_icon);
                    }

                    Log.e(TAG, "Data recieved: " + email + "," + mobile + "," + address + "," + bloodGroup);
                    nameL.setText(name);
                    if (bloodGroup != null) {
                        bloodGroupProvided = true;
                        bloodGroupT.setText(bloodGroup);
                    } else {
                        bloodGroupT.setText("Click to add");
                    }
                    if (address != null) {
                        addressT.setText(address);
                    } else {
                        addressT.setText("Click to add");
                    }
                    if (email != null) {
                        emailT.setText(email);
                    } else {
                        emailT.setText("Click to add");
                    }
                    if (mobile != null) {
                        mobileT.setText(mobile);
                    } else {
                        mobileT.setText("Click to add");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void addUpdateAddressToFirebaseCollection() {
//        if (LocationActivity.shouldUpdateLocation == true){
//            latitude= LocationActivity.latitude;
//            longitude = LocationActivity.longitude;
//            addressT.setText(latitude+", "+ longitude);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child(Uid).child("newaddress").setValue(latitude + ", " + longitude).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Address added to: " + Uid);
                        Toast.makeText(getActivity(), "Address updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error.\n" + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
//        }
//        else {
//            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
//            reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    reference.child(Uid).child("address").setValue(newaddress).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d(TAG, "Address added to: " + Uid);
//                            Toast.makeText(getActivity(), "Address updated", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Toast.makeText(getActivity(), "Error.\n" + databaseError, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    }

    void addUpdateMobileToFirebaseCollection() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child(Uid).child("mobile").setValue(newmobile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Mobile updated of: " + Uid);
                        Toast.makeText(getActivity(), "Mobile updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error.\n" + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void addBloodToFirebaseCollection() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child(Uid).child("blood").setValue(newblood).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Blood added to: " + Uid);
                        Toast.makeText(getActivity(), "Blood added", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error.\n" + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void addEmailToFirebaseCollection() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference.child(Uid).child("email").setValue(newemail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Email added to: " + Uid);
                        Toast.makeText(getActivity(), "Email added", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error.\n" + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chooseImage() {
        Log.e(TAG, "Choose image called");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Gallary_intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, line + "onActivityResult() triggered" + line);
        if (requestCode == Gallary_intent && resultCode == RESULT_OK)///image selected from gallery
        {
            Log.e(TAG, line + "Image selected from gallery" + line);
            uri = data.getData();
            imagePath = FirebaseStorage.getInstance().getReference().child("PatientImages").child(uri.getLastPathSegment());///setting storage reference
            ///uploading file on firebase storage
            showProgressBar();
            imagePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ///upload succe
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            fileUri = uri.toString();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);
                            ref.child("picture").setValue(fileUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        updateImage();
                                    }

                                }
                            });
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ///upload error
                }
            });
        }
    }

    public void updateImage() {
        Log.e(TAG, line + "Update image called" + line);
        showProgressBar();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(Uid);///setting reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {///retrieving data using singleValueEventListener
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ///load image into imageView using picasso
                Log.e(TAG, dataSnapshot.child("picture").getValue(String.class));
                try {
                    Picasso.with(getActivity()).load(dataSnapshot.child("picture").getValue(String.class)).into(userImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            hideProgressBar();
                        }

                        @Override
                        public void onError() {

                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
//        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.dropdownborder));
    }

    public void launchAlertDialog(String title, String positiveButtonTitle, String negativeButtonTitle, final String dialogType) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view1 = inflater.inflate(R.layout.custom_dialog, null);
        final androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(title);
        alertDialog.setIcon(R.drawable.logo_icon);
        alertDialog.setCancelable(false);
//        alertDialog.setMessage("");

        final EditText inputText = (EditText) view1.findViewById(R.id.input);
        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, positiveButtonTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogType.equalsIgnoreCase("address")) {
                    newaddress = inputText.getText().toString();
                    addressT.setText(newaddress);
                    addUpdateAddressToFirebaseCollection();
                } else if (dialogType.equalsIgnoreCase("blood")) {
                    newblood = inputText.getText().toString();
                    bloodGroupT.setText(newblood);
                    addBloodToFirebaseCollection();
                } else if (dialogType.equalsIgnoreCase("email")) {
                    newemail = inputText.getText().toString();
                    emailT.setText(newemail);
                    addEmailToFirebaseCollection();
                } else if (dialogType.equalsIgnoreCase("mobile")) {
                    inputText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                    inputText.setInputType(Configuration.KEYBOARD_12KEY);
                    newmobile = inputText.getText().toString();
                    mobileT.setText(newmobile);
                    addUpdateMobileToFirebaseCollection();
                }

            }
        });

        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, negativeButtonTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();

            }
        });

        alertDialog.setView(view1);
        alertDialog.show();
    }

    public void launchPasswordDialog(String title, String positiveButtonTitle, String negativeButtonTitle) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view1 = inflater.inflate(R.layout.password_dialog, null);
        final androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(title);
        alertDialog.setIcon(R.drawable.logo_icon);
        alertDialog.setCancelable(false);
//        alertDialog.setMessage("");

        final EditText inputText = (EditText) view1.findViewById(R.id.input);
        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, positiveButtonTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newpassword = inputText.getText().toString();
                user.updatePassword(newpassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Password changed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Password not changed", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error changing password " + e);
                    }
                });

            }
        });

        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, negativeButtonTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();

            }
        });

        alertDialog.setView(view1);
        alertDialog.show();
    }
}
