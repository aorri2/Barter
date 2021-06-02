package com.example.barter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.barter.FirebaseHelper;
import com.example.barter.PostInfo;
import com.example.barter.R;
import com.example.barter.listener.OnPostListener;
import com.example.barter.view.ContentsItemView;
import com.example.barter.view.ReadContentsView;

import static com.example.barter.Util.INTENT_PATH;


public class PostActivity extends BasicActivity {
    PostInfo postInfo;
    private FirebaseHelper firebaseHelper;
    private ReadContentsView readContentsView;
    private LinearLayout contentsLayout;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");



        contentsLayout = findViewById(R.id.contentsLayout);
        readContentsView = findViewById(R.id.readContentsView);


        firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.setOnPostListener(onPostListener);
        uiUpdate();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    postInfo= (PostInfo)data.getSerializableExtra("postinfo");
                    contentsLayout.removeAllViews();
                    uiUpdate();



                }
                break;

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                firebaseHelper.storageDelete(postInfo);
                finish();
                return true;

            case R.id.modify:

                myStartActivity(WritePostActivity.class,postInfo);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    OnPostListener onPostListener = new OnPostListener(){
        @Override
        public void onDelete() {

            Log.e("로그","삭제 성공");
        }

        @Override
        public void onModify() {

            Log.e("로그","수정 성공");
        }

    };

    private void uiUpdate(){

        setToolbarTitle(postInfo.getTitle());
        readContentsView.setPostInfo(postInfo);
    }


    private void myStartActivity(Class c, PostInfo postInfo){
        Intent intent = new Intent(this,c);
        intent.putExtra("postInfo",postInfo);
        startActivityForResult(intent,0);
    }
}
