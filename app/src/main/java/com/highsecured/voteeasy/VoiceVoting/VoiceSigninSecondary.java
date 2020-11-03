package com.highsecured.voteeasy.VoiceVoting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.highsecured.voteeasy.ErrorActivity;
import com.highsecured.voteeasy.PollActivity;
import com.highsecured.voteeasy.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.highsecured.voteeasy.PopActivity.getIMEINumber;

public class VoiceSigninSecondary extends AppCompatActivity implements RecognitionListener {

    private static final String TAG = "CreateList";

    Getset address = new Getset();

    static String cutOut = "";


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

    StringBuffer sb = new StringBuffer();

    private int removeCount = 0;

    private DatabaseReference reference;


    //touchRelative
    private RelativeLayout touchRelative, touchRelative2, touchRelative3, touchRelative4, touchRelative5;
    private String correctedList;
    private int notify = 0;


    
    //Signin Assets
    

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

    TouchGetSet activation = new TouchGetSet();

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VoiceSigninSecondary.this, VoiceOptionActivity.class);
        startActivity(intent);

        super.onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_signin_activity);



        recordButtonStatus = false;

        mList = (ListView) findViewById(R.id.list_speech);


        //touchRelative
        touchRelative = findViewById(R.id.touchRelative);
        touchRelative2 = findViewById(R.id.touchRelative2);
        touchRelative3 = findViewById(R.id.touchRelative3);
        touchRelative4 = findViewById(R.id.touchRelative4);
        touchRelative5 = findViewById(R.id.touchRelative5);

        touchRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Log.d(TAG, "onClick: touchRelativeClicked");

                if(recordButtonStatus){
                    recordButtonStatus = false;

                    speech.stopListening();

                    notify++;

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
                }else{
                    ActivityCompat.requestPermissions
                            (VoiceSigninSecondary.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                    recordButtonStatus = true;

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_on));
                }
            }
        });


        touchRelative2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recordButtonStatus){
                    recordButtonStatus = false;


                    activation.setRealtive2("activate");




                    notify++;
                    speech.stopListening();

//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
                }else{
                    ActivityCompat.requestPermissions
                            (VoiceSigninSecondary.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                    recordButtonStatus = true;

                    activation.setRealtive2("activate");


//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_on));
                }
            }
        });



        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener((RecognitionListener) this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);


        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);


        SpeechAssistant(getString(R.string.touchTheScreen));

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(VoiceSigninSecondary.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
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


    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle results) {

        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


        String touchRelative2 = activation.getRealtive2();

        if(touchRelative2 == "activate"){

            String text = matches.get(0);

            String[] arrOfText = text.split(" ", -2);

            List<String> list = new ArrayList<String>();
            ArrayList<String> names = new ArrayList<>();


            for (String a : arrOfText)
            {
                names.add(a);
                Log.d(TAG, "onResults: " + a);
            }

            mList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, names));


            String Customtext = text.replaceAll("[^\\d]", " ").trim().replaceAll(" +", "");

            Log.d(TAG, "onResults: CustomText : " + Customtext);
            Toast.makeText(this, "Poll no is : " +Customtext, Toast.LENGTH_SHORT).show();


            if(Customtext.length() > 4){


                cutOut = Customtext.substring(0,4);

            }
            else{
                cutOut = Customtext;
            }


            String pollingNoList = address.getPollingList();

            String[] arrayoFpoll = pollingNoList.split(" ", -2);


            ArrayList<String> polls = new ArrayList<>();


            for (String a : arrayoFpoll)
            {
                polls.add(a);
                Log.d(TAG, "onResults: " + a);
            }

            if(polls.contains("repeat") || polls.contains("दोहराना") || polls.contains("પુનરાવર્તન")|| polls.contains("पुन्हा करा")
                    || polls.contains("পুনরাবৃত্তি")|| polls.contains("పునరావృతం")|| polls.contains("மீண்டும்")|| polls.contains("ആവർത്തിച്ച്")){


                SpeechAssistant(sb.toString());

            }
            else{
                if(Integer.parseInt(cutOut) >= Integer.parseInt(polls.get(0)) &&
                        Integer.parseInt(cutOut) <= Integer.parseInt(polls.get(polls.size() - 1)))
                {

                    PollTheVote(cutOut);
//                String imei_no = getIMEINumber(VoiceSigninSecondary.this);

//                Toast.makeText(this, "The IMEI NO. IS  " + imei_no, Toast.LENGTH_SHORT).show();

                }

                else{

                    SpeechAssistant(getString(R.string.invalidPN));


                }

            }


            recordButtonStatus = false;



        }

        else{
            String text = matches.get(0);


            String[] arrOfText = text.split(" ", -2);

            List<String> list = new ArrayList<String>();
            ArrayList<String> names = new ArrayList<>();


            for (String a : arrOfText)
            {
                names.add(a);
                Log.d(TAG, "onResults: " + a);
            }

            mList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, names));


            String Customtext = text.replaceAll("[^\\d]", " ").trim().replaceAll(" +", "");

            Log.d(TAG, "onResults: CustomText : " + Customtext);

            Toast.makeText(this, "CustomText is : " +Customtext, Toast.LENGTH_SHORT).show();

            edtSigninPhone.setText(Customtext);

            signinPhone();

            recordButtonStatus = false;

        }



    }

    private void PollTheVote(String cutOut) {

        String imei_no = getIMEINumber(VoiceSigninSecondary.this);

        addVote(cutOut , imei_no , edtSigninPhone.getText().toString())
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
                                Toast.makeText(VoiceSigninSecondary.this, "details on error: " + details, Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();
                            }

                            // [START_EXCLUDE]
                            Log.w(TAG, "addNumbers:onFailure", e);
                            Toast.makeText(VoiceSigninSecondary.this, "an error occured , Voting UNSUCCESSFUL ! Try Again", Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();

                            return;
                            // [END_EXCLUDE]

                        }



                        // [START_EXCLUDE]
                        String result = task.getResult();


//                        progressDialog.dismiss();

                        Toast.makeText(VoiceSigninSecondary.this, "result answer = " +   result, Toast.LENGTH_SHORT).show();
                        // [END_EXCLUDE]

                        if(result == "Voted Successfully!" || result == "Already Voted" || result == null){
                            if(result == "Voted Successfully!"){
                                SpeechAssistant(getString(R.string.voteSuccess));
                            }
                            else if(result == "Already Voted"){
                                SpeechAssistant(getString(R.string.alreadyVoted));
                            }
                            Intent newIntent = new Intent(VoiceSigninSecondary.this, VoiceCheckingIMEI.class);
                            startActivity(newIntent);
                        }

//                                finish();
//                        Intent newIntent = new Intent(VoiceSigninSecondary.this, WelcomeActivity.class);
//                        startActivity(newIntent);

                    }
                });
            /*    .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VoiceSigninSecondary.this, "Voting is failed! Please Vote Again", Toast.LENGTH_SHORT).show();
            }
        });*/
        // [END call_add_numbers]

    }







    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }
    
    
    public void signInWithPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            progressBar.setVisibility(View.GONE);

                            progressDialog = new ProgressDialog(VoiceSigninSecondary.this);
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
                                                                    Toast.makeText(VoiceSigninSecondary.this, "details on error: " + details, Toast.LENGTH_SHORT).show();

                                                                }

                                                                // [START_EXCLUDE]
                                                                Log.w(TAG, "addNumbers:onFailure", e);
                                                                Toast.makeText(VoiceSigninSecondary.this, "an error occured", Toast.LENGTH_SHORT).show();
                                                                return;
                                                                // [END_EXCLUDE]

                                                            }



                                                            // [START_EXCLUDE]
                                                            String result = task.getResult();

                                                            Toast.makeText(VoiceSigninSecondary.this, "result answer = " +   result, Toast.LENGTH_SHORT).show();
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

                                        Intent intent = new Intent(VoiceSigninSecondary.this, PollActivity.class);
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

    public void signinPhone() {

        String number = edtSigninPhone.getText().toString().trim();

        if(number.isEmpty() || number.length() < 10) {


            if (number.isEmpty() || number.length() < 10) {

                SpeechAssistant(getString(R.string.validPhnoRequired));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        SpeechAssistant(getString(R.string.sayValidPhone));

                    }
                }, 4000);

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
//            startTimer();
            checkForPhoneNumber(number);

        }
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
//        CountDownTimer = new android.os.CountDownTimer( timeleftinmilli = 60000, 1000) {
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//                counter.setText("Please wait for " + millisUntilFinished/1000 +  " sec");
//            }
//
//            @Override
//            public void onFinish() {
//
//                edtSigninOTPFloatingText.setVisibility(View.INVISIBLE);
//                counter.setText("finished");
//                counter.setVisibility(View.GONE);
//                btnResendOtp.setVisibility(View.VISIBLE);
//                btnResendOtp.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        edtSigninOTPFloatingText.setVisibility(View.VISIBLE);
//                        btnResendOtp.setVisibility(View.GONE);
//                        btnSigninPhone.setVisibility(View.VISIBLE);
//                    }
//                });
//
//            }
//        }.start();

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

        /*progressDialog = new ProgressDialog(VoiceSigninSecondary.this);
        progressDialog.setMessage("Fetching...");
        progressDialog.setTitle("Voter ID Validation");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);*/



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




                        address.setNameList(nameList);
                        address.setPollingList(polling_noList);

                        ArrayList<String> mVoter_Details = new ArrayList<>();
                        String[] VoterDetailsStrings = voter_details.trim().split("_", -2);
                        for (String a : VoterDetailsStrings) {
                            mVoter_Details.add(a);
                        }

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
                        String[] arrOfpollnumbers = polling_noList.split(" ", -2);

