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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.doorstep.R
import com.example.doorstep.databinding.FragmentLoginBinding
import com.example.doorstep.viewModels.LoginViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    lateinit var binding:FragmentLoginBinding
    lateinit var viewModel:LoginViewModel

    var preferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    private lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient:GoogleSignInClient

    private val RC_SIGN_IN = 9001;

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


        return binding.root
    }

    public fun googleSignIn(){

        var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_Client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        Log.d("div", "LoginFragment L72");
        binding.mainGoogleLogin.setOnClickListener {
            Log.d("div", "LoginFragment L74");
            googleSignInClient.signOut()
            var intent: Intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
            Log.d("div", "LoginFragment L78");
        }
    }



    public fun reload(){
        //TODO: The user is logged in.....go to the home page
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("div", "LoginFragment L89 onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("div", "LoginFragment L92");
        if(requestCode== RC_SIGN_IN)
        {
            Log.d("div", "LoginFragment L95 $data");
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            googleSignInResult(task)
        }
        Log.d("div", "LoginFragment L99 onActivityResult")
        //callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    fun googleSignInResult(task: Task<GoogleSignInAccount>)
    {
        try{
            val account:GoogleSignInAccount = task.getResult(ApiException::class.java)!!
            Log.d("div", "LoginFragment  L106 ${account.email}, ${account.id}")
            //If data found in database then goto FirebaseGoogleAuth function otherwise go to signUp page
//            Toast.makeText(activity, viewModel.uid, Toast.LENGTH_LONG).show()
            val authCredential: AuthCredential =
                GoogleAuthProvider.getCredential(account.idToken, null)
            Log.d("div", "LoginFragment L111");
            signInWithGoogleAuthCredential(authCredential)
            Log.d("div", "LoginFragment L113");
        }
        catch (e: ApiException)
        {
            Toast.makeText(activity, "Login failed", Toast.LENGTH_LONG).show()
            Log.e("googleLogin", "Error: ${e.stackTrace} / ${e.cause} / ${e.status} / ${e.localizedMessage}")
        }
    }

    fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential?) {
        val firebaseAuth = FirebaseAuth.getInstance()
        Log.d("div", "LoginFragment L124");

        firebaseAuth.signInWithCredential(googleAuthCredential!!)
            .addOnCompleteListener { authTask: Task<AuthResult> ->
                if (authTask.isSuccessful) {
                    Log.d("div", "LoginFragment L129");
                    val isNewUser =
                        authTask.result!!.additionalUserInfo!!.isNewUser
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        Log.d("div", "AuthRepository L35 ${firebaseUser.uid}, ${firebaseUser.email}, ${authTask.result!!.additionalUserInfo!!.isNewUser}, ${firebaseUser.displayName}," +
                                " ${firebaseUser.phoneNumber}, ${firebaseUser.photoUrl.toString()}");
                    }
                } else {
                    Log.d("div", "AuthRepository Google Failed ${authTask.exception} ${authTask.isCanceled} ${authTask.isComplete}")
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    fun ChangeUI(view: View) {
        val phoneNumberContainer = view.findViewById<LinearLayout>(R.id.phoneNumberContainer)
        val otpContainer = view.findViewById<LinearLayout>(R.id.otpContainer)
        phoneNumberContainer.visibility = View.INVISIBLE
        otpContainer.visibility = View.VISIBLE
    }


}