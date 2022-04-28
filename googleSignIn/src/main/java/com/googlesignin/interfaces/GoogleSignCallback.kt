package com.googlesignin.interfaces

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface GoogleSignCallback {
    fun googleSignInSuccessResult(googleSignInAccount: GoogleSignInAccount?)
    fun googleSignInFailureResult(message: String?)
}