package com.example.test_sel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test_sel.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;


public class UserSignUpActivity extends AppCompatActivity {
    String[] arr = {"1", "2"};
    //  https://medium.com/@hannaholukoye/adding-an-icon-on-a-spinner-on-android-e99c7bc6c180

    private EditText EDT_full_nameET, EDT_start_year, EDT_phone_numberET, EDT_description;
    private ImageView img_head;
    private Spinner SPN_engineering, SPN_academy;
    private Button BTNsign_up;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    private ProgressBar ProgressBar;
    private String fullName, phone, academy, start_year, userId, imagePath = "", engineering = "", description = "";
    private DatabaseReference rootDataBase;

    private DatabaseReference refuser;

    private boolean isSignUp = true;
    Intent deleteIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);
        EDT_full_nameET = findViewById(R.id.EDT_full_nameET);
        EDT_phone_numberET = findViewById(R.id.EDT_phone_numberET);
        EDT_start_year = findViewById(R.id.EDT_start_year);
        BTNsign_up = findViewById(R.id.BTNsign_up);
        img_head = findViewById(R.id.IMG_signup_img);
        EDT_description = findViewById(R.id.EDT_description);

        String type = getIntent().getExtras().getString("type");
        isSignUp = type.equals("signUp") ? true : false;
        if (isSignUp) {
            EDT_phone_numberET.setText(getIntent().getExtras().getString("phone"));
        }

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        ProgressBar = findViewById(R.id.pbarReg);
        userId = fAuth.getCurrentUser().getUid();

        //save data on firebase
        rootDataBase = FirebaseDatabase.getInstance().getReference("Users");

        if (isSignUp) {
            userId = fAuth.getCurrentUser().getUid();
            phone = getIntent().getExtras().getString("phone");
        } else {
            img_head.setImageResource(R.drawable.img_update);
            BTNsign_up.setText("UPDATE");
            setMyData();
            userId = getIntent().getExtras().getString("userid");
        }


        //acadany spinner
        SPN_academy = findViewById(R.id.SPN_academy);

        ArrayAdapter<CharSequence> academicAdapter = ArrayAdapter.createFromResource(this, R.array.academy, android.R.layout.simple_spinner_item);
        academicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPN_academy.setAdapter(academicAdapter);
        SPN_academy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                academy = parent.getItemAtPosition(position).toString();
                if (position == 0) {
                    //select nothing
                    SPN_academy.setSelection(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //engineering spinner
        SPN_engineering = findViewById(R.id.SPN_engineering);


        ArrayAdapter<CharSequence> engineeringAdapter = ArrayAdapter.createFromResource(this, R.array.engineering, android.R.layout.simple_spinner_item);
        engineeringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPN_engineering.setAdapter(engineeringAdapter);

        SPN_engineering.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                engineering = parent.getItemAtPosition(position).toString();
                if (position == 0) {
                    //select nothing
                    SPN_engineering.setSelection(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //if user  save details
        BTNsign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullName = EDT_full_nameET.getText().toString();
                phone = EDT_phone_numberET.getText().toString().trim();
                start_year = EDT_start_year.getText().toString().trim();
                description = EDT_description.getText().toString().trim();


                //checks
                if (TextUtils.isEmpty(fullName)) {
                    EDT_full_nameET.setError("name is required");
                    return;
                }
                if (TextUtils.isEmpty(start_year)) {
                    EDT_start_year.setError("your start year is required");
                    return;
                }
                if (Integer.parseInt(start_year) > 2020 || Integer.parseInt(start_year) < 2000) {
                    EDT_start_year.setError("the year that you start study is not legal");
                    return;
                }

                //ProgressBar after all the check
                ProgressBar.setVisibility(View.VISIBLE);

                User user = new User(userId, fullName, phone, academy, start_year, engineering, imagePath, description);

                // insert to the firebsae
                rootDataBase.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            finish();
                        } else {
                            Toast.makeText(UserSignUpActivity.this, "data is not insert ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }

//TODO IT HAs WEIRD ERROR when i try to do ,1 ,method yo init spinner

//    public void mySpinner(int arr,final String insertStr, final Spinner spinner) {
//        ArrayAdapter<CharSequence> engineeringAdapter = ArrayAdapter.createFromResource(this, arr, android.R.layout.simple_spinner_item);
//        engineeringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(engineeringAdapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                insertStr = parent.getItemAtPosition(position).toString();
////                engineering=String.valueOf(SPN_engineering.getSelectedItemId());
//                if (position == 0) {
//                    //select nothing
//                    spinner.setSelection(1);
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//    }


    public boolean isEDempty(EditText etText) {
        if (etText.getText().toString().trim().length() == 0) {
            etText.setError("this box is empty");
            return true;
        }
        return false;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public void setMyData() {

        //get data
        refuser = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        refuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                EDT_full_nameET.setText(dataSnapshot.child("fullName").getValue().toString());
                EDT_phone_numberET.setText(dataSnapshot.child("phone").getValue().toString());
                //  txt_fill_academy.setText(dataSnapshot.child("academy").getValue().toString());
                EDT_start_year.setText(dataSnapshot.child("startYear").getValue().toString());
                EDT_description.setText(dataSnapshot.child("description").getValue().toString());
                //txt_fill_engineering.setText(dataSnapshot.child("engineering").getValue().toString());
                imagePath = dataSnapshot.child("imagePath").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStop() {
        if(isSignUp){
            fAuth.signOut();
        }
        super.onStop();
    }
}
