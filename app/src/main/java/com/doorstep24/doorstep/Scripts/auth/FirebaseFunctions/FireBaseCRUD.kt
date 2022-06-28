package com.doorstep24.doorstep.Scripts.auth.FirebaseFunctions

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class FireBaseCRUD {
    fun createUser(data: Map<String?, Any?>?, firestore: FirebaseFirestore): Boolean {
        val userCreated = BooleanArray(1)
        firestore.collection("Customers").document().set(data!!).addOnSuccessListener {
            userCreated[0] = true
        }.addOnFailureListener { userCreated[0] = false }
        return userCreated[0]
    }

    fun checkUser(userId: String?, firestore: FirebaseFirestore): Boolean {
        val userExist = BooleanArray(1)
        val docIdRef = firestore.collection("Customers").document(
            userId!!
        )
        docIdRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e("Task Successful ","FUNCTION ENTERED")
                val document = task.result
                userExist[0] = true

            } else {
                userExist[0] = false
                Log.e("FireBaseCRUD", "Failed with: ", task.exception)
            }
        }
        return userExist[0]
    }

}