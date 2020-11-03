package com.highsecured.voteeasy.VoiceVoting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.highsecured.voteeasy.MainActivity;
import com.highsecured.voteeasy.R;
import com.highsecured.voteeasy.WelcomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoiceOptionActivity extends AppCompatActivity implements
        RecognitionListener {



    private static final String TAG = "MainActivity";

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private TextView returnedText;
    private Button recordButton;


    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "SpeechRecognizer";
    Boolean recordButtonStatus;
    private ListView mList;

    private RelativeLayout touchLinear;


    //speaking
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private TextToSpeech mTTS;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_activity_options);


        touchLinear = findViewById(R.id.touchOption);
        recordButtonStatus = false;

        mList = (ListView) findViewById(R.id.list);


        Intent intent = getIntent() ;

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        Intent nice = getIntent();
        final String transported = nice.getStringExtra("langi");
        Log.d(TAG, "onCreate: value of transported " + transported);


        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                transported);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                transported);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
                transported);

        touchLinear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(recordButtonStatus){
                    recordButtonStatus = false;

                    speech.stopListening();
//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
                }else{
                    ActivityCompat.requestPermissions
                            (VoiceOptionActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                    recordButtonStatus = true;
//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_on));
                }
            }


        });


        //speaking from mainactivity

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
                            //                        mButtonSpeak.setEnabled(true);
                            speak();
                        }
                    }
                    else {
                        Log.e("TTS", "Initialization failed");
                    }
                }
            });
        }



       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(MainActivity.this, ListMaker.class);
                startActivity(intent);

            }
        }, 8000);*/




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(VoiceOptionActivity.this, "Permission Denied!", Toast
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
        recordButtonStatus = false;
//        recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));



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

        Log.d(TAG, "onResults: Very important Arraylist is" + matches);

        String text = matches.get(0);

        Log.d(TAG, "onResults: Very important text = matches.get(0) is " + text);



        String[] arrOfText = text.split(" ", -2);

        Log.d(TAG, "onResults: Very important String[] is " + arrOfText);

        List<String> list = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<>();


        for (String a : arrOfText){
//            names.add("\"" + a + "\""
            names.add(a);
            Log.d(TAG, "onResults: " + a);
        }

        mList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, names));

        if (names.contains("u")
                || names.contains("USSD") || names.contains("US")  ||names.contains("YOU") || names.contains("you") ||names.contains("யுஎஸ்எஸ்டி") ||
        names.contains("யு") || names.contains("எஸ்")  || names.contains("டி") ||names.contains("यूएसएसडी")
|| names.contains("यू") || names.contains("एस") || names.contains("डी") ||names.contains("യുഎസ്എസ്ഡി") ){

            Toast.makeText(this, "Dial *123# to vote", Toast.LENGTH_SHORT).show();

        }

        else if(names.contains("voice") || names.contains("Voice") ||names.contains("voice voting")
        || names.contains("குரல்") || names.contains("వాయిస్") ||names.contains("व्हॉईस") ||
         names.contains("વોઇસ") ||names.contains("ভয়েস")
        || names.contains("आवाज") ||names.contains("യുഎസ്എസ്ഡി")){

            Intent intent = new Intent(VoiceOptionActivity.this, VoiceCheckingIMEI.class);
            startActivity(intent);

        }

        else if(names.contains("manual") || names.contains("Manual") ||names.contains("manual voting")
                || names.contains("മാനുവൽ") ||names.contains("मैनुअल")
                || names.contains("મેન્યુઅલ") ||names.contains("ম্যানুয়াল")
                || names.contains("మాన్యువల్") ||names.contains("கையேடு")){

            Intent intent = new Intent(VoiceOptionActivity.this, WelcomeActivity.class);
            startActivity(intent);

        }

        else
        {

            informationMenu();
        }

        recordButtonStatus = false;
//        recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
    }

    private void informationMenu() {
        Toast.makeText(this, "Found!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(VoiceOptionActivity.this, MainActivity.class );
        startActivity(intent);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "RMS Changed: " + rmsdB);
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

        Getset textuse = new Getset();




            String text =  getString(R.string.options) + getString(R.string.select);
            float pitch = (float) mSeekBarPitch.getProgress() / 50;
            if (pitch < 0.1) pitch = 0.1f;
            float speed = (float) mSeekBarSpeed.getProgress() / 50;
            if (speed < 0.1) speed = 0.1f;

            mTTS.setPitch(pitch);
            mTTS.setSpeechRate(speed);

            mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);

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



}
