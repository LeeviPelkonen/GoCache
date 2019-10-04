package com.example.gocache

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.NonNull
import com.example.gocache.ui.home.HomeFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_login.view.*

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        fun signIn() {
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100)
        }

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener {
            when (it.id) {
                R.id.sign_in_button -> signIn()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val account = GoogleSignIn.getLastSignedInAccount(this)

        if (account != null) {
            Log.d("LoginInfo", "Using last account to sign in")
            updateUI(account)
        }
        else {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
            try {
                val account: GoogleSignInAccount =
                    completedTask.getResult(ApiException::class.java)!!
                Log.d(
                    "LoginInfo",
                    (account.displayName).toString() + " " + (account.email).toString()
                )
                updateUI(account)
            } catch (e: ApiException) {
                Log.w("lofinFail", "signInresult: failed code=" + e.statusCode)
            }
        }

        if (requestCode == 100) run {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        else if (requestCode == 200) run {

        }

    }

    private fun updateUI(v: GoogleSignInAccount) {
        val info = Bundle()
        info.putString("name", v.displayName)
        info.putString("email", v.email)
        if (v.photoUrl != null) {
            info.putString("picture", v.photoUrl.toString())
        }
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.putExtras(info)
        startActivity(mainIntent)
    }

    fun signOut(context: Context) {
        Log.d("LoginInfo","pressed Signout" )
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
        mGoogleSignInClient.signOut()
            .addOnCompleteListener {
                Log.d("LoginInfo", "worked")
            }
    }
}
