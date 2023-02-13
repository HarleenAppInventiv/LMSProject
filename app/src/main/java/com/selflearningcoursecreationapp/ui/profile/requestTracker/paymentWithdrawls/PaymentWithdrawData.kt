package com.selflearningcoursecreationapp.ui.profile.requestTracker.paymentWithdrawls

import com.google.gson.annotations.SerializedName

data class PaymentWithdrawData(
    @SerializedName("list") val list: List<PaymentWithdrawList>,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("resultCount") val resultCount: Int

)

data class PaymentWithdrawList(

    @SerializedName("id") val id: Int,
    @SerializedName("rowNum") val rowNum: Int,
    @SerializedName("requestId") val requestId: String,
    @SerializedName("status") val status: Int,
    @SerializedName("createdDate") val createdDate: String,
    @SerializedName("amount") val amount: Float,
    @SerializedName("maskAccountNumber") val maskAccountNumber: String,
    @SerializedName("bankName") val bankName: String,
    @SerializedName("modifiedDate") val modifiedDate: String
)