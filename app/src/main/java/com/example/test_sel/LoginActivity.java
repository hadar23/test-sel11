package com.example.test_sel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test_sel.Classes.Course;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText EDT_phone, EDT_code;
    private TextView TXT_msg;
    private Button BTN_next;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private ProgressBar ProgressBar;
    private Boolean flag_verify = false;
    private CountryCodePicker codePicker;
    private String verificationId;
    private String phoneNum = "";
    private PhoneAuthProvider.ForceResendingToken token;
    private DatabaseReference refuser;

    public void readData() throws IOException {
        DatabaseReference rootDataBase = FirebaseDatabase.getInstance().getReference("Courses");

        InputStream is = getResources().openRawResource(R.raw.afekacourse);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";

        reader.readLine();
        while ((line = reader.readLine()) != null) {
            String[] token = line.split(",");
            Course course = new Course((token[0]), token[1]);
            DatabaseReference c = rootDataBase.child(token[0]);
            c.setValue(course);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EDT_phone = findViewById(R.id.EDT_phone);
        EDT_code = findViewById(R.id.EDT_code);
        BTN_next = findViewById(R.id.BTN_next);
        TXT_msg = findViewById(R.id.TXT_msg);

        codePicker = findViewById(R.id.ccp);
        ProgressBar = findViewById(R.id.pbarLog);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        //sign_in button
        BTN_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flag_verify) {
                    if (!EDT_phone.getText().toString().isEmpty() && EDT_phone.getText().toString().length() == 10) {

                        String localPhone = EDT_phone.getText().toString();
                        if (localPhone.substring(0, 1).equals("0")) {
                            localPhone = localPhone.substring(1);
                        }
                        phoneNum = "+" + codePicker.getSelectedCountryCode() + localPhone;
                        ProgressBar.setVisibility(View.VISIBLE);
                        TXT_msg.setVisibility(View.VISIBLE);
                        requestOTP(phoneNum);

                        //flag_verify is true the phone is good
                    } else {
                        EDT_phone.setError("phone number is not valid");
                    }


                } else {
                    String userOTP = EDT_code.getText().toString();
                    if (!userOTP.isEmpty() && userOTP.length() == 6) {

                        //using cedential we can sign in the user
                        PhoneAuthCredential cedential = PhoneAuthProvider.getCredential(verificationId, userOTP);
                        verifyAuth(cedential);
                    } else {
                        EDT_code.setError("Valid OTP is required");
                    }
                }
            }


        });

    }

    //if the user already loged in
    @Override
    protected void onStart() {
        super.onStart();
        if (fAuth.getCurrentUser() != null) {
            ProgressBar.setVisibility(View.VISIBLE);
            TXT_msg.setText("Checking");
            TXT_msg.setVisibility(View.VISIBLE);
            checkUserProfile();
        }
    }

    //check if the user has already profile in the system
    private void checkUserProfile() {
        refuser = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userNameRef = refuser.child("Users").child(fAuth.getCurrentUser().getUid());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    //username exist
                    Intent i = new Intent(getApplicationContext(), homePageActivity.class);
                    i.putExtra("type", "update");
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(getApplicationContext(), UserSignUpActivity.class);
                    i.putExtra("phone", phoneNum);
                    Log.d("myLoginAc", phoneNum);
                    i.putExtra("type", "signUp");
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);

    }

    private void verifyAuth(PhoneAuthCredential cedential) {
        fAuth.signInWithCredential(cedential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Phone Verified." + fAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
                    checkUserProfile();
                } else {
                    ProgressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Authenication failed", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void requestOTP(final String phoneNum) {
        //inside verify we pass the phone num,time in long format, time unit,acitivity we want to callback
        //the time is for the user can resent to otp
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 6L,
                TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    //s string is varication id the we need to verify with the user
                    //forceResendingToken is require when the user not recive the otp and we want to sent otp again

                    //when otp cose  is sent to the user
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        ProgressBar.setVisibility(View.GONE);
                        TXT_msg.setVisibility(View.GONE);
                        EDT_code.setVisibility(View.VISIBLE);
                        verificationId = s;
                        token = forceResendingToken;
                        BTN_next.setText("Verify");
                        flag_verify = true;
//                        String userId = fAuth.getCurrentUser().getUid();

//                //save data on firebase
//                DatabaseReference refUser = FirebaseDatabase.getInstance().getReference("Users").child(userId);
//                refUser.child("phone").setValue(phoneNum);
//                refUser.child("isFinished").setValue("false");


                        // as soon as the code is sent ' we want the user to be able to type

                    }

                    //when the otp time out , in our case 6 second
                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        Toast.makeText(LoginActivity.this, "OTP Timeout, Please Re-generate the OTP Again.", Toast.LENGTH_SHORT).show();

                    }

                    //when its verified
                    @Override
                    //if the phone can let the app call automatic the opr
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        //sign in to app
                        verifyAuth(phoneAuthCredential);

                    }

                    // when we have error
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(LoginActivity.this, "cannot create account " + e.getMessage(), Toast.LENGTH_SHORT).show();
//
                    }
                });
    }
}
