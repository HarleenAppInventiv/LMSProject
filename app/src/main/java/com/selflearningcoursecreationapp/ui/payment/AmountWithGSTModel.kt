package com.selflearningcoursecreationapp.ui.payment

import com.google.gson.annotations.SerializedName


data class AmountWithGSTModel(

    @SerializedName("stateId") val stateId: Int? = 0,
    @SerializedName("actualAmount") val actualAmount: Float? = 0f,
    @SerializedName("gst") val gst: Float? = 0f,
    @SerializedName("gstAmount") val gstAmount: Float? = 0f,
    @SerializedName("amountWithGST") val amountWithGST: Float? = 0f
)