package com.selflearningcoursecreationapp.models

//class DocModel : ArrayList<DocModelItem>()
data class DocModelItem(
    val contentId: String,
    val contentName: String,
    val contentURL: String,
    val mediaType: Int,
    val contentSize: Long,
    val userId: Int
)