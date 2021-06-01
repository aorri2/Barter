package com.example.barter.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.barter.Util;
import com.example.barter.adapter.MainAdapter;
import com.example.barter.PostInfo;
import com.example.barter.R;
import com.example.barter.fragment.Frag1;
import com.example.barter.fragment.Frag2;
import com.example.barter.fragment.Frag3;
import com.example.barter.fragment.Frag4;
import com.example.barter.fragment.Frag5;
import com.example.barter.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag1 frag1;
    private Frag2 frag2;
    private Frag3 frag3;
    private Frag4 frag4;
    private Frag5 frag5;
    private  FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private  DocumentReference documentReference;
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private StorageReference storageRef;
    private ArrayList<PostInfo> postList;
    private static final String TAG = "MainActivity";
    private Util util;
    private int successCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViewById(R.id.floatingActionButton3).setOnClickListener(onClickListener);
        bottomNavigationView = findViewById(R.id.bottomNavi);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef= storage.getReference();

        if(firebaseUser == null){
            myStartActivity(SignUpActivity.class);
        }else{

//            myStartActivity(MemberInitActivity.class);
            //  myStartActivity(CameraActivity.class);

            documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());


                        } else {

                            Log.d(TAG, "No such document");
                            myStartActivity(MemberInitActivity.class);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }





        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_add:
                        setFrag(0);
                        break;
                    case R.id.action_chat:
                        setFrag(1);
                        break;
                    case R.id.action_home:
                        setFrag(2);
                        break;
                    case R.id.action_list:
                        setFrag(3);
                        break;
                    case R.id.action_person:
                        setFrag(4);
                        break;
                }
                return true;
            }
        });
        frag1 = new Frag1();
        frag2 = new Frag2();
        frag3 = new Frag3();
        frag4 = new Frag4();
        frag5 = new Frag5();

        setFrag(0); // 첫프래그먼트 화면 지정(ㅇ)안에 넣음 댐

        postList = new ArrayList<>();
        mainAdapter = new MainAdapter(MainActivity.this,postList);


        mainAdapter.setOnPostListener(onPostListener);

        util = new Util(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(mainAdapter);

    }


    @Override
    protected void onResume(){
        super.onResume();
        postUpdate();

        }

    private void postUpdate(){
        if(firebaseUser != null){
            CollectionReference collectionReference = firebaseFirestore.collection("posts");


            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    postList.add(new PostInfo(
                                            document.getData().get("title").toString(),
                                            (ArrayList<String>) document.getData().get("content"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId()

                                    ));
                                    Log.e("로그 : ","데이터 : "+document.getData().get("title").toString());
                                    Log.e("로그: ","데이터  ID : "+document.getId());


                                }
                                mainAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }


    private void startSignupActivity() {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this,c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void myStartActivity(Class c, String id){
        Intent intent = new Intent(this,c);
        intent.putExtra("id",id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(int position) {
           final String id = postList.get(position).getId();
            ArrayList<String> contentsList= postList.get(position).getContent();

            for (int i=0; i<contentsList.size(); i++){

                String contents = contentsList.get(i);
                if(Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/v0/b/barter-project-91cd2.appspot.com/o/posts")){
                    successCount++;
                    String[] list =contents.split("\\?") ;
                    String[] list2 =list[0].split("%2F");
                    String name = list2[list2.length-1];

                    // Create a storage reference from our app


// Create a reference to the file to delete
                    StorageReference desertRef = storageRef.child("posts/"+id+"/"+name);



// Delete the file
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            successCount--;
                            storeUploader(id);

                        }
                    }).addOnFailureListener(exception -> {

                        util.showToast("게시글을 삭제하지 못했 습니다.");
                        // Uh-oh, an error occurred!
                    });
                }

            }
            storeUploader(id);
        }

        @Override
        public void onModify(int position) {
            String id = postList.get(position).getId();
            myStartActivity(WritePostActivity.class, id);
        }
    };

    View.OnClickListener onClickListener = (v) ->{
      switch (v.getId()){
          case R.id.floatingActionButton3:
              Intent intent = new Intent(MainActivity.this, WritePostActivity.class);
              startActivity(intent);
              break;
      }
    };

    private void storeUploader(String id){
        if(successCount == 0){
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            util.showToast("게시글을 삭제하였습니다.");
                            postUpdate();
//                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        util.showToast("게시글을 삭제하지 못했 습니다.");
//                                        Log.w(TAG, "Error deleting document", e);
                    });
        }
    }

    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n)
        {
            case 0:
                ft.replace(R.id.main_frame,frag1);
                ft.commit();

                break;
            case 1:
                ft.replace(R.id.main_frame, frag2);
                ft.commit();

                break;
            case 2:
                ft.replace(R.id.main_frame, frag3);
                ft.commit();

                break;
            case 3:
                ft.replace(R.id.main_frame, frag4);
                ft.commit();

                break;
            case 4:
                ft.replace(R.id.main_frame, frag5);
                ft.commit();
                break;
        }
    }
}