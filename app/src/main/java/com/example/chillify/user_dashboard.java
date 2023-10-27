package com.example.chillify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chillify.adapter.MusicAdapter;
import com.example.chillify.model.music;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class user_dashboard extends AppCompatActivity implements View.OnClickListener{

    View dash_music_add;
    TextView user_name, signout_button;
    RecyclerView recyclerView;
    List<music> data;
    MusicAdapter adapter;
    String currentUser = "Admin";
    String currentDisplay = "Admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        signout_button = findViewById(R.id.signout_button);
        dash_music_add = findViewById(R.id.dash_music_add);
        user_name = findViewById(R.id.user_name);
        //================================================

        Intent login = getIntent();
        currentUser = login.getStringExtra("uid");
        currentDisplay = login.getStringExtra("nama");
        user_name.setText(currentDisplay);

        signout_button.setOnClickListener(this);
        dash_music_add.setOnClickListener(this);

        loadList();
        //recycle trigger
        reload();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == dash_music_add.getId()){
            Intent addMusic = new Intent(this, music_insert.class);
            addMusic.putExtra("user", currentUser);
            startActivity(addMusic);
        }else if(v.getId() == signout_button.getId()){
            if(!currentUser.equals("Admin")) {
                FirebaseAuth.getInstance().signOut();
            }

            Toast.makeText(this, "Berhasil Keluar, Terimakasih " + currentDisplay, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void loadList(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chillify App").child(currentUser);

        data = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = new ArrayList<>();
                Map<String, music> value = (Map<String, music>) dataSnapshot.getValue();
                Log.d("testing getsnapshot", "Value is: " + value);

                for(DataSnapshot resultSnapshot : dataSnapshot.getChildren()){
                    music resMusic = resultSnapshot.getValue(music.class);
                    Log.d("testing result", resultSnapshot.getKey());
//                    data.add(resMusic);

                    data.add(new music(resultSnapshot.getKey(), resMusic.getJudul(), resMusic.getBand(), resMusic.getLink(), resMusic.getImg_url()));
                    reload();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("loadList", "Failed to read value.", error.toException());
            }
        });
    }

    public void reload(){
        Log.d("testing", "recycled");
        recyclerView = findViewById(R.id.music_recycler);
        adapter = new MusicAdapter(this, data);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(user_dashboard.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}