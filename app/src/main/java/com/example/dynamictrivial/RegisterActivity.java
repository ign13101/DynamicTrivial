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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText username_edittext, mail_edittext, password_edittext, password_repeat_edittext;
    Button login_button;
    MediaPlayer mp;
    TextView text_view_register;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    String username, email, password, co_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        TextView registerLink = findViewById(R.id.register_textview);
        mp = MediaPlayer.create(this, R.raw.click_sound);
        username_edittext = findViewById(R.id.username_edittext);
        mail_edittext = findViewById(R.id.mail_edittext);
        password_edittext = findViewById(R.id.password_edittext);
        password_repeat_edittext = findViewById(R.id.password_repeat_edittext);
        login_button = findViewById(R.id.login_button);
        text_view_register = findViewById(R.id.register_textview);

        //        Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserData");
        text_view_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                if (!validateUsername() | !validateEmail() | !validatePassword()) {
                    return;
                }
                if (password.equals(co_password)) {

                    mAuth.createUserWithEmailAndPassword(email, password).
                            addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        UserData data = new UserData(username, email);
                                        FirebaseDatabase.getInstance().getReference("UserData")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(RegisterActivity.this, "Registro completado con éxito", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Comprueba el email o la contraseña", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

        });

        //Cosas de Nachito
        registerLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mp.start();
                // do something when the TextView is clicked
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean validateUsername() {
        username = username_edittext.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(RegisterActivity.this, "Introduce tu usuario", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail() {
        email = mail_edittext.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Introduce tu email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegisterActivity.this, "Por favor introduce un email válido", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword() {
        password = password_edittext.getText().toString().trim();
        co_password = password_repeat_edittext.getText().toString().toLowerCase();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Introduce tu contraseña", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(co_password)) {
            Toast.makeText(RegisterActivity.this, "Repite la contraseña", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() == 6) {
            Toast.makeText(RegisterActivity.this, "La contraseña es demasiado corta", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
/*
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
*/

}