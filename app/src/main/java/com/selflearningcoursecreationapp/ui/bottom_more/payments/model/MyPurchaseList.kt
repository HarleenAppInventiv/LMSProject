package com.selflearningcoursecreationapp.ui.bottom_more.payments.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class MyPurchaseDataModel(
    @SerializedName("list") val list: List<MyPurchaseList>?,
    @SerializedName("totalCount") val totalCount: Int?,
    @SerializedName("pageNumber") val pageNumber: Int?,
    @SerializedName("pageSize") val pageSize: Int?,
    @SerializedName("resultCount") val resultCount: Int?

)

@Parcelize
data class MyPurchaseList(
    @SerializedName("transactionId") val transactionId: String?,
    @SerializedName("courseId") val courseId: Int?,
    @SerializedName("orderId") val orderId: String?,
    @SerializedName("receiptId") val receiptId: String?,
    @SerializedName("courseTitle") val courseTitle: String?,
    @SerializedName("modifiedDate") val modifiedDate: String?,
    @SerializedName("createdDate") val createdDate: String?,
    @SerializedName("paymentStatus") val paymentStatus: Int?,
    @SerializedName("amount") val amount: Float?,
    @SerializedName("invoiceURL") val invoiceURL: String?,
    @SerializedName("totalPaymentStatus") val totalPaymentStatus: Int?,
    @SerializedName("currencyCode") val currencyCode: String?,
    @SerializedName("createdByName") val createdByName: String?,
    @SerializedName("createdById") val createdById: Int?,
    @SerializedName("categoryTypeId") val categoryTypeId: Int?,
    @SerializedName("categoryTypeName") val categoryTypeName: String?,
    @SerializedName("averageRating") val averageRating: Float?,
    @SerializedName("courseLogoContentBlurHash") val courseLogoContentBlurHash: String?,
    @SerializedName("courseLogoContentUrl") val courseLogoContentUrl: String?,
    @SerializedName("courseBannerContentBlurHash") val courseBannerContentBlurHash: String?,
    @SerializedName("courseBannerContentURL") val courseBannerContentURL: String?,
    @SerializedName("currencySymbol") val currencySymbol: String?
) : Parcelable
