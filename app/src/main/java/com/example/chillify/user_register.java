package com.example.chillify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class user_register extends AppCompatActivity implements View.OnClickListener{

    EditText regis_nama, regis_email, regis_password;
    Button regis_button;
    TextView regis_login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        regis_nama = findViewById(R.id.regis_nama);
        regis_email = findViewById(R.id.regis_email);
        regis_password = findViewById(R.id.regis_password);
        regis_button = findViewById(R.id.regis_button);
        regis_login = findViewById(R.id.regis_login);
        //================================================

        mAuth = FirebaseAuth.getInstance();

        regis_button.setOnClickListener(this);
        regis_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == regis_button.getId()){

            String nama = regis_nama.getText().toString();
            String email = regis_email.getText().toString();
            String password = regis_password.getText().toString();

            if(nama.length() == 0){
                regis_nama.setError("Nama Harus Diisi!");
                return;
            }
            if(email.length() == 0){
                regis_email.setError("Email Harus Diisi");
                return;
            }
            if(password.length() < 6){
                regis_password.setError("Password Harus Diisi Dan Lebih Dari 6 Karakter");
                return;
            }

            registration(nama, email, password);

        }else if(v.getId() == regis_login.getId()){
            finish();
        }

    }

    public void registration(String nama, String email, String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(user_register.this, "Pengguna Berhasil Terdaftar!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(user_register.this, "Pengguna Gagal Didaftarkan Karena " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest addName = new UserProfileChangeRequest.Builder().setDisplayName(nama).build();
                        user.updateProfile(addName);

                        mAuth.signOut();
                        finish();
                    }
                });
    }
}