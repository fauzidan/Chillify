package com.example.chillify.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chillify.model.music;
import com.example.chillify.R;
import com.example.chillify.music_ingfo;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder>{

    private Context context;
    List<music> data;

    class MusicViewHolder extends RecyclerView.ViewHolder{
        TextView list_m_title;
        TextView list_m_band;
        ImageView list_m_thumb;

        MusicViewHolder(View view){
            super(view);
            list_m_title = view.findViewById(R.id.list_m_title);
            list_m_band = view.findViewById(R.id.list_m_band);
            list_m_thumb = view.findViewById(R.id.list_m_thumb);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent info = new Intent(v.getContext(), music_ingfo.class);
                    music selectedMusic = data.get(getAdapterPosition());
                    String selectedId = selectedMusic.getId();

                    info.putExtra("id", selectedId);
                    v.getContext().startActivity(info);
                }
            });
        }
    }

    public MusicAdapter(Context context, List<music> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_list, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, final int position) {
        final music result = data.get(position);
        holder.list_m_title.setText(result.getJudul());
        holder.list_m_band.setText(result.getBand());
        if(result.getImg_url().length() != 0){
            Glide.with(context)
                    .load(result.getImg_url())
                    .into(holder.list_m_thumb);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
