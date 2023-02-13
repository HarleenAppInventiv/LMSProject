import com.google.gson.annotations.SerializedName


data class RevenueDataModel(

    @SerializedName("list") var revenueList: ArrayList<RevenueDataList>? = null,
    @SerializedName("totalCount") var totalCount: Int? = 0,
    @SerializedName("pageNumber") var pageNumber: Int? = 0,
    @SerializedName("pageSize") var pageSize: Int? = 0,
    @SerializedName("resultCount") var resultCount: Int? = 0,
    @SerializedName("courseRevenue") var courseRevenue: Float = 0f
)