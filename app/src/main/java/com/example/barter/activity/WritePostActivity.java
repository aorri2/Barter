package com.example.barter.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.barter.PostInfo;
import com.example.barter.R;
import com.example.barter.Util;
import com.example.barter.view.ContentsItemView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import static com.example.barter.Util.GALLERY_IMAGE;
import static com.example.barter.Util.GALLERY_VIDEO;
import static com.example.barter.Util.INTENT_MEDIA;
import static com.example.barter.Util.INTENT_PATH;
import static com.example.barter.Util.isStorageUrl;
import static com.example.barter.Util.showToast;
import static com.example.barter.Util.storageUrlToName;

public class WritePostActivity extends BasicActivity {
    private static final String TAG ="WritePostActivity" ;
    private FirebaseUser user;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private int pathCount,successCount;
    private RelativeLayout btnBackLayout;
    private ImageView selectedImageView;
    private PostInfo postInfo;
    private StorageReference storageRef;
    private Util util;
    private RelativeLayout loaderLayout;
    private EditText selectedEditText;
    private EditText contentsEditText;
    private EditText titleEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        setToolbarTitle("게시글 작성");
        parent = findViewById(R.id.contentsLayout);
        btnBackLayout = findViewById(R.id.btnBackLayout);
        loaderLayout = findViewById(R.id.loaderLayout);
        contentsEditText = findViewById(R.id.contentsEditText);
        titleEditText = findViewById(R.id.et_title);


        findViewById(R.id.btn_check).setOnClickListener(onClickListener);
        findViewById(R.id.btn_img).setOnClickListener(onClickListener);
        findViewById(R.id.btn_video).setOnClickListener(onClickListener);
        findViewById(R.id.btn_imgModify).setOnClickListener(onClickListener);
        findViewById(R.id.btn_videoModify).setOnClickListener(onClickListener);
        findViewById(R.id.btn_delete).setOnClickListener(onClickListener);

