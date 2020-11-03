package com.highsecured.voteeasy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "SigninActivity";

/*    private static final String PREF_LOGIN = "LOGIN_PREF";
    private static final String KEY_CREDENTIALS = "LOGIN_CREDENTIALS";*/

    Button btnSigninPhone, btnSendOtp , btnResendOtp;
    EditText edtSigninPhone, edtSigninOTP;
    TextInputLayout edtSigninPhoneFloatingText, edtSigninOTPFloatingText;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    ProgressBar progressBar;
    String code;
    FirebaseAuth mAuth;

    TextView counter;
    private android.os.CountDownTimer CountDownTimer;
    private long timeleftinmilli = 30000;

    //Cloud functions
    private FirebaseFunctions mFunctions;

    ProgressDialog progressDialog;

//     static StringBufer nameList = "nameList";
//     static String iurlList = "iurlList";
//     static String polling_noList = "polling_nolist";
//     static  String partyList = "partyList";
//     static String partyURLlist = "partyURLlist";


//      StringBuffer nameList = new StringBuffer("");
//      StringBuffer partyList = new StringBuffer("");
//      StringBuffer candiURL = new StringBuffer("");
//      StringBuffer partyUrl = new StringBuffer("");
//      StringBuffer poll_no = new StringBuffer("");






    @Override
    public void onBackPressed() {
        super.onBackPressed();

       /* Intent goback = new Intent(SigninActivity.this, WelcomeActivity.class);
        startActivity(goback);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        btnSigninPhone = findViewById(R.id.btnSigninPhone);
        btnSendOtp = findViewById(R.id.btnsendotpPhone);
        btnResendOtp = findViewById(R.id.btnresendotpPhone);

        counter = findViewById(R.id.counter);


        edtSigninPhone = findViewById(R.id.edtSigninPhone);
        edtSigninOTP = findViewById(R.id.edtSigninOTP);

        edtSigninPhoneFloatingText = findViewById(R.id.edtSigninPhoneFloatingText);
        edtSigninOTPFloatingText = findViewById(R.id.edtSigninOTPFloatingText);

        progressBar = findViewById(R.id.progressBar);






        mAuth = FirebaseAuth.getInstance();

        //Cloud Function
        mFunctions = FirebaseFunctions.getInstance();

        //register with mobile number - callback
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                String otp_received = phoneAuthCredential.getSmsCode();

                if (otp_received != null)
                {

                    edtSigninOTP.setVisibility(View.VISIBLE);

                      edtSigninOTP.setText(otp_received);

                    //Toast.makeText(getApplicationContext(), "Code has been sent to the number", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);




                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code, otp_received);
                    signInWithPhone(credential);
                }

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                edtSigninOTP.setVisibility(View.VISIBLE);

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

                edtSigninOTP.setVisibility(View.VISIBLE);

                String input_code = edtSigninOTP.getText().toString().trim();

                super.onCodeSent(s, forceResendingToken);
                code = s;

                if(input_code.isEmpty() || input_code.length() < 6){
                    edtSigninOTPFloatingText.setError("Valid OTP is required");
                    edtSigninOTPFloatingText.setHintEnabled(false);
                    edtSigninOTPFloatingText.requestFocus();
                    return;
                } else {
                    edtSigninOTPFloatingText.setError(null);
                }

                if (code != null) {
                    //Toast.makeText(getApplicationContext(), "Code has been sent to the number", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);


                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code, input_code);
                    signInWithPhone(credential);
                }
            }
        };
    }

    public void signInWithPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            progressBar.setVisibility(View.GONE);

                            progressDialog = new ProgressDialog(SigninActivity.this);
                            progressDialog.setMessage("Fetching...");
                            progressDialog.setTitle("Voter ID Validation");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                            progressDialog.setCancelable(false);

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot != null) {

                                        Log.d(TAG, "onDataChange: function Called");


                                        //Cloud function code :D
                                        {
                                            // [START call_add_numbers]

//                                            addNumbers(Long.parseLong(edtSigninPhone.getText().toString()), 6)
                                            addNumbers(edtSigninPhone.getText().toString().trim(), "You")
                                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<String> task) {
                                                            if (!task.isSuccessful()) {
                                                                Exception e = task.getException();
                                                                if (e instanceof FirebaseFunctionsException) {
                                                                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                                                    // Function error code, will be INTERNAL if the failure
                                                                    // was not handled properly in the function call.
                                                                    FirebaseFunctionsException.Code code = ffe.getCode();
                                                                    // Arbitrary error details passed back from the function,
                                                                    // usually a Map<String, Object>.
                                                                    Object details = ffe.getDetails();
                                                                    Toast.makeText(SigninActivity.this, "details on error: " + details, Toast.LENGTH_SHORT).show();

                                                                }

                                                                // [START_EXCLUDE]
                                                                Log.w(TAG, "addNumbers:onFailure", e);
                                                                Toast.makeText(SigninActivity.this, "an error occured", Toast.LENGTH_SHORT).show();
                                                                return;
                                                                // [END_EXCLUDE]

                                                            }



                                                            // [START_EXCLUDE]
                                                            String result = task.getResult();

                                                            Toast.makeText(SigninActivity.this, "result answer = " +   result, Toast.LENGTH_SHORT).show();
                                                            // [END_EXCLUDE]
                                                        }
                                                    });
                                            // [END call_add_numbers]
                                        }

                                        Log.d(TAG, "onDataChange: function retrieved the answer" );

                                        progressDialog.dismiss();

//                                        String names = nameList.toString();
//                                        String parties = partyList.toString();
//                                        String nameImage = candiURL.toString();
//                                        String partyImage = partyUrl.toString();
//                                        String pollno =  poll_no.toString();
//
//
//
//                                        Log.d(TAG, "onCreate: from CandiNameList: " + names);
//                                        Log.d(TAG, "onCreate: from PartyNameList: " + parties);
//                                        Log.d(TAG, "onCreate: from CandiImageURLs: " + nameImage);
//                                        Log.d(TAG, "onCreate: from PartyImageURLs: " + partyImage);
//                                        Log.d(TAG, "onCreate: from Polling_no: " + pollno);

                                        Intent intent = new Intent(SigninActivity.this, PollActivity.class);
//                                        intent.putExtra("CandiNames" , names);
//                                        intent.putExtra("CandiImages" , nameImage);
//                                        intent.putExtra("CandiParty" , parties);
//                                        intent.putExtra("PartyImages" , partyImage);
//                                        intent.putExtra("Polling_no" , pollno);


//                                        startActivity(intent);
//                                        finish();

                                    } else {
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.activity_custom_toast,
                                                (ViewGroup) findViewById(R.id.custom_toast_container));

                                        TextView text = (TextView) layout.findViewById(R.id.text);
                                        text.setText("Phone number is not registered");

                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setGravity(Gravity.BOTTOM, 0, 45);
                                        toast.setDuration(Toast.LENGTH_LONG);
                                        toast.setView(layout);
                                        toast.show();

                                  /*      SharedPreferences.Editor editor = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE).edit();
                                        editor.clear();
                                        editor.commit();
*/
                                        progressBar.setVisibility(View.GONE);
                                        return;
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });

                        }

                        else {
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.activity_custom_toast,
                                        (ViewGroup) findViewById(R.id.custom_toast_container));

                                TextView text = (TextView) layout.findViewById(R.id.text);
                                text.setText("Invalid OTP");

                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.BOTTOM, 0, 45);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();

                                progressBar.setVisibility(View.GONE);
                            }
                            else {
                                edtSigninOTPFloatingText.setError("Invalid OTP");
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    public void signinPhone(View view) {

        String number = edtSigninPhone.getText().toString().trim();

        if(number.isEmpty() || number.length() < 10) {
            if (number.isEmpty() || number.length() < 10) {
                edtSigninPhoneFloatingText.setError("Valid mobile number is required");
                edtSigninPhoneFloatingText.setHintEnabled(false);
                edtSigninPhoneFloatingText.requestFocus();
            } else {
                edtSigninPhoneFloatingText.setError(null);
            }
        } else {
            edtSigninPhoneFloatingText.setError(null);
            progressBar.setVisibility(View.VISIBLE);
            counter.setVisibility(View.VISIBLE);
            btnSendOtp.setVisibility(View.INVISIBLE);
            btnSigninPhone.setVisibility(View.VISIBLE);
            startTimer();
            checkForPhoneNumber(number);

        }
    }

    private void startTimer() {
        CountDownTimer = new android.os.CountDownTimer( timeleftinmilli = 60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                counter.setText("Please wait for " + millisUntilFinished/1000 +  " sec");
            }

            @Override
            public void onFinish() {

                edtSigninOTPFloatingText.setVisibility(View.INVISIBLE);
                counter.setText("finished");
                counter.setVisibility(View.GONE);
                btnResendOtp.setVisibility(View.VISIBLE);
                btnResendOtp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        edtSigninOTPFloatingText.setVisibility(View.VISIBLE);
                        btnResendOtp.setVisibility(View.GONE);
                        btnSigninPhone.setVisibility(View.VISIBLE);
                    }
                });

            }
        }.start();

    }


    private void verifyPhone() {

        String number = edtSigninPhone.getText().toString();
        String phonenumber = "+91" + number;
        progressBar.setVisibility(View.GONE);

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
                    verifyPhone();
                }
                else {

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.activity_custom_toast,
                            (ViewGroup) findViewById(R.id.custom_toast_container));

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText("Phone number is not registered");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                    progressBar.setVisibility(View.GONE);

                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void requestSMSPermission()
    {
        String permission = Manifest.permission.RECEIVE_SMS;

        int grant = ContextCompat.checkSelfPermission(this, permission);
        if(grant != PackageManager.PERMISSION_GRANTED)
        {
            String[] permission_list = new String[1];
            permission_list[0] = permission;

            ActivityCompat.requestPermissions(this, permission_list, 1);

        }
    }



    // [START function_add_numbers]

    private Task<String> addNumbers(String a, String b)
    {
        progressDialog = new ProgressDialog(SigninActivity.this);
        progressDialog.setMessage("Fetching...");
        progressDialog.setTitle("Voter ID Validation");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);



        // Create the arguments to the callable function, which are two Strings
        Map<String, Object> data = new HashMap<>();


        data.put("phoneNumber", a);
        data.put("secondNumber", b);

        // Call the function and extract the operation from the result
        return mFunctions
                .getHttpsCallable("addNumbers")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {

                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        Map<String, Object> result = (Map<String, Object>) task.getResult().getData();

                        String nameList = (String) result.get("CandiName");
                        String iurlList = (String) result.get("CandiImage");
                        String partyList = (String) result.get("CandiParty");
                        String partyURLlist = (String) result.get("PartyImage");
                        String polling_noList = (String) result.get("CandiPollingNo");
                        String voter_details = (String) result.get("VoterDetails");


                        Intent intent = new Intent(SigninActivity.this, PollActivity.class);
                            intent.putExtra("CandiNames" , nameList);
                            intent.putExtra("CandiImages" , iurlList);
                            intent.putExtra("CandiParty" , partyList);
                            intent.putExtra("PartyImages" , partyURLlist);
                            intent.putExtra("Polling_no" , polling_noList);
                            intent.putExtra("VoterDetails" , voter_details);
                            intent.putExtra("phoneNumber" , edtSigninPhone.getText().toString().trim());

                          startActivity(intent);
                          finish();

//
//                        if(partyURLlist.equals("undefined") || nameList.equals("undefined") || iurlList.equals("undefined")
//                        || partyList.equals("undefined") || polling_noList.equals("undefined"))
//                        {
//
//
//                            addNumbers("5" , "6" );
//
//                        }
//                        else{
//
//                            return  partyList;
//                        }

                        return partyList;


                    }
                });
    }
    // [END function_add_numbers]



}
