package com.selflearningcoursecreationapp.ui.bottom_more.payments.model

import com.google.gson.annotations.SerializedName


data class RemainingWalletBalanceData(
    @SerializedName("totalEarning") val totalEarning: Float? = 0f,
    @SerializedName("totalExpenses") val totalExpenses: Float? = 0f,
    @SerializedName("remainingWalletBalance") val remainingWalletBalance: Float? = 0f,
    @SerializedName("currencyCode") val currencyCode: String? = "dfgddf",
    @SerializedName("currencySymbol") val currencySymbol: String? = ""

)
