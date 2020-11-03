/*
package com.highsecured.voteeasy.VoiceVoting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.highsecured.voteeasy.PollActivity;
import com.highsecured.voteeasy.R;
import com.highsecured.voteeasy.ScanActivity;
import com.highsecured.voteeasy.SigninActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VoiceSigninActivity extends AppCompatActivity implements
        RecognitionListener {

    private static final String TAG = "VoiceSigninActivity";

    Getset texting = new Getset();
    TouchGetSet touch = new TouchGetSet();


    private static final int REQUEST_RECORD_PERMISSION = 100;

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    private String LOG_TAG = "SpeechRecognizer";
    Boolean recordButtonStatus;
    private ListView mList;

    //speaking
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private TextToSpeech mTTS;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;



    private int removeCount = 0;

    private DatabaseReference reference;

    static String newString = "";


    //touchRelative
    private RelativeLayout touchRelative, touchRelative2, touchRelative3, touchRelative4, touchRelative5;
    private String correctedList;
    private int notify = 0;


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






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_signin_activity);


        reference = FirebaseDatabase.getInstance().getReference().child("list");


        recordButtonStatus = false;

        mList = (ListView) findViewById(R.id.list_speech);


        //touchRelative
        touchRelative = findViewById(R.id.touchRelative);
        touchRelative2 = findViewById(R.id.touchRelative2);
        touchRelative3 = findViewById(R.id.touchRelative3);
        touchRelative4 = findViewById(R.id.touchRelative4);
        touchRelative5 = findViewById(R.id.touchRelative5);

        {

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


        touchRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: touchRelativeClicked");

                if(recordButtonStatus){
                    recordButtonStatus = false;

                    texting.setSayYN("no");
                    texting.setCorrectedList("false");

                    speech.stopListening();

                    notify++;

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
                }else{
                    ActivityCompat.requestPermissions
                            (VoiceSigninActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                    recordButtonStatus = true;

                    texting.setCorrectedList("false");

                    Log.d(TAG, "onClick: setCorrectedList = " + texting.getCorrectedList());

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_on));
                }
            }
        });


        touchRelative2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recordButtonStatus){
                    recordButtonStatus = false;

                    touch.setYn("activate");
                    texting.setSayYN("activate");
                    texting.setActivateRemove("no");
                    texting.setActivateappend("no");
                    texting.setCorrectedList("true");


                    notify++;

                    speech.stopListening();

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
                }else{
                    ActivityCompat.requestPermissions
                            (VoiceSigninActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                    recordButtonStatus = true;
                    texting.setCorrectedList("true");

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_on));
                }
            }
        });

        touchRelative3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recordButtonStatus){
                    recordButtonStatus = false;


                    texting.setActivateRemove("no");
                    texting.setActivateappend("no");
                    texting.setCorrectedList("addremove");


                    notify++;

                    speech.stopListening();

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
                }else{
                    ActivityCompat.requestPermissions
                            (VoiceSigninActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                    recordButtonStatus = true;
                    texting.setCorrectedList("addremove");

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_on));
                }
            }
        });

        touchRelative4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recordButtonStatus){
                    recordButtonStatus = false;


                    texting.setActivateRemove("no");
                    texting.setActivateappend("no");
                    texting.setCorrectedList("add");


                    notify++;

                    speech.stopListening();

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
                }
                else{
                    ActivityCompat.requestPermissions
                            (VoiceSigninActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                    recordButtonStatus = true;
                    texting.setCorrectedList("add");

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_on));
                }
            }
        });
        touchRelative5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recordButtonStatus){
                    recordButtonStatus = false;


                    texting.setActivateRemove("no");
                    texting.setActivateappend("no");
                    texting.setCorrectedList("remove");


                    notify++;

                    speech.stopListening();

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
                }else{
                    ActivityCompat.requestPermissions
                            (VoiceSigninActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                    recordButtonStatus = true;
                    texting.setCorrectedList("remove");

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_on));
                }
            }
        });


        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);


        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);


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
                            //   mButtonSpeak.setEnabled(true);
                            speak();


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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(VoiceSigninActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            recordButtonStatus = false;
//            recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
            Log.i(LOG_TAG, "destroy");
        }
    }


    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");


        if (texting.getCorrectedList() == "add" )
        {
            touchRelative4.setVisibility(View.GONE);
            touchRelative3.setVisibility(View.VISIBLE);
        }
        if (texting.getCorrectedList() == "remove"){

            touchRelative5.setVisibility(View.GONE);
            touchRelative3.setVisibility(View.VISIBLE);

        }



    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        recordButtonStatus = false;
//        recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "Results");

    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "Ready For Speech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "Results");


        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String equateTextadd = texting.getActivateappend();
        String equateTextremove = texting.getActivateRemove();
        String equateReview = texting.getSayYN();
        String equateCorrected = texting.getCorrectedList();


        if( equateCorrected == "true")
        {

            String text = matches.get(0);

            String[] arrOfText = text.split(" ", -2);

            List<String> list = new ArrayList<String>();
            ArrayList<String> names = new ArrayList<>();


            for (String a : arrOfText){
//            names.add("\"" + a + "\""
                names.add(a);
                Log.d(TAG, "onResults: " + a);
            }

            if (names.contains("repeat") || names.contains("re")
                    || names.contains("peat") || names.contains("p") ) {

                texting.setState("yes");
                recordButtonStatus = false;

                float pitch = (float) mSeekBarPitch.getProgress() / 55;
                if (pitch < 0.1) pitch = 0.1f;
                float speed = (float) mSeekBarSpeed.getProgress() / 80;
                if (speed < 0.1) speed = 0.1f;

                mTTS.setPitch(pitch);
                mTTS.setSpeechRate(speed);

                mTTS.speak(texting.getText() + "Please tell the Candidate ID to make voting", TextToSpeech.QUEUE_FLUSH, null);

                touchRelative2.setVisibility(View.GONE);
                touchRelative3.setVisibility(View.VISIBLE);

            }

            else if (names.contains("no") || names.contains("No")
                    || names.contains("know") || names.contains("now")){

//                textuse.setState("yes");
                recordButtonStatus = false;

                float pitch = (float) mSeekBarPitch.getProgress() / 55;
                if (pitch < 0.1) pitch = 0.1f;
                float speed = (float) mSeekBarSpeed.getProgress() / 80;
                if (speed < 0.1) speed = 0.1f;

                mTTS.setPitch(pitch);
                mTTS.setSpeechRate(speed);

                mTTS.speak("OK, then,,,Do you want to add or remove items?, " +
                        "if yes, please say add or remove or else say continue to move on" , TextToSpeech.QUEUE_FLUSH, null);

                touchRelative2.setVisibility(View.GONE);
                touchRelative3.setVisibility(View.VISIBLE);


            }

            texting.setState("yes");
        }
        else if( equateCorrected == "addremove")
        {

            String text = matches.get(0);

            String[] arrOfText = text.split(" ", -2);

            List<String> list = new ArrayList<String>();
            ArrayList<String> names = new ArrayList<>();


            for (String a : arrOfText){
//            names.add("\"" + a + "\""
                names.add(a);
                Log.d(TAG, "onResults: " + a);
            }

            if (names.contains("a") || names.contains("add")
                    || names.contains("A") || names.contains("Add") || names.contains("Ad")
                    || names.contains("ad") || names.contains("had") || names.contains("Had")
                    || names.contains("hat")) {

                texting.setState("yes");
                recordButtonStatus = false;

                float pitch = (float) mSeekBarPitch.getProgress() / 55;
                if (pitch < 0.1) pitch = 0.1f;
                float speed = (float) mSeekBarSpeed.getProgress() / 80;
                if (speed < 0.1) speed = 0.1f;

                mTTS.setPitch(pitch);
                mTTS.setSpeechRate(speed);

                mTTS.speak("touch the screen to say extra items to add", TextToSpeech.QUEUE_FLUSH, null);

                touchRelative3.setVisibility(View.GONE);
                touchRelative4.setVisibility(View.VISIBLE);

            }

            else if (names.contains("remove") || names.contains("remo")
                    || names.contains("Remove") || names.contains("re")){

//                textuse.setState("yes");
                recordButtonStatus = false;

                float pitch = (float) mSeekBarPitch.getProgress() / 55;
                if (pitch < 0.1) pitch = 0.1f;
                float speed = (float) mSeekBarSpeed.getProgress() / 80;
                if (speed < 0.1) speed = 0.1f;

                mTTS.setPitch(pitch);
                mTTS.setSpeechRate(speed);

                mTTS.speak("touch the screen to say unwanted items listed in order to remove them"  , TextToSpeech.QUEUE_FLUSH, null);

                touchRelative3.setVisibility(View.GONE);
                touchRelative5.setVisibility(View.VISIBLE);

            }

            else if (names.contains("continue") || names.contains("conti")
                    || names.contains("Continue") || names.contains("new")
                    || names.contains("con") || names.contains("t")){

                String listofItems = texting.getText();
                reference.setValue(listofItems.replace("biscuit", "1").replace("pen", "2").replace("towel", "3")
                        .replace("handbags", "5").replace("rice", "6")
                        .replace("dhal" , "7").replace("box", "8").replace("null", ""));



//                textuse.setState("yes");
                recordButtonStatus = false;

                touchRelative3.setVisibility(View.VISIBLE);

                Intent intent = new Intent(VoiceSigninActivity.this, ScanActivity.class);
                startActivity(intent);


            }


            texting.setState("yes");
        }



        else if( equateCorrected == "add")
        {

            String text = matches.get(0);
            texting.setText(  text + " " +  texting.getText() );

            String[] arrOfText = texting.getText().split(" ", -2);

            List<String> list = new ArrayList<String>();
            ArrayList<String> names = new ArrayList<>();






            for (String a : arrOfText){
//            names.add("\"" + a + "\""
                names.add(a);
                Log.d(TAG, "onResults: " + a);
            }

            mList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, names));


            if (names.contains("information")) {
                informationMenu();
            }

            recordButtonStatus = false;
//        recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));

            texting.setState("repeat");
        }

        else if( equateCorrected == "remove")
        {

            String text = matches.get(0);

            String[] arrOfText = texting.getText().trim().split(" ", -2);
            String[] arrOfRemove = text.split(" ", -2);

            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> namestoberemoved = new ArrayList<>();


            for (String a : arrOfText){
//            names.add("\"" + a + "\""
                names.add(a);
                Log.d(TAG, "onResults: " + a);
            }

            for (String a : arrOfRemove)
            {
                namestoberemoved.add(a);
            }

            names.removeAll(namestoberemoved);



            if (names.size() == 0)
            {

                Intent intent = new Intent(VoiceSigninActivity.this, VoiceOptionActivity.class);
                texting.setText("");
                texting.setHs("activate");
                startActivity(intent);
            }



            for (String a : names)
            {

                texting.setText(correctedList = correctedList + a + " ");

            }

            correctedList = "";

            mList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, names));

            texting.setState("repeat2");

        }

        else {

            String text = matches.get(0);

            String[] arrOfText = text.split(" ", -2);

            List<String> list = new ArrayList<String>();
            ArrayList<String> names = new ArrayList<>();


            for (String a : arrOfText)
            {
                names.add(a);
                Log.d(TAG, "onResults: " + a);
            }


            for (String a : arrOfText){
//            names.add("\"" + a + "\""
                newString = newString + a;
                Log.d(TAG, "onResults: " + a);
            }

            if(newString.length() >= 10 ){

                newString = newString.replaceAll("[^\\d]", " ").trim();

                // Remove extra spaces from the beginning
                // and the ending of the string

                // Replace all the consecutive white
                // spaces with a single space
                newString = newString.replaceAll(" +", " ");

                if (newString.length() == 10){


                    edtSigninPhone.setText(newString);

                    texting.setText(newString);

                    signinPhone();





                }else {



                }





            }


            mList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, names));


            recordButtonStatus = false;

            texting.setState("tellList");


        }

        recordButtonStatus = false;

        String textTofindLength = texting.getText();

        int length = textTofindLength.length();

        if (length > 0){

            if (texting.getState() == "tellList")
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        String text = "Shall I review the list items? SAY YES OR NO";
                        float pitch = (float) mSeekBarPitch.getProgress() / 55;
                        if (pitch < 0.1) pitch = 0.1f;
                        float speed = (float) mSeekBarSpeed.getProgress() / 70;
                        if (speed < 0.1) speed = 0.1f;

                        mTTS.setPitch(pitch);
                        mTTS.setSpeechRate(speed);

                        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

                        touchRelative.setVisibility(View.GONE);
                        touchRelative2.setVisibility(View.VISIBLE);


                    }
                }, 3000);
            }

            else if (texting.getState() == "repeat")
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        String text = "if you want to add items again, say add or you need to remove items," +
                                " say remove, or say continue to move on ";
                        float pitch = (float) mSeekBarPitch.getProgress() / 55;
                        if (pitch < 0.1) pitch = 0.1f;
                        float speed = (float) mSeekBarSpeed.getProgress() / 70;
                        if (speed < 0.1) speed = 0.1f;

                        mTTS.setPitch(pitch);
                        mTTS.setSpeechRate(speed);

                        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);


                    }
                }, 3000);
            }

            else if (texting.getState() == "repeat2")
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        String text = "successfully removed the unwanted items! once again if you want to add items, say add or you need to remove items," +
                                " say remove, or say continue to move on ";

                        float pitch = (float) mSeekBarPitch.getProgress() / 55;
                        if (pitch < 0.1) pitch = 0.1f;
                        float speed = (float) mSeekBarSpeed.getProgress() / 70;
                        if (speed < 0.1) speed = 0.1f;

                        mTTS.setPitch(pitch);
                        mTTS.setSpeechRate(speed);

                        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);


                    }
                }, 3000);
            }




        }



    }

    private void informationMenu() {
        Toast.makeText(this, "Found!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "RMS Changed: " + rmsdB);

//        Toast.makeText(this, "rmsdata changed", Toast.LENGTH_SHORT).show();

    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "ERROR: There was an error recording audio.";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "ERROR: There was an error with the Client.";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "ERROR:  You need to accept permissions first.  Please go to your phone Settings -> Apps -> Speech to Text and accept.";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "ERROR:  There was a Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "ERROR: There was a Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "ERROR: I didn't quite catch that.";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "ERROR: RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "ERROR:  A Server error occurred";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "ERROR: I didn't quite catch that";
                break;
            default:
                message = "Hmm, I'm not sure I understand, please try again.";
                break;
        }
        return message;
    }

    //speaking
    private void speak() {


        touchRelative2.setVisibility(View.INVISIBLE);

        String text = "Please touch the screen to tell your Registered Mobile Number!";
        float pitch = (float) mSeekBarPitch.getProgress() / 70;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress() / 80;
        if (speed < 0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        texting.setState("tellList");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();

        }

//        super.onDestroy();
    }




    public void signInWithPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            progressBar.setVisibility(View.GONE);

                            progressDialog = new ProgressDialog(VoiceSigninActivity.this);
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
                                                                    Toast.makeText(VoiceSigninActivity.this, "details on error: " + details, Toast.LENGTH_SHORT).show();

                                                                }

                                                                // [START_EXCLUDE]
                                                                Log.w(TAG, "addNumbers:onFailure", e);
                                                                Toast.makeText(VoiceSigninActivity.this, "an error occured", Toast.LENGTH_SHORT).show();
                                                                return;
                                                                // [END_EXCLUDE]

                                                            }



                                                            // [START_EXCLUDE]
                                                            String result = task.getResult();

                                                            Toast.makeText(VoiceSigninActivity.this, "result answer = " +   result, Toast.LENGTH_SHORT).show();
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

                                        Intent intent = new Intent(VoiceSigninActivity.this, PollActivity.class);
//                                        intent.putExtra("CandiNames" , names);
//                                        intent.putExtra("CandiImages" , nameImage);
//                                        intent.putExtra("CandiParty" , parties);
//                                        intent.putExtra("PartyImages" , partyImage);
//                                        intent.putExtra("Polling_no" , pollno);


//                                        startActivity(intent);
//                                        finish();

                                    } else {

                                        String newText = "Phone number is not registered!";
                                        float pitch = (float) mSeekBarPitch.getProgress() / 70;
                                        if (pitch < 0.1) pitch = 0.1f;
                                        float speed = (float) mSeekBarSpeed.getProgress() / 80;
                                        if (speed < 0.1) speed = 0.1f;

                                        mTTS.setPitch(pitch);
                                        mTTS.setSpeechRate(speed);

                                        mTTS.speak(newText, TextToSpeech.QUEUE_FLUSH, null);





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

                                        finish();

      SharedPreferences.Editor editor = getSharedPreferences(PREF_LOGIN, MODE_PRIVATE).edit();
                                        editor.clear();
                                        editor.commit();


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

    public void signinPhone() {

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
        progressDialog = new ProgressDialog(VoiceSigninActivity.this);
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

                        ArrayList<String> mVoter_Details = new ArrayList<>();
                        String[] VoterDetailsStrings = voter_details.trim().split("_", -2);
                        for (String a : VoterDetailsStrings) {
                            mVoter_Details.add(a);
                        }

                        if (mVoter_Details.size() > 1) {


                            String name = mVoter_Details.get(4);
                            String phoneno = mVoter_Details.get(0);
                            String voteridno = mVoter_Details.get(1);
                            String relation = mVoter_Details.get(9);
                            String dob = mVoter_Details.get(5);
                            String consti = mVoter_Details.get(2);
                            String constiName = mVoter_Details.get(6);
                            String part = mVoter_Details.get(3);
                            String partName = mVoter_Details.get(7);
                            String address = mVoter_Details.get(8);


                            String[] arrOfString = nameList.split(" ", -2);

                            StringBuffer sb = new StringBuffer();

                            String Intro = "Hello ." + name + "..." + "I am an Artificial Intelligence ..  that I will poll your vote in highly secured manner based on your choice" +
                                    "Your Voter ID number is .." + voteridno + ".. In your part of the constituency , there are . " + mVoter_Details.size() + "Candidates" +
                                    "I am going to list out the Candidates name with thier Polling NO. , FOR Example If I say ... 1,0,2,4 Arun , here 1,0,2,4 is a polling No. and Arun is a candidate Name" +
                                    "You need to say the prefered Candidate Polling No. Once I listed out the Candidates details. You can also interupt me by touching the screen to tell the Polling Number .while telling the list of candidates details , Thank You ";

                            sb.append(Intro);
                            sb.append(".. The Candidates are ...");
                            for (int i = 0; i < arrOfString.length; i++) {

                                sb.append(arrOfString[i]);
                                sb.append("..");

                            }

                            texting.setText(sb.toString());

                            sb.append("... Please touch the screen to tell the Candidate ID to make voting .. or .. If you want to repeat the candidate ID again .. say REPEAT ");

                            String text = sb.toString();


//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {

//                                text = "Shall I review the list items? SAY YES OR NO";
                            float pitch = (float) mSeekBarPitch.getProgress() / 55;
                            if (pitch < 0.1) pitch = 0.1f;
                            float speed = (float) mSeekBarSpeed.getProgress() / 70;
                            if (speed < 0.1) speed = 0.1f;

                            mTTS.setPitch(pitch);
                            mTTS.setSpeechRate(speed);

                            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

                            touchRelative.setVisibility(View.GONE);
                            touchRelative2.setVisibility(View.VISIBLE);


                            Intent intent = new Intent(VoiceSigninActivity.this, PollActivity.class);
                            intent.putExtra("CandiNames", nameList);
                            intent.putExtra("CandiImages", iurlList);
                            intent.putExtra("CandiParty", partyList);
                            intent.putExtra("PartyImages", partyURLlist);
                            intent.putExtra("Polling_no", polling_noList);
                            intent.putExtra("VoterDetails", voter_details);
                            intent.putExtra("phoneNumber", edtSigninPhone.getText().toString().trim());

                            startActivity(intent);
                            finish();

                            if (partyURLlist.equals("undefined") || nameList.equals("undefined") || iurlList.equals("undefined")
                                    || partyList.equals("undefined") || polling_noList.equals("undefined")) {


                                addNumbers("5", "6");

                            } else {

                                return partyList;
                            }

                        }

                        return "didn't get anything";

                    }

                });

                        }


}

*/
