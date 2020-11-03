package com.highsecured.voteeasy;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.highsecured.voteeasy.VoiceVoting.VoiceSigninSecondary;

import java.util.HashMap;
import java.util.Map;


public class PopActivity extends AppCompatActivity {

    private static final String TAG = "PopActivity";
    private static final int READ_PHONE_STATE = 1;
    private ImageView close;
    private ImageView CandidateImage;
    private TextView CandiName , PartyName;
    private Button vote_btn;
    ProgressDialog progressDialog;
    private FirebaseFunctions mFunctions;

    TelephonyManager tm;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_popup);

       checkPermission();



        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        String party = intent.getStringExtra("party");
        String ImageUrl = intent.getStringExtra("ImageUrl");
        final String poll_no = intent.getStringExtra("poll_no");
        final String phoneNumber = intent.getStringExtra("phoneNumber");

        mFunctions = FirebaseFunctions.getInstance();

        close = findViewById(R.id.closeView);
        CandidateImage = findViewById(R.id.CandiImage);
        CandiName = findViewById(R.id.CandiName);
        PartyName = findViewById(R.id.PartyName);
        vote_btn = findViewById(R.id.vote_btn);



        Glide.with(PopActivity.this).asBitmap().load(ImageUrl).into(CandidateImage);
        CandiName.setText(name);
        PartyName.setText(party);



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        vote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String imei_no = getIMEINumber(PopActivity.this);


                addVote(poll_no , imei_no , phoneNumber)
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
                                        Toast.makeText(PopActivity.this, "details on error: " + details, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }

                                    // [START_EXCLUDE]
                                    Log.w(TAG, "addNumbers:onFailure", e);
                                    Toast.makeText(PopActivity.this, "an error occured , Voting UNSUCCESSFUL ! Try Again", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();

                                    return;
                                    // [END_EXCLUDE]

                                }

                                // [START_EXCLUDE]
                                String result = task.getResult();
                                progressDialog.dismiss();

                                Toast.makeText(PopActivity.this, "result answer = " +   result, Toast.LENGTH_SHORT).show();
                                // [END_EXCLUDE]

//                                finish();
                                Intent newIntent = new Intent(PopActivity.this, WelcomeActivity.class);
                                startActivity(newIntent);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PopActivity.this, "Voting is failed! Please Vote Again", Toast.LENGTH_SHORT).show();
                    }
                });
                // [END call_add_numbers]

            }
        });


        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        }

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        
        getWindow().setLayout((int) (width*.8) , (int) (height*.7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        
        getWindow().setAttributes(params);
        
    }

    private void checkPermission() {
        int permisI  = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);


        if(permisI == PackageManager.PERMISSION_GRANTED)
        {
            tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        }

        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 123);
        }

    }

    public static String getIMEINumber(@NonNull final Context context)
            throws SecurityException, NullPointerException {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert tm != null;
            imei = tm.getImei();
            //this change is for Android 10 as per security concern it will not provide the imei number.
            if (imei == null) {
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } else {
            assert tm != null;
            if (tm.getDeviceId() != null && !tm.getDeviceId().equals("000000000000000")) {
                imei = tm.getDeviceId();
            } else {
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }

        return imei;
    }


    // [START function_add_numbers]

    private Task<String> addVote(String a , String b , String c)
    {
        progressDialog = new ProgressDialog(PopActivity.this);
        progressDialog.setMessage("Fetching...");
        progressDialog.setTitle("Voter ID Validation");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);



        // Create the arguments to the callable function, which are two Strings
        Map<String, Object> data = new HashMap<>();


        data.put("poll_no", a);
        data.put("imei_no", b);
        data.put("phoneNumber" , c);

        // Call the function and extract the operation from the result
        return mFunctions
                .getHttpsCallable("addVote")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {

                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        Map<String, Object> result = (Map<String, Object>) task.getResult().getData();

                        String details = (String) result.get("Thankful");

                        if(details.equals("Already voted!") || details.equals("Voted Successfully!") ){
                            Toast.makeText(PopActivity.this, details, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PopActivity.this, ErrorActivity.class);
                            startActivity(intent);
                        }

                        return (String) result.get("Thankful");


                    }
                });
    }
    // [END function_add_numbers]


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case READ_PHONE_STATE:
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                    try {
//                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                            return;
//                        }
//                        String imei = telephonyManager.getDeviceId();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//        }
//    }
}
