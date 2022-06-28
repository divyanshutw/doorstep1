package com.doorstep24.doorstep.fragments

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
import androidx.lifecycle.MutableLiveData
import com.doorstep24.doorstep.R
import com.doorstep24.doorstep.activities.CustomerHomeActivity
import com.doorstep24.doorstep.utilities.Dialogs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SignupFragment : Fragment() {

    val isLoadingDialogVisible = MutableLiveData<Boolean>(false)

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
        Log.e("Signuo" ,firstName.text.toString() )
        val userdata = HashMap<String, Any>()

        submit.setOnClickListener {
            userdata["firstName"] = firstName.text.toString()
            userdata["lastName"] = lastName.text.toString()
            userdata["phoneNumber"] = phoneNumber.text.toString()
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
                Log.e("Signuo" , "ojojoiioj$userdata")
                startActivity(Intent(activity, CustomerHomeActivity::class.java))
            }.addOnFailureListener { e -> Log.e("SIGNUP ACTIVITY", e.message!!) }
        }

        initObservers()

        return view
    }

    private fun initObservers() {
        isLoadingDialogVisible.observe(viewLifecycleOwner){
            if(it != null){
                if(it){
                    val dialogs = Dialogs(requireContext(), viewLifecycleOwner)
                    dialogs.showLoadingDialog(isLoadingDialogVisible)
                }
            }
        }
    }
}