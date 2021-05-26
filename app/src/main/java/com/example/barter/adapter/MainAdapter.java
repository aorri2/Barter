package com.example.barter.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.PostInfo;
import com.example.barter.R;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    public static class MainViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public MainViewHolder(CardView v){
            super(v);
            cardView = v;
        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataset){
        mDataset = myDataset;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener((v)->{

        });
        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;
//        ImageView imageView = cardView.findViewById(R.id.)
        TextView textView = cardView.findViewById(R.id.item_text);
        textView.setText(mDataset.get(position).getTitle());

    }



    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
