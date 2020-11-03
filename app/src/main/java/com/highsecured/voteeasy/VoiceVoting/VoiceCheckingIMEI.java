package com.highsecured.voteeasy.VoiceVoting;



import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.highsecured.voteeasy.ErrorActivity;
import com.highsecured.voteeasy.InfoActivity;
import com.highsecured.voteeasy.R;
import com.highsecured.voteeasy.SigninActivity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



//public class VoiceCheckingIMEI extends AppCompatActivity implements UpdateHelper.OnUpdateCheckListener{
public class VoiceCheckingIMEI extends AppCompatActivity {
    private static final int READ_PHONE_STATE = 1;
    private static final String TAG = "VoiceCheckingIMEI";

/*

    private static final String PREF_LOGIN = "LOGIN_PREF";
    private static final String KEY_CREDENTIALS = "LOGIN_CREDENTIALS";

*/

    Button btnWelcomeSignin, btnWelcomeJoinNow;
    Context context;

    DatabaseReference database;
    DatabaseReference iRef;
    TelephonyManager tm;
    private ProgressDialog progressDialog;


    private FirebaseFunctions mFunctions;

    //speaking
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private TextToSpeech mTTS;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;


    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(VoiceCheckingIMEI.this)
                .setTitle("EXIT")
                .setCancelable(false)
                .setMessage("Are your sure want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.finishAffinity(VoiceCheckingIMEI.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        checkPermission();

        String imei_no = getIMEINumber(VoiceCheckingIMEI.this);


        mFunctions = FirebaseFunctions.getInstance();

        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);

        checkIMEI(imei_no)
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
                                Toast.makeText(VoiceCheckingIMEI.this, "details on error: " + details, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                            // [START_EXCLUDE]
                            Log.w(TAG, "addNumbers:onFailure", e);
                            Toast.makeText(VoiceCheckingIMEI.this, "an error occured", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            Intent intent = new Intent(getApplicationContext(), VoiceCheckingIMEI.class);
                            startActivity(intent);

                            return;
                            // [END_EXCLUDE]

                        }

                        // [START_EXCLUDE]
                        String result = task.getResult();
                        progressDialog.dismiss();

                        Toast.makeText(VoiceCheckingIMEI.this, "result answer = " +   result, Toast.LENGTH_SHORT).show();
                        // [END_EXCLUDE]
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VoiceCheckingIMEI.this, "Connection Failed!", Toast.LENGTH_SHORT).show();

                SpeechAssistant("Connection Failed , You have already Voted");

                Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
                startActivity(intent);

            }
        });
        // [END call_add_numbers]



        btnWelcomeSignin = findViewById(R.id.btnWelcomeSignin);

        btnWelcomeSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(intent);
            }
        });


        btnWelcomeJoinNow = findViewById(R.id.btnWelcomeJoinNow);
        btnWelcomeJoinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);

            }
        });





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


    private Task<String> checkIMEI(String a)
    {
        progressDialog = new ProgressDialog(VoiceCheckingIMEI.this);
        progressDialog.setMessage("Searching...");
        progressDialog.setTitle("Checking Mobile Status");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);



        // Create the arguments to the callable function, which are two Strings
        Map<String, Object> data = new HashMap<>();


        data.put("imei_no", a);

        // Call the function and extract the operation from the result
        return mFunctions
                .getHttpsCallable("checkIMEI")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {

                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        Map<String, Object> result = (Map<String, Object>) task.getResult().getData();

                        String required = (String) result.get("getResult");

                        if (required.equals("true")){

                            SpeechAssistant("You have .. Already Voted");

                            Toast.makeText(context, "Access Denied", Toast.LENGTH_SHORT).show();
                            Intent intentTRUE = new Intent(getApplicationContext(), ErrorActivity.class);
                            startActivity(intentTRUE);



                            return required;

                        }
                        else if(required.equals("undefined"))
                        {
                            SpeechAssistant("Please Try Again , Data Fetching Error");


                            Toast.makeText(context, "Please Try again", Toast.LENGTH_SHORT).show();
                            Intent intentUNDEFINED = new Intent(getApplicationContext(), VoiceCheckingIMEI.class);
                            startActivity(intentUNDEFINED);

                            return required;
                        }
                        else if(required.equals("false"))
                        {
                            SpeechAssistant(getString(R.string.accessPermitted));

                            Intent intentFALSE = new Intent(getApplicationContext(), VoiceSigninSecondary.class);
                            startActivity(intentFALSE);

                            return required;
                        }
                        else
                        {

                            SpeechAssistant("You have already voted, If not .. Please try again");
                            Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
                            startActivity(intent);
                            return required;


                        }






                    }


                });
    }



/*    @Override
    public void onUpdateCheckListener(String urlApp) {
        //create alert dialog box

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Latest Version Available!")
                .setMessage("Please update the app to the latest Version :) ")
                .setPositiveButton("UPDATE", null)
                .show();

        alertDialog.setCanceledOnTouchOutside(false);

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentPlaystore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.nssandmarket.nsjainconstructions"));
                startActivity(intentPlaystore);

            }
        });

    }*/
/*@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
        case READ_PHONE_STATE:
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    String imei = telephonyManager.getDeviceId();

                } catch (Exception e) {
                    e.printStackTrace();
                }
    }
}*/


    private void SpeechAssistant(final String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = mTTS.setLanguage(Locale.ENGLISH);



                        if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED)
                        {
                            Log.e("TTS", "Language not supported");


                        }
                        else
                        {

                            speak(text);



                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }, 6000);
                        }

                    }

                    else {
                        Log.e("TTS", "Initialization failed");
                    }
                }


            });
        }

    }

    private void speak(String text) {

        float pitch = (float) mSeekBarPitch.getProgress() / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress() / 50;
        if (speed < 0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }



}