package com.selflearningcoursecreationapp.ui.bottom_more.payments.model

import com.google.gson.annotations.SerializedName

data class PaymentEarningsData(
    @SerializedName("courseId") val courseId: Int,
    @SerializedName("courseTitle") val courseTitle: String,
    @SerializedName("courseLogoContentBlurHash") val courseLogoContentBlurHash: String,
    @SerializedName("courseLogoContentUrl") val courseLogoContentUrl: String,
    @SerializedName("courseBannerContentBlurHash") val courseBannerContentBlurHash: String,
    @SerializedName("courseBannerContentURL") val courseBannerContentURL: String,
    @SerializedName("createdDate") val createdDate: String,
    @SerializedName("modifiedDate") val modifiedDate: String,
    @SerializedName("publishdDate") val publishdDate: String,
    @SerializedName("amount") val amount: Float,
    @SerializedName("totalLearner") val totalLearner: Int,
    @SerializedName("courseTypeId") val courseTypeId: Int,
    @SerializedName("currencyCode") val currencyCode: String,
    @SerializedName("currencySymbol") val currencySymbol: String
)

data class MyPaymentEarningDataModel(
    @SerializedName("list") val list: ArrayList<PaymentEarningsData>?,
    @SerializedName("totalCount") val totalCount: Int?,
    @SerializedName("pageNumber") val pageNumber: Int?,
    @SerializedName("pageSize") val pageSize: Int?,
    @SerializedName("resultCount") val resultCount: Int?

)