        btnBackLayout.setOnClickListener(onClickListener);
        contentsEditText.setOnFocusChangeListener(onFocusChangeListener);
        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    selectedEditText = null;
                }
            }
        });
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        util = new Util();
        postInfo=(PostInfo)getIntent().getSerializableExtra("postInfo");
        postInit();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    String path= data.getStringExtra(INTENT_PATH);
                    pathList.add(path);


                  /*  ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                   */

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    if(selectedEditText == null){
                        parent.addView(contentsItemView);
                    }else{
                        for(int i=0; i<parent.getChildCount(); i++){
                            if(parent.getChildAt(i) == selectedEditText.getParent()){
                                parent.addView(contentsItemView,i+1);
                                break;
                            }
                        }
                    }

                    contentsItemView.setImage(path);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnBackLayout.setVisibility(View.VISIBLE);
                        selectedImageView = (ImageView) v;
                    }
                });
                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                /*
                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btnBackLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                        }
                    });
                    Glide.with(this).load(path).override(1000).into(imageView);
                    linearLayout.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("내용");
                    editText.setOnFocusChangeListener(onFocusChangeListener);

                    linearLayout.addView(editText);

                 */


                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK) {
                    String path= data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild((View) selectedImageView.getParent())-1,path);
                    Glide.with(this).load(path).override(1000).into(selectedImageView);
                }
                break;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_check:
                    storageUpload();
                    break;
                case R.id.btn_img:
                    myStartActivity(GalleryActivity.class,GALLERY_IMAGE,0);
                    break;
                case R.id.btn_video:
                    myStartActivity(GalleryActivity.class,GALLERY_VIDEO,0);
                    break;
                case R.id.btnBackLayout:
                    if(btnBackLayout.getVisibility() == View.VISIBLE){
                        btnBackLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.btn_imgModify:
                    myStartActivity(GalleryActivity.class,GALLERY_IMAGE,1);
                    btnBackLayout.setVisibility(View.GONE);
                    break;
                case R.id.btn_videoModify:
                    myStartActivity(GalleryActivity.class,GALLERY_VIDEO,1);
                    btnBackLayout.setVisibility(View.GONE);
                    break;
                case R.id.btn_delete:
                    final View selectedView = (View)selectedImageView.getParent();


                    StorageReference desertRef = storageRef.child("posts/"+postInfo.getId()+"/"+storageUrlToName(pathList.get(parent.indexOfChild(selectedView)-1)));
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                           showToast(WritePostActivity.this,"파일 삭제 성공");
                            pathList.remove(parent.indexOfChild(selectedView) - 1);
                            parent.removeView(selectedView);
                            btnBackLayout.setVisibility(View.GONE);

                        }
                    }).addOnFailureListener(exception -> {

                      showToast(WritePostActivity.this,"파일 삭제 실패");
                        // Uh-oh, an error occurred!
                    });


                    break;
            }
        }
    };

    private void myStartActivity(Class c, int media, int requestCode){
        Intent intent = new Intent(this,c);
        intent.putExtra(INTENT_MEDIA,media);
        startActivityForResult(intent,requestCode);

    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                selectedEditText = (EditText) v;
            }
        }
    };
    private void storageUpload(){
        final String title = ((EditText)findViewById(R.id.et_title)).getText().toString();

        if(title.length() >0 ) {

            loaderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentsList= new ArrayList<>();

            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            final DocumentReference documentReference =  postInfo == null ? firebaseFirestore.collection("posts").document():firebaseFirestore.collection("posts").document(postInfo.getId()) ;
            final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();
            for(int i =0; i<parent.getChildCount(); i++){
                LinearLayout linearLayout = (LinearLayout)parent.getChildAt(i);
                for(int j=0;j<linearLayout.getChildCount(); j++){
                    View view = linearLayout.getChildAt(j);
                    if(view instanceof EditText){
                        String text = ((EditText)view).getText().toString();
                        if(text.length() > 0) {
                            contentsList.add(text);
                        }
                    }else if(!isStorageUrl( pathList.get(pathCount))){

                        String path = pathList.get(pathCount);
                        successCount++;
                        contentsList.add(path);



                        String[] pathArray =path.split("\\.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/"+documentReference.getId()+"/"+pathCount+pathArray[pathArray.length-1]);

                        try{
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index",""+(contentsList.size()-1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream,metadata);



                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));

                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            successCount--;
                                            contentsList.set(index,uri.toString());
                                            if(successCount == 0){
                                                //완료

                                                PostInfo postInfo = new PostInfo(title,contentsList,user.getUid(),date);
                                                storeUpload(documentReference,postInfo);
                                                for(int i=0; i<contentsList.size(); i++) {
                                                    Log.e("로그 : ", "콘텐츠: " + contentsList.get(i));
                                                }
                                            }
                                        }
                                    });

                                }
                            });
                        }catch (FileNotFoundException e){

                            Log.e("로그","에러: "+e.toString());
                        }

                        pathCount++;
                    }
                }

            }
            if(successCount == 0){
                storeUpload(documentReference,new PostInfo(title,contentsList,user.getUid(),date));
            }


        }else{
            Toast.makeText(WritePostActivity.this, "글 제목이나 글 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    private void postInit(){
        if(postInfo != null){
            titleEditText.setText(postInfo.getTitle());

            ArrayList<String> contentsList= postInfo.getContent();

            for (int i=0; i<contentsList.size(); i++){

                String contents = contentsList.get(i);
                if(isStorageUrl( contents)){
                    pathList.add(contents);

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                        parent.addView(contentsItemView);


                    contentsItemView.setImage(contents);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btnBackLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                        }
                    });
                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                    if(i < contentsList.size() -1){
                        String nextContents = contentsList.get(i + 1);
                        if(!isStorageUrl( nextContents)){
                            contentsItemView.setText(nextContents);
                        }
                    }


                }else if(i == 0){
                    contentsEditText.setText(contents);
                }

            }
        }
    }

    private void storeUpload(DocumentReference documentReference, final PostInfo postInfo){
        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        loaderLayout.setVisibility(View.GONE);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("postinfo",postInfo);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        loaderLayout.setVisibility(View.GONE);
                    }
                });

        FirebaseFirestore db = FirebaseFirestore.getInstance();


    }
}