//
//
//                        StringBuilder listConst = new StringBuilder();
//
//
//                        for(int j = 0 ; j <arrOfpollnumbers.length ; j++){
//
//                            for(int i = 0 ; i < arrOfpollnumbers.length ; i++){
//
//                                if(i>0){
//
//                                    listConst.append(",");
//                                }
//
//                                listConst.append(arrOfpollnumbers[j].charAt(i));
//
//                            }
//
//                            listConst.append(" ");
//
//                        }

//                        String poll = listConst.toString();
//                        String finalPoll = poll.substring(0 ,poll.length()-1);

                        String[] arrayOfpoll = polling_noList.split(" ", -2);



                        String Intro = getString(R.string.hello)+ name + "..." +  getString(R.string.intorAI) +
                                getString(R.string.yourVoterID) + voteridno +  getString(R.string.constituency) + mVoter_Details.size() +  getString(R.string.candidate) +
                                getString(R.string.last);

                        sb.append(Intro);
                        sb.append(getString(R.string.candies));

                        for (int i = 0; i < arrOfString.length; i++) {

                            sb.append(arrayOfpoll[i]  + "." + arrOfString[i]);
                            sb.append(". . .");

                        }

                        sb.append(getString(R.string.repeatHolder));

                        touchRelative.setVisibility(View.INVISIBLE);
                        touchRelative2.setVisibility(View.VISIBLE);

                        progressDialog.dismiss();

                        SpeechAssistant(sb.toString());




