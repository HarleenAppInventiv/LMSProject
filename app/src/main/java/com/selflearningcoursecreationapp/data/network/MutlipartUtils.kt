package com.selflearningcoursecreationapp.data.network

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

val TEXT_PLAIN = "text/plain"
fun String?.getRequestBody(type: String = TEXT_PLAIN): RequestBody? {
    return this?.toRequestBody((type).toMediaTypeOrNull())
}

fun JSONObject?.getRequestBody(): RequestBody? {
    return this?.toString()?.toRequestBody(("application/json; charset=utf-8").toMediaTypeOrNull())
}

fun Int?.getRequestBody(): RequestBody? {
    return this?.toString()?.toRequestBody((TEXT_PLAIN).toMediaTypeOrNull())
}

fun Long?.getRequestBody(): RequestBody? {
    return this?.toString()?.toRequestBody((TEXT_PLAIN).toMediaTypeOrNull())
}

fun File.getMultiPartBody(param: String): MultipartBody.Part {
    val requestFile = this.asRequestBody("image/*".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(
        param,
        name,
        requestFile
    )
}