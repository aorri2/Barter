package com.example.barter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barter.FirebaseHelper;
import com.example.barter.Frag.Frag1;
import com.example.barter.Frag.Frag3;
import com.example.barter.Frag.Frag4;
import com.example.barter.Frag.MyInfoFragment;
import com.example.barter.Frag.listChatFragment;
import com.example.barter.adapter.MainAdapter;
import com.example.barter.listener.OnPostListener;
import com.example.barter.PostInfo;
import com.example.barter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.barter.Util.isStorageUrl;
import static com.example.barter.Util.showToast;
import static com.example.barter.Util.storageUrlToName;


public class MainActivity extends BasicActivity {
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag1 frag1;
    private listChatFragment frag2;
    private Frag3 frag3;
    private Frag4 frag4;
    private MyInfoFragment frag5;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private ArrayList<PostInfo> postList;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setToolbarTitle(getResources().getString(R.string.app_name));
        findViewById(R.id.floatingActionButton3).setOnClickListener(onClickListener);
        bottomNavigationView = findViewById(R.id.bottomNavi);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        if(firebaseUser == null){
            myStartActivity(SignUpActivity.class);
        }else{

//            myStartActivity(MemberInitActivity.class);
            //  myStartActivity(CameraActivity.class);
            firebaseFirestore = FirebaseFirestore.getInstance();

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
                switch (item.getItemId()) {
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

        postList = new ArrayList<>();
        mainAdapter = new MainAdapter(MainActivity.this,postList);
        mainAdapter.setOnPostListener(onPostListener);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(mainAdapter);

        getFirebaseMessagingToken();

        frag1 = new Frag1();
        frag2 = new listChatFragment();
        frag3 = new Frag3();
        frag4 = new Frag4();
        frag5 = new MyInfoFragment();

        setFrag(0); // 첫프래그먼트 화면 지정(ㅇ)안에 넣음 댐
    }

    @Override
    protected void onResume(){
        super.onResume();
        postUpdate();

    }
    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete() {
            postUpdate();
            Log.e("로그: ","삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그: ","수정 성공");
        }
    };
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

    private void myStartActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }

    private void myStartActivity(Class c, PostInfo postInfo){
        Intent intent = new Intent(this,c);
        intent.putExtra("postInfo",postInfo);
        startActivity(intent);
    }



    View.OnClickListener onClickListener = (v) ->{
        switch (v.getId()){
            case R.id.floatingActionButton3:
                Intent intent = new Intent(MainActivity.this, WritePostActivity.class);
                startActivity(intent);
                break;
        }
    };



    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, frag1);
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

    public void getFirebaseMessagingToken() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        //Could not get FirebaseMessagingToken
                        return;
                    }
                    if (null != task.getResult()) {
                        //Got FirebaseMessagingToken
                        String firebaseMessagingToken = Objects.requireNonNull(task.getResult());
                        Map<String, Object> map = new HashMap<>();
                        map.put("pushToken", firebaseMessagingToken);
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
                        //Use firebaseMessagingToken further
                    }
                });
    }
}