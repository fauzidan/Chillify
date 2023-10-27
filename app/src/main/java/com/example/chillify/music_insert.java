package com.example.chillify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chillify.model.music;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class music_insert extends AppCompatActivity implements View.OnClickListener{

    EditText music_title, music_band, music_link;
    Button music_add;
    ImageView m_img_add;
    TextView upload_button;

    String currentUser, cloudName, uploadLink;
    Uri filePath, uploadedPath;

    FirebaseStorage storage;
    StorageReference storageReference;

    final int code = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_insert);

        music_title = findViewById(R.id.music_title);
        music_band = findViewById(R.id.music_band);
        music_link = findViewById(R.id.music_link);
        music_add = findViewById(R.id.music_add);
        m_img_add = findViewById(R.id.m_img_add);
        upload_button = findViewById(R.id.upload_button);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent addMusic = getIntent();
        currentUser = addMusic.getStringExtra("user");

        music_add.setOnClickListener(this);
        upload_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == music_add.getId()){

            String judul = music_title.getText().toString();
            String band = music_band.getText().toString();
            String link = music_link.getText().toString();

            if(judul.length() == 0){
                music_title.setError("Judul Harus Diisi");
                return;
            }
            if(band.length() == 0){
                music_band.setError("Band/Penyanyi Harus Diisi");
                return;
            }
            if(link.length() == 0){
                music_link.setError("Link Musik Harus Diisi");
                return;
            }

            uploadImg();
            finish();

        }else if(v.getId() == upload_button.getId()){

            openFiles();

        }

    }

    public void pushMusic(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chillify App").child(currentUser);

        String judul = music_title.getText().toString();
        String band = music_band.getText().toString();
        String link = music_link.getText().toString();

        Log.d("test upload", "msk akhir " + uploadLink);
        myRef.push().setValue(new music("", judul, band, link, uploadLink));
        Toast.makeText(this, "Musik Telah Ditambahkan!", Toast.LENGTH_SHORT).show();
    }

    public void getUploadUrl(){

        StorageReference ref = storageReference.child(currentUser + "/" + cloudName);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                uploadLink = uri.toString();
//                Log.d("test upload", "msk " + uploadLink);
            }
        });
    }

    public void uploadImg(){
        if(filePath != null){
//            ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Mengunggah Foto");
//            progressDialog.show();

            cloudName = UUID.randomUUID().toString();
            StorageReference ref = storageReference.child(currentUser + "/" + cloudName);

            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    progressDialog.dismiss();
                    Log.d("test upload", "berhasil");

                    StorageReference ref = storageReference.child(currentUser + "/" + cloudName);
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadLink = uri.toString();
                            Log.d("test upload", "msk " + uploadLink);
                            pushMusic();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    progressDialog.dismiss();
                    Log.d("test upload", "gagal upload");
                    Log.d("test upload", e.getMessage());
                }
            });
        }else {
            uploadLink = "";
            pushMusic();
        }
    }

    public void openFiles(){
        Intent openFile = new Intent();
        openFile.setType("image/*");
        openFile.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(openFile, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == code && resultCode == RESULT_OK && data!=null && data.getData()!=null){

            filePath = data.getData();

            try {
                Bitmap improter = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                m_img_add.setImageBitmap(improter);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}