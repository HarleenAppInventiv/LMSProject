package com.googlesignin

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.googlesignin.interfaces.GoogleSignCallback

class GoogleSignInAI {
    private var mActivity: Activity? = null
    private var mGoogleSignInOptions: GoogleSignInOptions? = null
    private var GoogleSignInRequestCode = 0
    private var mGoogleSignCallback: GoogleSignCallback? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    /*
   *  Initialize activity instance
   */
    fun setActivity(activity: Activity?) {
        mActivity = activity
    }

    /*
    *  Initialize Google callback
    */
    fun setCallback(mGoogleSignCallback: GoogleSignCallback?) {
        this.mGoogleSignCallback = mGoogleSignCallback
    }

    /*
    *  Initialize Google request code
    */
    fun setRequestCode(requestCode: Int) {
        this.GoogleSignInRequestCode = requestCode
    }

    /*
    * Configure google sign in request for contacts
    */
    fun setUpGoogleClientForGoogleLogin() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

        // Build a GoogleSignInClient with the options specified by mGoogleSignInOptions.
        mGoogleSignInClient = GoogleSignIn.getClient(mActivity!!, mGoogleSignInOptions!!)
    }

    fun doSignIn() {
        val account = GoogleSignIn.getLastSignedInAccount(mActivity!!)
        when {
            account != null -> {
                mGoogleSignCallback!!.googleSignInSuccessResult(account)
            }
            mGoogleSignInClient != null -> {
                val signInIntent = mGoogleSignInClient!!.signInIntent
                mActivity!!.startActivityForResult(signInIntent, GoogleSignInRequestCode)
            }
            else -> Toast.makeText(
                mActivity,
                "Google SignIn Client is not connected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun onActivityResult(data: Intent?) {
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (data != null) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                // Signed in successfully, show authenticated UI.
                mGoogleSignCallback!!.googleSignInSuccessResult(account)
            } else Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            mGoogleSignCallback!!.googleSignInFailureResult(e.statusCode.toString() + "")
        }
    }

    fun doSignout() {
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient!!.signOut()
                .addOnCompleteListener(
                    mActivity!!
                ) { Toast.makeText(mActivity, "Sign Out Successfully", Toast.LENGTH_SHORT).show() }
        } else {
            Toast.makeText(mActivity, "Please Login First", Toast.LENGTH_SHORT).show()
        }
    }
}