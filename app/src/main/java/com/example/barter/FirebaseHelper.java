package com.example.barter;

import android.app.Activity;

import com.example.barter.activity.MainActivity;
import com.example.barter.listener.OnPostListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.example.barter.Util.isStorageUrl;
import static com.example.barter.Util.showToast;
import static com.example.barter.Util.storageUrlToName;

public class FirebaseHelper {
    private Activity activity;
    private OnPostListener onPostListener;
    private int successCount;
   public FirebaseHelper(Activity activity){
        this.activity = activity;
    }

    public void setOnPostListener(OnPostListener onPostListener){
            this.onPostListener = onPostListener;
    }

    public void storageDelete(PostInfo postInfo){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final String id = postInfo.getId();
        ArrayList<String> contentsList= postInfo.getContent();

        for (int i=0; i<contentsList.size(); i++){

            String contents = contentsList.get(i);
            if(isStorageUrl(contents)){
                successCount++;

                StorageReference desertRef = storageRef.child("posts/"+id+"/"+storageUrlToName(contents));
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        successCount--;
                        storeDelete(id);

                    }
                }).addOnFailureListener(exception -> {

                    showToast(activity,"게시글을 삭제하지 못했 습니다.");
                    // Uh-oh, an error occurred!
                });
            }

        }
        storeDelete(id);
    }

    private void storeDelete(String id){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if(successCount == 0){
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(activity,"게시글을 삭제하였습니다.");
                            onPostListener.onDelete();
//                            postUpdate();
//                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        showToast(activity,"게시글을 삭제하지 못했 습니다.");
//                                        Log.w(TAG, "Error deleting document", e);
                    });
        }
    }
}
