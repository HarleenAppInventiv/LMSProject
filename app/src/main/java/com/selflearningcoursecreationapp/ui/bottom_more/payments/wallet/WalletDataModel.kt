package com.selflearningcoursecreationapp.ui.bottom_more.payments.wallet

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class WalletDataModel(
    @SerializedName("list") val list: List<AmountList>,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("resultCount") val resultCount: Int
)

@Parcelize
data class AmountList(

    @SerializedName("amount") val amount: Float,
    @SerializedName("modifiedDate") val modifiedDate: String,
    @SerializedName("status") val status: Int,
    @SerializedName("transactionTypeId") val transactionTypeId: Int
) : Parcelable