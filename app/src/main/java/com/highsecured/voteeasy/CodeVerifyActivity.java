package com.highsecured.voteeasy;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class CodeVerifyActivity extends AppCompatActivity {

    EditText first, second, third, fourth, fifth, sixth;
    FirebaseAuth mAuth;
    String verifycode, name, phone, type;
    private ProgressBar pgsBar;

    TextView verifycounter;

    private static final String TAG = "CodeVerifyActivity";
    private android.os.CountDownTimer CountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codeverify);

        mAuth = FirebaseAuth.getInstance();

        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        fourth = findViewById(R.id.fourth);
        fifth = findViewById(R.id.fifth);
        sixth = findViewById(R.id.sixth);
        EditText otp_editText = findViewById(R.id.otp_editText);

        verifycounter = findViewById(R.id.verifycounter);

        pgsBar = findViewById(R.id.pBar);

        Bundle bundle = getIntent().getExtras();

        verifycode = bundle.getString("verifycode");
        name = bundle.getString("name1");
        phone = bundle.getString("phone1");
        type = bundle.getString("type");




        startTimer();


        first.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub
                if(first.getText().toString().length()==1)
                {
                    second.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        //backspace (deleting character)
        first.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    first.setText("");
                }
                return false;
            }
        });

        second.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub
                if(second.getText().toString().length()==1)
                {
                    third.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        //backspace (deleting character)
        second.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    second.setText("");
                    first.requestFocus();
                }
                return false;
            }
        });

        third.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub
                if(third.getText().toString().length()==1)
                {
                    fourth.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        //backspace (deleting character)
        third.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    third.setText("");
                    second.requestFocus();
                }
                return false;
            }
        });

        fourth.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub
                if(fourth.getText().toString().length()==1)
                {
                    fifth.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        //backspace (deleting character)
        fourth.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    fourth.setText("");
                    third.requestFocus();
                }
                return false;
            }
        });

        fifth.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub
                if(fifth.getText().toString().length()==1)
                {
                    sixth.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        //backspace (deleting character)
        fifth.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    fifth.setText("");
                    fourth.requestFocus();
                }
                return false;
            }
        });

        //backspace (deleting character)
        sixth.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    sixth.setText("");
                    fifth.requestFocus();
                }
                return false;
            }
        });
    }

    //verify text clicked
    public void verify(View view) {

        int one = first.getText().toString().length();
        int two = second.getText().toString().length();
        int three = third.getText().toString().length();
        int four = fourth.getText().toString().length();
        int five = fifth.getText().toString().length();
        int six = sixth.getText().toString().length();

        if(one != 1 && two != 1 && three != 1 && four != 1 && five != 1 && six != 1){
            sixth.setError("Valid number is required");
            first.requestFocus();
            return;
        }

        String input_code = first.getText().toString() + second.getText().toString() +
                third.getText().toString() + fourth.getText().toString() +
                fifth.getText().toString() + sixth.getText().toString();

        /*if(verifycode != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifycode, auto_input);
            signInWithPhone(credential);
        }*/

        if(verifycode != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifycode, input_code);
            signInWithPhone(credential);
        }


    }


    public void signInWithPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pgsBar.setVisibility(View.VISIBLE);

                        if (task.isSuccessful()) {

                            User user = new User(phone, name);


                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pgsBar.setVisibility(View.GONE);


                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot != null) {

                                                        Intent intent = new Intent(CodeVerifyActivity.this, MainActivity.class);
                                                        if(CountDownTimer != null){
                                                            CountDownTimer.cancel();
                                                            CountDownTimer = null;
                                                        }
                                                        startActivity(intent);
                                                        finish();

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

                                                        pgsBar.setVisibility(View.GONE);
                                                        return;
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }

                                            });
                                        }
                                    });

                        } else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            pgsBar.setVisibility(View.GONE);

                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.activity_custom_toast,
                                    (ViewGroup) findViewById(R.id.custom_toast_container));

                            TextView text = (TextView) layout.findViewById(R.id.text);
                            text.setText("Invalid OTP");

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();

                        }
                        else {
                            sixth.setError("Valid OTP is required");
                        }
                    }
                });
    }

    private void startTimer() {
        CountDownTimer = new CountDownTimer( 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                verifycounter.setText("Please wait for " + millisUntilFinished/1000 +  " sec");
            }


            @Override
            public void onFinish() {

               Intent verify = new Intent(CodeVerifyActivity.this , InfoActivity.class);
               startActivity(verify);

            }
        }.start();

    }

/*    public void needHelp(View view) {
        Intent intent = new Intent(getApplicationContext(), Needhelp.class);
        startActivity(intent);
    }*/
}