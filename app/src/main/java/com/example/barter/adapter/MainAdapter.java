package com.example.barter.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barter.FirebaseHelper;
import com.example.barter.activity.PostActivity;
import com.example.barter.activity.WritePostActivity;
import com.example.barter.listener.OnPostListener;
import com.example.barter.PostInfo;
import com.example.barter.R;
import com.example.barter.view.ReadContentsView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.barter.Util.isStorageUrl;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private final int MORE_INDEX = 2;
    private OnPostListener onPostListener;
    private FirebaseHelper firebaseHelper;

    public static class MainViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public MainViewHolder(CardView v){
            super(v);
            cardView = v;


        }
    }

    public MainAdapter(Activity activity, ArrayList<PostInfo> myDataset){
       this.mDataset = myDataset;
        this.activity = activity;


        firebaseHelper = new FirebaseHelper(activity);
    }

    public void setOnPostListener(OnPostListener onPostListener){
        firebaseHelper.setOnPostListener(onPostListener);
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener((v)->{
            Intent intent = new Intent(activity, PostActivity.class);
            intent.putExtra("postInfo",mDataset.get(mainViewHolder.getAdapterPosition()));
            activity.startActivity(intent);
        });


        cardView.findViewById(R.id.menu_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v,mainViewHolder.getAdapterPosition());

            }
        });

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView item_text = cardView.findViewById(R.id.item_text);

        PostInfo postInfo = mDataset.get(position);
        item_text.setText(postInfo.getTitle());

        ReadContentsView readContentsView = cardView.findViewById(R.id.readContentsView);


        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);

        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(postInfo)){
            contentsLayout.setTag(postInfo);
            contentsLayout.removeAllViews();

            readContentsView.setMoreIndex(MORE_INDEX);
            readContentsView.setPostInfo(postInfo);
        }








    }



    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modify:
                        myStartActivity(WritePostActivity.class, mDataset.get(position));
                        return true;
                    case R.id.delete:

                        firebaseHelper.storageDelete(mDataset.get(position));
                        return true;
                    default:
                        return false;
                }

            }
        });
        inflater.inflate(R.menu.post_menu, popup.getMenu());
        popup.show();
    }


    private void myStartActivity(Class c, PostInfo postInfo){
        Intent intent = new Intent(activity,c);
        intent.putExtra("postInfo",postInfo);
        activity.startActivity(intent);
    }


}

