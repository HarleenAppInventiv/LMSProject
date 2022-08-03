package com.selflearningcoursecreationapp.models.course


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderData(
    @SerializedName("amount")
    var amount: Int? = null,
    @SerializedName("courseId")
    var courseId: Int? = null,
    @SerializedName("currency")
    var currency: String? = null,
    @SerializedName("currencySymbol")
    var currencySymbol: String? = null,
    @SerializedName("course")
    var course: CourseData? = null,
    @SerializedName("currencyId")
    var currencyId: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("image")
    var image: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("notes")
    var notes: String? = null,
    @SerializedName("orderId")
    var orderId: String? = null,
    @SerializedName("transactionId")
    var transactionId: String? = null,
    @SerializedName("prefill_Contact")
    var prefillContact: String? = null,
    @SerializedName("createdDateTime")
    var createdDateTime: String? = null,
    @SerializedName("prefill_Email")
    var prefillEmail: String? = null,
    @SerializedName("prefill_Name")
    var prefillName: String? = null,
    @SerializedName("razorpayKey")
    var razorpayKey: String? = null,
    @SerializedName("status")
    var status: Int? = null,
    @SerializedName("themeId")
    var themeId: String? = null
) : Parcelable