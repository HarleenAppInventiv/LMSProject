import com.google.gson.annotations.SerializedName


data class RevenueDataList(

    @SerializedName("learnerName") var learnerName: String? = "",
    @SerializedName("enrolledDate") var enrolledDate: String? = "",
    @SerializedName("amount") var amount: Float? = 0f,
    @SerializedName("courseRating") var courseRating: Float? = 0f,
    @SerializedName("currencyCode") var currencyCode: String? = "",
    @SerializedName("currencySymbol") var currencySymbol: String? = "",
    @SerializedName("profileUrl") var profileUrl: String? = "",
    @SerializedName("profileBlurHash") var profileBlurHash: String? = "",
    @SerializedName("invoiceURL") val invoiceURL: String,

    )