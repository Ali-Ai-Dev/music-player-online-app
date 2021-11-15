package com.app.ali_bozorgzad.music_player;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

class AdapterMusicList extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;

    AdapterMusicList(Context context){
        this.context = context;
    }

    private class RvViewHolder extends RecyclerView.ViewHolder {
        ImageView musicImage;
        TextView musicName, artistName;
        CardView cardView;

        RvViewHolder(View view) {
            super(view);

            musicImage = (ImageView) view.findViewById(R.id.imgMusicCover);
            musicName = (TextView) view.findViewById(R.id.txtMusicName);
            artistName = (TextView) view.findViewById(R.id.txtArtistName);
            cardView = (CardView) view.findViewById(R.id.cardView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.rv_music_list,parent,false);
        return new RvViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final RvViewHolder holder = (RvViewHolder) viewHolder;
        Picasso.with(context).load(ActivityMain.musicPicture.get(position)).into(holder.musicImage);
        holder.musicName.setText(ActivityMain.musicName.get(position));
        holder.artistName.setText(ActivityMain.artistName.get(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ActivityPlayingMusic.class);
                intent.putExtra("musicInfoID", ActivityMain.musicInfoID.get(position));
                intent.putExtra("musicName", ActivityMain.musicName.get(position));
                intent.putExtra("artistName", ActivityMain.artistName.get(position));
                intent.putExtra("musicPicture", ActivityMain.musicPicture.get(position));
                intent.putExtra("link", ActivityMain.musicFile.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ActivityMain.musicInfoID.size();
    }
}
