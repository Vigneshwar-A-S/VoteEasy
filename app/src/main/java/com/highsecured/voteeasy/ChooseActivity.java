package com.highsecured.voteeasy;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.highsecured.voteeasy.VoiceVoting.Getset;
import com.highsecured.voteeasy.VoiceVoting.VoiceCheckingIMEI;
import com.highsecured.voteeasy.VoiceVoting.VoiceMainActivity;
import com.highsecured.voteeasy.VoiceVoting.VoiceOptionActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChooseActivity extends AppCompatActivity implements RecognitionListener {

    private static final String TAG = "ChooseActivity";

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private TextView returnedText;
    private Button recordButton;


    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "SpeechRecognizer";
    Boolean recordButtonStatus;
    private ListView mList;

    private RelativeLayout touchRelative;


    //speaking
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private TextToSpeech mTTS;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;



    RelativeLayout eng, tam, tel, hin, mal, guj, mar, beng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LoadLocale();
        setContentView(R.layout.activity_choose_languagae);


        
        eng = findViewById(R.id.btn_eng);       
        tam = findViewById(R.id.btn_tamil);       
        hin = findViewById(R.id.btn_hindi);       
        tel = findViewById(R.id.btn_telugu);       
        mal = findViewById(R.id.btn_mal);
        mar = findViewById(R.id.btn_marati);
        guj = findViewById(R.id.btn_guj);       
        beng = findViewById(R.id.btn_beng);


        

        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setLocale("eng");
            }
        });
        tam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ta");
            }
        });
        hin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("hi");
            }
        });
        tel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("te");
            }
        });
        guj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("gu");
            }
        });

        mar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("mr");
            }
        });
        beng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("bn");
            }
        });
        mal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ml");
            }
        });





        touchRelative = findViewById(R.id.touchLayout);
        recordButtonStatus = false;

        mList = (ListView) findViewById(R.id.list);


        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);


        touchRelative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(recordButtonStatus){
                    recordButtonStatus = false;

                    speech.stopListening();
//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
                }else{
                    ActivityCompat.requestPermissions
                            (ChooseActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                    recordButtonStatus = true;
//                    recordButton.setBackground(getDrawable(R.drawable.microphone_button_on));
                }
            }


        });

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



    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

//        save data to shared Preference
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang" , lang);
        editor.apply();
    }

    public void LoadLocale(String langi){


        SharedPreferences prefs = getSharedPreferences("settings" , ChooseActivity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(langi);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(ChooseActivity.this, "Permission Denied!", Toast
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

        if (names.contains("tamil") || names.contains("Tamil")
                || names.contains("mill") || names.contains("ta")  ||names.contains("mil") || names.contains("you")){

            LoadLocale("ta");
            Intent intent = new Intent(ChooseActivity.this, VoiceMainActivity.class);
            intent.putExtra("stt" , "ta");
            startActivity(intent);


            Toast.makeText(this, "Choosen Tamil language", Toast.LENGTH_SHORT).show();

        }

        else if(names.contains("English") || names.contains("eng") ||names.contains("lish") || names.contains("english")){
            LoadLocale("en");

            Intent intent = new Intent(ChooseActivity.this, VoiceMainActivity.class);
            intent.putExtra("stt" , "en");
            startActivity(intent);

            Toast.makeText(this, "Choosen English language", Toast.LENGTH_SHORT).show();

        }

        else if(names.contains("Hindi") || names.contains("hindi") ||names.contains("hin") || names.contains("the")){
            LoadLocale("hi");
            Intent intent = new Intent(ChooseActivity.this, VoiceMainActivity.class);
            intent.putExtra("stt" , "hi");

            startActivity(intent);

            Toast.makeText(this, "Choosen Hindi language", Toast.LENGTH_SHORT).show();

        }

        else if(names.contains("Telugu") || names.contains("telugu") ||names.contains("telungu") || names.contains("telu")){
            LoadLocale("te");
            Intent intent = new Intent(ChooseActivity.this, VoiceMainActivity.class);
            intent.putExtra("stt" , "te");

            startActivity(intent);

            Toast.makeText(this, "Choosen Hindi language", Toast.LENGTH_SHORT).show();

        }

        else if(names.contains("Marati") || names.contains("marati") ||names.contains("marathi") || names.contains("mara")){
            LoadLocale("mr");
            Intent intent = new Intent(ChooseActivity.this, VoiceMainActivity.class);
            intent.putExtra("stt" , "mr");

            startActivity(intent);

            Toast.makeText(this, "Choosen Marati language", Toast.LENGTH_SHORT).show();

        }

        else if(names.contains("Bengali") || names.contains("bengali") ||names.contains("beng") || names.contains("kali")){
            LoadLocale("bn");
            Intent intent = new Intent(ChooseActivity.this, VoiceMainActivity.class);
            intent.putExtra("stt" , "bn");

            startActivity(intent);

            Toast.makeText(this, "Choosen Bengali language", Toast.LENGTH_SHORT).show();

        }

        else if(names.contains("Gujarati") || names.contains("gujarati") ||names.contains("Gujarathi") || names.contains("gujarathi")){
            LoadLocale("gu");
            Intent intent = new Intent(ChooseActivity.this, VoiceMainActivity.class);
            intent.putExtra("stt" , "gu");
            startActivity(intent);

            Toast.makeText(this, "Choosen Gujarati language", Toast.LENGTH_SHORT).show();

        }
        else if(names.contains("Malayalam") || names.contains("malayalam") ||names.contains("mala") || names.contains("lamp")){
            LoadLocale("ml");
            Intent intent = new Intent(ChooseActivity.this, VoiceMainActivity.class);
            intent.putExtra("stt" , "ml");
            startActivity(intent);

            Toast.makeText(this, "Choosen Malayalam language", Toast.LENGTH_SHORT).show();

        }


        else
        {

            Toast.makeText(this, "Select Valid Language", Toast.LENGTH_SHORT).show();
        }

        recordButtonStatus = false;
//        recordButton.setBackground(getDrawable(R.drawable.microphone_button_off));
    }

    private void informationMenu() {
        Toast.makeText(this, "Found!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ChooseActivity.this, MainActivity.class );
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



        String text = "Hi, .. Please Select your Language ... You can select .. or touch the screen ..  to say ...English .. Tamil .. Hindi .. Gujarati .. Marati .. Bengali .. Telugu .. Malayaalam. ";
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