//                        Intent intent = new Intent(VoiceSigninSecondary.this, PollActivity.class);
//                        intent.putExtra("CandiNames" , nameList);
//                        intent.putExtra("CandiImages" , iurlList);
//                        intent.putExtra("CandiParty" , partyList);
//                        intent.putExtra("PartyImages" , partyURLlist);
//                        intent.putExtra("Polling_no" , polling_noList);
//                        intent.putExtra("VoterDetails" , voter_details);
//                        intent.putExtra("phoneNumber" , edtSigninPhone.getText().toString().trim());
//
//                        startActivity(intent);
//                        finish();

                        if(partyURLlist.equals("undefined") || nameList.equals("undefined") || iurlList.equals("undefined")
                                || partyList.equals("undefined") || polling_noList.equals("undefined"))
                        {

                            addNumbers(edtSigninPhone.getText().toString().trim() , "6" );

                        }

                        else
                        {
                            return  partyList;
                        }

                        return nameList;
                    }
                });
    }
    // [END function_add_numbers]





    private Task<String> addVote(String a , String b , String c)
    {
        Log.d(TAG, "addVote: Entered into addVote Contact");
//        progressDialog = new ProgressDialog(VoiceSigninSecondary.this);
//        progressDialog.setMessage("Fetching...");
//        progressDialog.setTitle("Voter ID Validation");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.show();
//        progressDialog.setCancelable(false);



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
                            Toast.makeText(VoiceSigninSecondary.this, details, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(VoiceSigninSecondary.this, ErrorActivity.class);
                            startActivity(intent);
                        }



                        return (String) result.get("Thankful");


                    }
                });
    }

}
