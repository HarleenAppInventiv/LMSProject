package com.selflearningcoursecreationapp.models.login

//data class login(
//    val `data`: Data,
//    val message: String,
//    val statusCode: Int,
//    val type: String,
//)

data class Data(
    val _id: String,
    val accessToken: String,
    val created: Long,
    val email: String,
    val name: String,
    val profilePicture: String,
    val status: String,
    val userType: String,
)