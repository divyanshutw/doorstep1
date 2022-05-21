package com.example.doorstep.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.doorstep.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class SignupTest extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     View view= inflater.inflate(R.layout.fragment_signup, container, false);
        EditText firstName,lastName,phoneNumber;
        Button submit=view.findViewById(R.id.button_submitData);
        firstName=view.findViewById(R.id.edittext_firstName);
        lastName=view.findViewById(R.id.edittext_lastName);
        phoneNumber=view.findViewById(R.id.editText_phoneNumber);
        HashMap<String,Object> userdata=new HashMap<>();
        userdata.put("firstName",firstName.getText().toString());
        userdata.put("lastName",lastName.getText().toString());
        userdata.put("phoneNumber",phoneNumber.getText().toString());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firestore=FirebaseFirestore.getInstance();
                FirebaseAuth auth=FirebaseAuth.getInstance();
                DocumentReference reference=firestore.collection("Customers").document(auth.getUid());
                reference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });



       return view;
    }
}