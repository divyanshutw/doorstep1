package com.example.doorstep.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.doorstep.Interface.ChangeFragment
import com.example.doorstep.R
import com.example.doorstep.Scripts.auth.OtpAuthentication
import com.example.doorstep.activities.CustomerHomeActivity
import com.example.doorstep.databinding.FragmentLoginBinding
import com.example.doorstep.viewModels.LoginViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class   LoginFragment : Fragment() {

    lateinit var binding:FragmentLoginBinding
    lateinit var viewModel:LoginViewModel

    var preferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    private lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient:GoogleSignInClient

    private val RC_SIGN_IN = 9001;

    private lateinit var phoneNUmberContainer:LinearLayout
    private lateinit var otpContainer:LinearLayout

    private lateinit var Submit:Button
    private lateinit var check:Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding.lifecycleOwner = this

        preferences=activity?.getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE)
        editor = preferences!!.edit()

        googleSignIn();
        auth = Firebase.auth


        val number = binding.edittextMobile
        auth = FirebaseAuth.getInstance()
        Submit = binding.buttonGetOtp
        check = binding.buttonCheckOtp
        val otpAuthentication = arrayOfNulls<OtpAuthentication>(1)
        val otp =binding.edittextOtp
        phoneNUmberContainer=binding.phoneNumberContainer
        otpContainer=binding.otpContainer


        Submit.setOnClickListener { view ->
            Log.e("SUBMIT","ENTER")
            otpAuthentication[0] = OtpAuthentication(requireActivity(), auth!!)
            otpAuthentication[0]!!.getOtp(number.text.toString())
            otpAuthentication[0]!!.setMyCustomListener { result ->
                if (result) {

                    ChangeUI()
                }
            }
            otpAuthentication[0]!!.setFragmentChangeListener(ChangeFragment { value: Boolean ->
                if (value){
                    val firebaseFirestore:FirebaseFirestore=FirebaseFirestore.getInstance()
                    val firebaseAuth:FirebaseAuth= FirebaseAuth.getInstance()
                    firebaseFirestore.collection("Customers").document(firebaseAuth.uid.toString()).get().addOnCompleteListener(
                        OnCompleteListener { if (it.isSuccessful) {
                            val document = it.result
                            if(document != null) {
                                if (document.exists()) {
                                    startActivity(Intent(context,CustomerHomeActivity::class.java))
                                } else {
                                    Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signupFragment)
                                }
                            }else{
                                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signupFragment)
                            }
                        } else {
                            Log.e("TAG", "Error: ", it.exception)
                        } })
                    Log.e("CHANGE CREATED87y7","FUNCTION ENTERED")

                }
            })
        }
        check.setOnClickListener { if (otp.text != null) otpAuthentication[0]!!.signIn(otp.text.toString()) }


        return binding.root
    }

    private fun googleSignIn(){

        var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_Client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.mainGoogleLogin.setOnClickListener {
            googleSignInClient.signOut()
            var intent: Intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }



    private fun reload(){
        startActivity(Intent(this.context, CustomerHomeActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("div", "LoginFragment L132 $resultCode $requestCode")
        if(requestCode== RC_SIGN_IN)
        {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            googleSignInResult(task)
        }
        //callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    private fun googleSignInResult(task: Task<GoogleSignInAccount>)
    {
        try{
            Log.d("div","LoginFragment L143 googleSignInResult")
            val account:GoogleSignInAccount = task.getResult(ApiException::class.java)!!
            //If data found in database then goto FirebaseGoogleAuth function otherwise go to signUp page
            Log.d("div","LoginFragment L146 googleSignInResult")
            val authCredential: AuthCredential =
                GoogleAuthProvider.getCredential(account.idToken, null)
            Log.d("div","LoginFragment L149 googleSignInResult ${account.idToken}")
            signInWithGoogleAuthCredential(authCredential)
        }
        catch (e: ApiException)
        {
            Toast.makeText(activity, "Login failed", Toast.LENGTH_LONG).show()
            Log.e("div", "Error: ${e.stackTrace} / ${e.cause} / ${e.status} / ${e.localizedMessage}")
        }
    }

    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential?) {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signInWithCredential(googleAuthCredential!!)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                if (authTask.isSuccessful) {
                    val isNewUser = authTask.result!!.additionalUserInfo!!.isNewUser
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        Log.d("div", "LoginFragment L160 ${firebaseUser.uid}, ${firebaseUser.email}, ${authTask.result!!.additionalUserInfo!!.isNewUser}, ${firebaseUser.displayName}," +
                                " ${firebaseUser.phoneNumber}, ${firebaseUser.photoUrl.toString()}, $isNewUser");
                        editor?.putBoolean("isLoggedIn", true)
                        editor?.putString("uid", firebaseUser.uid)
                        editor?.commit()
                        reload()
                    }
                } else {
                    Log.d("div", "AuthRepository Google Failed ${authTask.exception} ${authTask.isCanceled} ${authTask.isComplete}")
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        if(currentUser != null){
//            reload();
//        }
        if(preferences!!.contains("isLoggedIn") && preferences!!.getBoolean("isLoggedIn", false))
            reload()
    }

    private fun ChangeUI() {
        Log.e("CHANGE UI","ENTER")
//        val phoneNumberContainer = view.findViewById<LinearLayout>(R.id.phoneNumberContainer)
//        val otpContainer = view.findViewById<LinearLayout>(R.id.otpContainer)
//        phoneNumberContainer.visibility = View.INVISIBLE
        phoneNUmberContainer.visibility=View.INVISIBLE
        otpContainer.visibility = View.VISIBLE
        Submit.visibility=View.INVISIBLE

        Log.e("CHANGE UI",phoneNUmberContainer.isVisible.toString())

    }



}