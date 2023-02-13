package com.selflearningcoursecreationapp.ui.bottom_more.payments.model

import com.google.gson.annotations.SerializedName
import com.selflearningcoursecreationapp.models.course.CourseData

data class PurchaseDetailDataModel(
    @SerializedName("transactionId") val transactionId: String? = "",
    @SerializedName("orderId") val orderId: String? = "",
    @SerializedName("createdDateTime") val createdDateTime: String? = "",
    @SerializedName("status") val status: Int? = 0,
    @SerializedName("amount") val amount: Int? = 0,
    @SerializedName("currencyId") val currencyId: String? = "",
    @SerializedName("currencySymbol") val currencySymbol: String? = "",
    @SerializedName("course") val course: CourseData? = null,
    @SerializedName("invoiceURL") val invoiceURL: String,

    )
