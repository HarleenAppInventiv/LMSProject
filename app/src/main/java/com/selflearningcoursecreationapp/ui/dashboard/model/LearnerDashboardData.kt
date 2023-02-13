import com.google.gson.annotations.SerializedName


data class LearnerDashboardData(

    @SerializedName("list") var list: List<LearnerDashboardDataList>,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("pageNumber") val pageNumber: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("resultCount") val resultCount: Int
)