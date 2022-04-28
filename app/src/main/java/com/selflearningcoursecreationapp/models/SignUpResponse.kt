package com.selflearningcoursecreationapp.models

data class SignUpResponse(
    val errorNo: String,
    val message: String,
    val identity: String,
)
