package com.fatecscs.ezpaymercado.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.fatecscs.ezpaymercado.Helper.ConfiguracaoFireBase;
import com.fatecscs.ezpaymercado.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        validarLogin();
    }

    public void validarLogin(){
        auth = ConfiguracaoFireBase.getFirebaseAutenticacao();
        auth.signInWithEmailAndPassword(
                "admin@admin.com",
                "ezpayadmin2020"
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Admin autenticado", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Falha na autenticacao", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
