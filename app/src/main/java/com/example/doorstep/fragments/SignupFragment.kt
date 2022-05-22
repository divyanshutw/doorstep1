package com.example.doorstep.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.doorstep.R
import com.example.doorstep.activities.CustomerHomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SignupFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        val submit = view.findViewById<Button>(R.id.button_submitData)
        val firstName: EditText = view.findViewById(R.id.edittext_firstName)
        val lastName: EditText = view.findViewById(R.id.edittext_lastName)
        val phoneNumber: EditText = view.findViewById(R.id.editText_phoneNumber)
        val userdata = HashMap<String, Any>()
        userdata["firstName"] = firstName.text.toString()
        userdata["lastName"] = lastName.text.toString()
        userdata["phoneNumber"] = phoneNumber.text.toString()
        submit.setOnClickListener {
            val firestore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
            val reference = firestore.collection("Customers").document(
                auth.uid!!
            )
            reference.set(userdata, SetOptions.merge()).addOnSuccessListener {
                Toast.makeText(
                    context,
                    firstName.text.toString() + ", welcome to DoorStep",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(activity, CustomerHomeActivity::class.java))
            }.addOnFailureListener { e -> Log.e("SIGNUP ACTIVITY", e.message!!) }
        }
        return view
    }
}