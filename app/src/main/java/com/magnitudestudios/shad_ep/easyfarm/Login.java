package com.magnitudestudios.shad_ep.easyfarm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.magnitudestudios.shad_ep.easyfarm.MainActivity;


public class Login extends AppCompatActivity {

    private ImageButton loginButton, signUpButtonL;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private EditText emailEdit, passwordEdit;
    private Button forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        loginButton = (ImageButton) findViewById(R.id.login_button);
        signUpButtonL = (ImageButton) findViewById(R.id.sign_up_button);
        emailEdit = (EditText) findViewById(R.id.login_email_edit);
        passwordEdit = (EditText) findViewById(R.id.login_password_edit);
        progressBar = (ProgressBar) findViewById(R.id.loginloading);
        forgotPassword = (Button) findViewById(R.id.forgotPassword);

        loginAnimation(loginButton);
        signUpButtonL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = emailEdit.getText().toString();
                if (TextUtils.isEmpty(emailAddress)) {
                    Toast.makeText(Login.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Email Sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Login.this, "Email Failed", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);

                            }
                        });
            }
        });
    }
    public void loginAnimation(final ImageButton button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(button, "scaleX", 0.9f);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 0.9f);
                        scaleDownX.setDuration(0);
                        scaleDownY.setDuration(0);

                        AnimatorSet scaleDown = new AnimatorSet();
                        scaleDown.play(scaleDownX).with(scaleDownY);

                        scaleDown.start();

                        return true;
                    case MotionEvent.ACTION_UP:
                        ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(button, "scaleX", 1f);
                        ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(button, "scaleY", 1f);
                        scaleDownX2.setDuration(0);
                        scaleDownY2.setDuration(0);

                        AnimatorSet scaleDown2 = new AnimatorSet();
                        scaleDown2.play(scaleDownX2).with(scaleDownY2);

                        scaleDown2.start();
                        String email = emailEdit.getText().toString();
                        final String password = passwordEdit.getText().toString();

                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(getApplicationContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        if (TextUtils.isEmpty(password)) {
                            Toast.makeText(getApplicationContext(), "Please enter a password!", Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        if (!email.contains("@")) {
                            emailEdit.setError("Must be a valid email");
                            return true;
                        }

                        progressBar.setVisibility(View.VISIBLE);

                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        passwordEdit.setError("Password length must be greater than 6 characters");
                                    } else {
                                        Toast.makeText(Login.this, "Login failed", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                }

                return true;
            }
        });
    }
}