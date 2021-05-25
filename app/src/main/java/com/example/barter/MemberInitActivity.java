package com.example.barter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class MemberInitActivity extends AppCompatActivity {

    private static final String TAG = "MemberInitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);
        // Initialize Firebase Auth


        findViewById(R.id.btn_checkinfo).setOnClickListener(onClickListener);
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
            }
        }
    };

    private void profileUpdate() {
        String name = ((EditText) findViewById(R.id.et_name)).getText().toString();
        String phoneNum = ((EditText) findViewById(R.id.et_phoneNumber)).getText().toString();
        String date = ((EditText) findViewById(R.id.et_date)).getText().toString();
        String address = ((EditText) findViewById(R.id.et_address)).getText().toString();



        if (name.length() > 0 && phoneNum.length() > 0 && date.length()>5 && address.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            MemberInfo memberInfo = new MemberInfo(name,phoneNum,date,address);
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

        } else {
            Toast.makeText(MemberInitActivity.this, "회원정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }




}