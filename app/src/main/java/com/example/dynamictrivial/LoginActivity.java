package com.example.dynamictrivial;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText txtemail, txtpassoword;
    private Button login_btn;
    MediaPlayer mp;
    FirebaseAuth mAuth;
    String loginemail, loginpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mp = MediaPlayer.create(this, R.raw.click_sound);
        txtemail = findViewById(R.id.email_login_edittext);
        txtpassoword = findViewById(R.id.password_login_edittext);
        login_btn = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                if (!validateEmail() | !validatePassword()) {
                    return;
                }
                mAuth.signInWithEmailAndPassword(loginemail, loginpassword).addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Fallo al iniciar sesión", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            }
        });

        TextView loginLink = findViewById(R.id.login_textview);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validateEmail() {
        loginemail = txtemail.getText().toString().trim();
        if (TextUtils.isEmpty(loginemail)) {
            Toast.makeText(LoginActivity.this, "Introduce tu email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(loginemail).matches()) {
            Toast.makeText(LoginActivity.this, "Por favor introduzca un email válido", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword() {
        loginpassword = txtpassoword.getText().toString().trim();
        if (TextUtils.isEmpty(loginpassword)) {
            Toast.makeText(LoginActivity.this, "Introduce la contraseña", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

}