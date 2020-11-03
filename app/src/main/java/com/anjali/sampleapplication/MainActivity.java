package com.anjali.sampleapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private CountryCodePicker ccp;
    private EditText phoneNum;
    private EditText otp;
    private Button signUp;
    private String checker = "", phoneNumber = "";
    private LinearLayout linearLayout;
    private FirebaseAuth mAuth;
    private String mVerification;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        phoneNum = findViewById(R.id.input_signUp_phone);
        ccp = findViewById(R.id.countryPicker);
        ccp.registerCarrierNumberEditText(phoneNum);
        otp = findViewById(R.id.inputcode);
        signUp = findViewById(R.id.signUP);
        linearLayout = findViewById(R.id.lPhoneNumber);



        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signUp.getText().equals("submit") || checker.equals("code Sent")){
                    String VerificationCode=otp.getText().toString();
                    if (VerificationCode.equals("")){
                        Toast.makeText(MainActivity.this, "aaaa", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        loadingBar.setTitle("code verification");
                        loadingBar.setMessage("Please wait");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerification,VerificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }

                }
                else{
                    phoneNumber=ccp.getFullNumberWithPlus();
                    /*phoneNumber=ccp.getSelectedCountryCodeWithPlus();
                    String aa=phoneNum.getText().toString();
                    phoneNumber+=aa;
                    */
                    //Toast.makeText(MainActivity.this, phoneNumber, Toast.LENGTH_SHORT).show();


                    if(!phoneNumber.equals("")){
                        loadingBar.setTitle("Phone number verification");
                        loadingBar.setMessage("Please wait");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        //mAuth.getFirebaseAuthSettings().forceRecaptchaFlowForTesting();

                        //PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60,TimeUnit.SECONDS,MainActivity.this,mCallbacks);
                        PhoneAuthOptions options =
                                PhoneAuthOptions.newBuilder(mAuth)
                                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(MainActivity.this)                 // Activity (for callback binding)
                                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                        .build();
                        PhoneAuthProvider.verifyPhoneNumber(options);
                    }else{
                        Toast.makeText(MainActivity.this, "Please write valid phone number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(MainActivity.this, "Invalid Phone number..."+e, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                linearLayout.setVisibility(View.VISIBLE);
                signUp.setText("continue");
                otp.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerification=s;
                mResendToken=forceResendingToken;
                linearLayout.setVisibility(View.GONE);
                checker="code Sent";
                signUp.setText("submit");
                otp.setVisibility(View.VISIBLE);
                loadingBar.dismiss();
                Toast.makeText(MainActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "You are now logged in", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        } else {
                            loadingBar.dismiss();
                            String e=task.getException().toString();
                            Toast.makeText(MainActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void sendUserToMainActivity(){
        mAuth.signOut();
        Intent intent=new Intent(MainActivity.this,Activity_second.class);
        startActivity(intent);
        //finish();

    }
}