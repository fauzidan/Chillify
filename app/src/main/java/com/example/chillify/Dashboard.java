package com.example.chillify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class Dashboard extends AppCompatActivity {

    ImageView image_prev, prev_show;
    Button select, upload, show;
    Uri path, uploaded;
    String cloudPath;

    final int code = 22;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //**WARNING!! INI ACTIVITY SANDBOX UNTUK TESTING FIREBASE!!**

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("Chillify App");
//
//        myRef.child("testing").setValue("aja");
//        myRef.child("testing2").setValue("ajass");

        image_prev = findViewById(R.id.image_prev);
        prev_show = findViewById(R.id.prev_show);
        select = findViewById(R.id.select);
        upload = findViewById(R.id.upload);
        show = findViewById(R.id.show);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
    }

    private void show(){
        StorageReference ref = storageReference.child("images/" + cloudPath);

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("test upload", uri.toString());
                uploaded = uri;

                Glide.with(Dashboard.this).load(uploaded.toString()).into(prev_show);
            }
        });
    };

    private void select(){
        Intent sel = new Intent();
        sel.setType("image/*");
        sel.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(sel, code);
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == code && resultCode == RESULT_OK && data!=null && data.getData()!=null){

            path = data.getData();

            try {
                Bitmap planter = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                image_prev.setImageBitmap(planter);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void upload(){
        if(path != null){
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            cloudPath = UUID.randomUUID().toString();

            StorageReference ref = storageReference.child("images/" + cloudPath);

            ref.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Log.d("test upload", "berhasil");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Log.d("test upload", "gagal upload");
                    Log.d("test upload", e.getMessage());
                }
            });
        }
    };
}