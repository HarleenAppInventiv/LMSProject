import com.google.gson.annotations.SerializedName


data class ModDashboardData(

    @SerializedName("list") var list: List<ModDashboardDataList>,
    @SerializedName("totalCount") var totalCount: Int,
    @SerializedName("pageNumber") var pageNumber: Int,
    @SerializedName("pageSize") var pageSize: Int,
    @SerializedName("resultCount") var resultCount: Int
)