package com.example.barter.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.barter.GlideApp;
import com.example.barter.PostInfo;
import com.example.barter.R;
import com.example.barter.listener.OnPostListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private OnPostListener onPostListener;
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
    public void onBindViewHolder(@NonNull MainAdapter.MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView item_text = cardView.findViewById(R.id.item_text);
        item_text.setText(mDataset.get(position).getTitle());

        TextView tv_createdAt = cardView.findViewById(R.id.tv_createAt);
        tv_createdAt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList= mDataset.get(position).getContent();

        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentsList)){
            contentsLayout.setTag(contentsList);
            contentsLayout.removeAllViews();
            final int MORE_INDEX = 2;
            for (int i=0; i<contentsList.size(); i++){
                if(i == MORE_INDEX){
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText("더 보기");
                    contentsLayout.addView(textView, i);
                    break;
                }
                String contents = contentsList.get(i);
                if(Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/v0/b/barter-project-91cd2.appspot.com/o/posts")){
                    ImageView imageView = new ImageView(activity);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView, i);
                    Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into( imageView);
                }else {

                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    textView.setTextColor(Color.rgb(0,0,0));
                    contentsLayout.addView(textView, i);

                }
            }
        }








    }



    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }

    public void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modify:
                        onPostListener.onModify(position);
                        return true;
                    case R.id.delete:
                        onPostListener.onDelete(position);

                        return true;
                    default:
                        return false;
                }

            }
        });
        inflater.inflate(R.menu.post_menu, popup.getMenu());
        popup.show();
    }


}

