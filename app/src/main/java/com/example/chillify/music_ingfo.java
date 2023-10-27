package com.example.chillify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chillify.model.music;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class music_ingfo extends AppCompatActivity implements View.OnClickListener{

    ImageView music_image;
    TextView m_title, m_band;
    Button btn_play;
    View btn_album, btn_edit;
    String currentId, currentUser = "Admin", playUrl = "#", cloudName = null, uploaded;
    Uri uploadedFile;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_ingfo);

        music_image = findViewById(R.id.music_image);
        m_title = findViewById(R.id.m_title);
        m_band = findViewById(R.id.m_band);
        btn_play = findViewById(R.id.btn_play);
        btn_album = findViewById(R.id.btn_album);
        btn_edit = findViewById(R.id.btn_edit);

        //================================================

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            currentUser = user.getUid();
            Log.d("ingfo_test", "uid : " + currentUser);
        }

        getIngfo();

        btn_play.setOnClickListener(this);
        btn_album.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == btn_play.getId()){

            if (!playUrl.startsWith("http://") && !playUrl.startsWith("https://"))
                playUrl = "https://" + playUrl;

            Intent play = new Intent(Intent.ACTION_VIEW, Uri.parse(playUrl));
            startActivity(play);

        }else if(v.getId() == btn_album.getId()){

            finish();

        }else if(v.getId() == btn_edit.getId()){

            Intent edit = new Intent(this, music_edit.class);
            edit.putExtra("user", currentUser);
            edit.putExtra("id", currentId);
            startActivity(edit);
        }

    }

    public void getIngfo(){

        Intent info = getIntent();
        currentId = info.getStringExtra("id");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chillify App").child(currentUser).child(currentId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, music> value = (Map<String, music>) dataSnapshot.getValue();
                Log.d("ingfo_test", "Value is: " + value);

                music resMusic = dataSnapshot.getValue(music.class);

                if(resMusic == null){
                    finish();
                }else{
                    m_title.setText(resMusic.getJudul());
                    m_band.setText(resMusic.getBand());
                    playUrl = resMusic.getLink();
                    uploaded = resMusic.getImg_url();
                    loadImg();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("ingfo_test", "Failed to read value.", error.toException());
            }
        });
    }

    public void loadImg(){
        Log.d("ingfo_test", "konten : " + uploaded);
        if (uploaded.length() != 0){
            Glide.with(getApplicationContext()).load(uploaded).into(music_image);
            Log.d("ingfo_test", "loaded");
        }
    }

}