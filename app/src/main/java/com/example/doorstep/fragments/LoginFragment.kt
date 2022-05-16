package com.example.doorstep.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.doorstep.R
import com.example.doorstep.Scripts.auth.OtpAuthentication
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val number = view.findViewById<EditText>(R.id.edittext_mobile)
        auth = FirebaseAuth.getInstance()
        val Submit = view.findViewById<Button>(R.id.button_getOtp)
        val check = view.findViewById<Button>(R.id.button_checkOtp)
        val otpAuthentication = arrayOfNulls<OtpAuthentication>(1)
        val otp = view.findViewById<EditText>(R.id.edittext_otp)
        Submit.setOnClickListener { view ->
            otpAuthentication[0] = OtpAuthentication(requireActivity(), auth!!)
            otpAuthentication[0]!!.getOtp(number.text.toString())
            otpAuthentication[0]!!.setMyCustomListener { result ->
                if (result) {
                    ChangeUI(view)
                }
            }
        }
        check.setOnClickListener { if (otp.text != null) otpAuthentication[0]!!.signIn(otp.text.toString()) }


        // Inflate the layout for this fragment
        return view
    }

    fun ChangeUI(view: View) {
        val phoneNumberContainer = view.findViewById<LinearLayout>(R.id.phoneNumberContainer)
        val otpContainer = view.findViewById<LinearLayout>(R.id.otpContainer)
        phoneNumberContainer.visibility = View.INVISIBLE
        otpContainer.visibility = View.VISIBLE
    }
}