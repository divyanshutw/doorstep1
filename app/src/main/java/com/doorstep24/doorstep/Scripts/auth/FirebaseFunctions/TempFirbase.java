package com.doorstep24.doorstep.Scripts.auth.FirebaseFunctions;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

class TempFirebase {

    public TempFirebase() {
    }

    public boolean createUser(Map<String, Object> data, FirebaseFirestore firestore) {
        final boolean[] userCreated = new boolean[1];

        firestore.collection("Customers").document().set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                userCreated[0]=true;

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userCreated[0]=false;

            }
        });

        return userCreated[0];
    }
    public boolean checkUser(String userId, FirebaseFirestore firestore){
        final boolean[] userExist = new boolean[1];

        DocumentReference docIdRef = firestore.collection("Customers").document(userId);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userExist[0] =true;
                    } else {
                        userExist[0] =false;
                    }
                } else {
                    Log.e("FireBaseCRUD", "Failed with: ", task.getException());
                }
            }
        });

        return userExist[0];
    }


}
