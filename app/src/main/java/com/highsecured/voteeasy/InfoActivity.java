package com.highsecured.voteeasy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class InfoActivity extends AppCompatActivity {

    private EditText edtPhone, edtName;
    private FirebaseAuth mAuth;
    private RadioButton buyer;
    TextInputLayout edtNameFloatingText, edtPhoneFloatingText;
    String code;
    String otp_received;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private ProgressBar pgsBar;

    private static final String TAG = "InfoActivity";


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent goback = new Intent(InfoActivity.this, WelcomeActivity.class);
        startActivity(goback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        edtPhone = findViewById(R.id.edtPhone);
        edtName = findViewById(R.id.edtName);


        pgsBar = findViewById(R.id.pBar);

        edtPhoneFloatingText = findViewById(R.id.edtPhoneFloatingText);
        edtNameFloatingText = findViewById(R.id.edtNameFloatingText);

        //buyer = findViewById(R.id.buyer);
        //seller = findViewById(R.id.seller);

        mAuth = FirebaseAuth.getInstance();


        //register with mobile number
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                pgsBar.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(), "Verification Completed", Toast.LENGTH_SHORT).show();

                Log.d(TAG, "onVerificationCompleted: phone code: " + phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pgsBar.setVisibility(View.GONE);
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.activity_custom_toast,
                        (ViewGroup) findViewById(R.id.custom_toast_container));

                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText(e.getMessage());

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM, 0, 45);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {


                super.onCodeSent(s, forceResendingToken);
                code = s;

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.activity_custom_toast,
                        (ViewGroup) findViewById(R.id.custom_toast_container));

                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText("Code has been sent to the number");


                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM, 0, 45);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

                pgsBar.setVisibility(View.GONE);

                String phone1 = edtPhone.getText().toString().trim();
                String name1 = edtName.getText().toString().trim();



                Intent intent = new Intent(InfoActivity.this, CodeVerifyActivity.class);
                intent.putExtra("verifycode", code);
                intent.putExtra("phone1", phone1);
                intent.putExtra("name1" , name1);
                intent.putExtra("type", "Buyer");

                startActivity(intent);
            }
        };
    }


        public void send_otp (View v){

            String phone = edtPhone.getText().toString().trim();
            String name = edtName.getText().toString().trim();

            if (name.isEmpty() || name.length() < 4)

            {

                if (name.isEmpty() || name.length() < 10) {
                    edtNameFloatingText.setError("Name should be atleast 4 letters");
                    edtNameFloatingText.setHintEnabled(false);
                    edtNameFloatingText.requestFocus();
                } else {
                    edtNameFloatingText.setError(null);
                }

            }

            else{

                //checking validity
                if (phone.isEmpty() || phone.length() < 10 ) {

                    if (phone.isEmpty() || phone.length() < 10) {
                        edtPhoneFloatingText.setError("Valid mobile number is required");
                        edtPhoneFloatingText.setHintEnabled(false);
                        edtPhoneFloatingText.requestFocus();
                    } else {
                        edtPhoneFloatingText.setError(null);
                    }

                } else {
                    pgsBar.setVisibility(View.VISIBLE);
                    checkForPhoneNumber(phone);
                }

            }



        }

    private void verifyPhone() {
        String number = edtPhone.getText().toString();
        String phonenumber = "+91" + number;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallback
        );

    }

    public void checkForPhoneNumber(String number) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.orderByChild("phone").equalTo(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    pgsBar.setVisibility(View.GONE);

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.activity_custom_toast,
                            (ViewGroup) findViewById(R.id.custom_toast_container));

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText("Phone number is already registered");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 45);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                }
                else {
                    verifyPhone();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

/*    public void needHelp(View view) {
        Intent intent = new Intent(getApplicationContext(), Needhelp.class);
        startActivity(intent);
    }*/
}