package com.selflearningcoursecreationapp.ui.moderator


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.google.gson.Gson
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.data.network.exception.ApiException
import com.selflearningcoursecreationapp.data.network.exception.UnAuthorizedException
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import org.json.JSONObject
import java.io.IOException
import kotlin.math.ceil

private const val TMDB_STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 10

class ModeratorDataSource(
    val api: ApiService,
    val data: GetReviewsRequestModel,
    val type: String = "",
) : PagingSource<Int, CourseData>() {


    override fun getRefreshKey(state: PagingState<Int, CourseData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CourseData> {
        val pageIndex = params.key ?: TMDB_STARTING_PAGE_INDEX
        val pageSize = NETWORK_PAGE_SIZE
        data.pageNumber = pageIndex
        data.pageSize = NETWORK_PAGE_SIZE

        val hashmap = HashMap<String, Any>()
        hashmap.put("RequestType", data.RequestType ?: 5)
        hashmap.put("pageNumber", data.pageNumber ?: 1)
        hashmap.put("pageSize", data.pageSize ?: 10)

        return try {
            val response =
                if (type.equals("course_req")) api.courseRequest(data)
                else if (type.equals("mod_Course")) api.modCourse(data)
                else api.requestList(hashmap)

            if (response.isSuccessful) {
                val responseData = mutableListOf<CourseData>()
                val data = response.body()?.resource?.list

                val resource = response.body()?.resource
                count = resource?.totalCount?.toDouble() ?: 0.0


                val totalPages = ceil(count / (this.data.pageSize ?: 1)).toInt()


//                var asd:Double = (finalCount/(this.data.pageSize ?: 1)).toDouble()
//                if(totalPages < asd){
//                    totalPages += 1
//                }

                if (data?.isNotEmpty() == true || data != null && data.size != 0) {
                    responseData.addAll(data)


                }
                val prevKey = if (pageIndex == 1) null else pageIndex - 1


                val nextKey =
                    if (pageIndex < totalPages) {
                        pageIndex.plus(1)
                    } else {
                        null
                    }
                LoadResult.Page(
                    data = responseData,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else {
                val error = Gson().fromJson(
                    JSONObject(
                        JSONObject(
                            response.errorBody()?.string()
                                ?: "{}"
                        ).toString()
                    ).toString(), ApiError::class.java
                )
                when (error.statusCode) {
                    HTTPCode.TOKEN_EXPIRED -> {
                        LoadResult.Error(UnAuthorizedException(error.message))

                    }
                    else -> {
                        LoadResult.Error(ApiException(error.message))
                    }
                }
            }


        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    companion object ReviewTotalCount {
        var count = 0.0
        var rating = 0f
        var finalCount = 0
        var userAlreadyRated: Boolean = false


    }
}