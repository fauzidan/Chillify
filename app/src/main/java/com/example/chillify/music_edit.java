package com.example.chillify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class music_edit extends AppCompatActivity implements View.OnClickListener{

    ImageView edit_img_add;
    EditText edit_title, edit_band, edit_link;
    Button edit_button, delete_button;
    String currentUser, currentId, uploadLink, cloudName;
    TextView upload_btn_edit;
    final int code = 54;
    Uri filePath;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_edit);

        edit_img_add = findViewById(R.id.edit_img_add);
        edit_title = findViewById(R.id.edit_title);
        edit_band = findViewById(R.id.edit_band);
        edit_link = findViewById(R.id.edit_link);
        edit_button = findViewById(R.id.edit_button);
        delete_button = findViewById(R.id.delete_button);
        upload_btn_edit = findViewById(R.id.upload_btn_edit);
        //===================================================

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        edit_button.setOnClickListener(this);
        delete_button.setOnClickListener(this);
        upload_btn_edit.setOnClickListener(this);

        Intent edit = getIntent();
        currentId = edit.getStringExtra("id");
        currentUser = edit.getStringExtra("user");

        fillForm();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == edit_button.getId()){

            if(edit_title.getText().toString().length() == 0){
                edit_title.setError("Judul Harus Diisi");
                return;
            }
            if(edit_band.getText().toString().length() == 0){
                edit_band.setError("Band/Penyanyi Harus Diisi");
                return;
            }
            if(edit_link.getText().toString().length() == 0){
                edit_link.setError("Link Musik Harus Diisi");
                return;
            }

            uploadImg();
            Toast.makeText(this, "Musik Telah Berhasil Diubah", Toast.LENGTH_SHORT).show();

            finish();

        }else if(v.getId() == delete_button.getId()){

            delete();
            Toast.makeText(this, "Musik Telah Dihapus", Toast.LENGTH_SHORT).show();

            finish();

        }else if(v.getId() == upload_btn_edit.getId()){

            openFiles();

        }

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

                    if(uploadLink.length() != 0){
                        deleteImg();
                    }

                    StorageReference ref = storageReference.child(currentUser + "/" + cloudName);
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadLink = uri.toString();
                            Log.d("test upload", "msk " + uploadLink);
                            update();
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
            update();
        }
    }

    public void update(){

        DatabaseReference myRef = database.getReference("Chillify App").child(currentUser).child(currentId);

        myRef.child("judul").setValue(edit_title.getText().toString());
        myRef.child("band").setValue(edit_band.getText().toString());
        myRef.child("link").setValue(edit_link.getText().toString());
        myRef.child("img_url").setValue(uploadLink);

    }

    public void deleteImg(){

        String divider = "%2F";
        String endDivider = "?alt";
        int startIndex = uploadLink.indexOf(divider) + 3;
        int endIndex = uploadLink.indexOf(endDivider);

        String oldCloudName = uploadLink.substring(startIndex, endIndex);
        StorageReference deleteRef = storageReference.child(currentUser + "/" + oldCloudName);
        Log.d("test replace", " " + currentUser + "/" + oldCloudName);

        deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("test replace", "berhasil");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("test replace", e.getMessage());
            }
        });

    }

    public void delete(){
        DatabaseReference myRef = database.getReference("Chillify App").child(currentUser).child(currentId);
        myRef.removeValue();

        if(uploadLink.length() != 0){
            deleteImg();
        }
    }

    public void fillForm(){

        DatabaseReference myRef = database.getReference("Chillify App").child(currentUser).child(currentId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, music> value = (Map<String, music>) dataSnapshot.getValue();
                Log.d("ingfo_test", "Value is: " + value);

                music resMusic = dataSnapshot.getValue(music.class);
                if(resMusic == null){
                    finish();
                }else {
                    edit_title.setText(resMusic.getJudul());
                    edit_band.setText(resMusic.getBand());
                    edit_link.setText(resMusic.getLink());
                    uploadLink = resMusic.getImg_url();

                    if (uploadLink.length() != 0){
                        Glide.with(getApplicationContext()).load(uploadLink).into(edit_img_add);
                        Log.d("ingfo_test", "loaded");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("test edit", "Failed to read value.", error.toException());
            }
        });
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
                edit_img_add.setImageBitmap(improter);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}