package com.example.barter.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.barter.MemberInfo;
import com.example.barter.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MemberInitActivity extends AppCompatActivity {

    private static final String TAG = "MemberInitActivity";
    private ImageView profileImageView;
    private String profilePath;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);
        // Initialize Firebase Auth

        profileImageView = findViewById(R.id.iv_profile);
        profileImageView.setOnClickListener(onClickListener);

        findViewById(R.id.btn_checkinfo).setOnClickListener(onClickListener);
        findViewById(R.id.btn_picshot).setOnClickListener(onClickListener);
        findViewById(R.id.btn_gallery).setOnClickListener(onClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:{
                if(resultCode == Activity.RESULT_OK){
                    profilePath= data.getStringExtra("profilePath");
                    Log.e("로그 : ","profilePath" +profilePath);
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    profileImageView.setImageBitmap(bmp);

                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_checkinfo:
                    profileUpdate();
                    break;
                case R.id.iv_profile:
                    CardView cardView = findViewById(R.id.cv_buttons);
                    if(cardView.getVisibility() == View.VISIBLE){
                        cardView.setVisibility(View.GONE);
                }else{
                        cardView.setVisibility(View.VISIBLE);
                }
//                    myStartActivity(CameraActivity.class);
                    break;
                case R.id.btn_picshot:
                    myStartActivity(CameraActivity.class);
                    break;
                case R.id.btn_gallery:

                    if(ContextCompat.checkSelfPermission(MemberInitActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){

                        if(ActivityCompat.shouldShowRequestPermissionRationale(MemberInitActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)){

                        }else {
                            ActivityCompat.requestPermissions(MemberInitActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                    ,1);
                            Toast.makeText(MemberInitActivity.this,"권한을 허용해 주세요",Toast.LENGTH_SHORT).show();


                        }
                    }else {
                        myStartActivity(GalleryActivity.class);
                    }


                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myStartActivity(GalleryActivity.class);
                }  else {
                    Toast.makeText(MemberInitActivity.this,"권한을 허용해 주세",Toast.LENGTH_SHORT).show();

                }

        }
    }


    private void profileUpdate() {
        final String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
        final String phoneNum = ((EditText) findViewById(R.id.et_phoneNumber)).getText().toString();
        final String date = ((EditText) findViewById(R.id.et_date)).getText().toString();
        final String address = ((EditText) findViewById(R.id.et_address)).getText().toString();



        if (name.length() > 0 && phoneNum.length() > 0 && date.length()>5 && address.length() > 0) {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();

            user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/"+user.getUid()+"/profileImage.jpg");

            if(profilePath == null){
                MemberInfo memberInfo = new MemberInfo(name,phoneNum,date,address);
                uploader(memberInfo);
            }else{
                try{
                    InputStream stream = new FileInputStream(new File(profilePath));

                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Log.e("성공", "성공: " + downloadUri);
                                MemberInfo memberInfo = new MemberInfo(name,phoneNum,date,address, downloadUri.toString());
                                uploader(memberInfo);



                            }
                            else {
                                Toast.makeText(MemberInitActivity.this,"회원정보 전송 실패",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (FileNotFoundException e){

                    Log.e("로그","에러: "+e.toString());
                }
            }





        } else {
            Toast.makeText(MemberInitActivity.this, "회원정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploader(MemberInfo memberInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MemberInitActivity.this, "회원정보 등록 성공.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MemberInitActivity.this, "회원정보 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivityForResult(intent,0);

    }


}