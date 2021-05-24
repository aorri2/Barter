package com.example.barter;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class WritePostActivity extends AppCompatActivity {
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        findViewById(R.id.btn_check).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_check:
                    profileUpdate();
                    break;
            }
        }
    };

    private void profileUpdate(){
        final String title = ((EditText)findViewById(R.id.et_title)).getText().toString();
        final String content = ((EditText)findViewById(R.id.et_content)).getText().toString();

        if(title.length() >0 && content.length() > 0) {
             user = FirebaseAuth.getInstance().getCurrentUser();
            PostInfo postInfo = new PostInfo(title,content,user.getUid());
            uploader(postInfo);
        }else{
            Toast.makeText(WritePostActivity.this, "글 제목이나 글 내용을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploader(PostInfo postInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").add(postInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("WritePostActivity","DocumentSnapshot Written with ID : "+documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("WritePostActivity","Error adding document",e);

            }
        });

    }
}
