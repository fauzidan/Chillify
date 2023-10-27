package com.example.chillify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLogin extends AppCompatActivity implements View.OnClickListener{

    EditText login_email, login_password;
    Button login_button;
    TextView login_register;
    private FirebaseAuth mAuth;

    String currentUser, currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_button);
        login_register = findViewById(R.id.login_register);
        //================================================

        mAuth = FirebaseAuth.getInstance();

        login_button.setOnClickListener(this);
        login_register.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        login_email.getText().clear();
        login_password.getText().clear();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            getDisplayName();

            Intent login = new Intent(this, user_dashboard.class);
            login.putExtra("nama", currentUser);
            login.putExtra("uid", currentUid);

            Toast.makeText(this, "Anda Sudah Login, " + currentUser, Toast.LENGTH_SHORT).show();
            startActivity(login);
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == login_button.getId()){

            String email = login_email.getText().toString();
            String password = login_password.getText().toString();

            if(email.length() == 0){
                login_email.setError("Email Harus Diisi");
                return;
            }
            if(password.length() == 0){
                login_password.setError("Password Harus Diisi Dan Minimal 6 Karakter!");
                return;
            }

            if(email.equals("admin") && password.equals("admin1111")){
                currentUser = "Admin";
                currentUid = "Admin";
                adminLogin();
            }else{
                login(email, password);
            }

        }else if(v.getId() == login_register.getId()){
            Intent register = new Intent(this, user_register.class);
            startActivity(register);
        }
    }

    public void login(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            getDisplayName();
                            Toast.makeText(UserLogin.this, "Berhasil Masuk, Selamat Datang " + currentUser, Toast.LENGTH_SHORT).show();

                            Intent login = new Intent(UserLogin.this, user_dashboard.class);
                            login.putExtra("nama", currentUser);
                            login.putExtra("uid", currentUid);
                            startActivity(login);
                        }else{
                            Toast.makeText(UserLogin.this, "Masuk Gagal Karena " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void adminLogin(){
        Intent login = new Intent(this, user_dashboard.class);
        login.putExtra("nama", currentUser);
        login.putExtra("uid", currentUid);

        Toast.makeText(UserLogin.this, "ADMIN : DEV & TEST MODE ONLY ", Toast.LENGTH_SHORT).show();
        startActivity(login);
    }

    public void getDisplayName(){
        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null){
            currentUser = user.getDisplayName();
            currentUid = user.getUid();
        }

    }

}