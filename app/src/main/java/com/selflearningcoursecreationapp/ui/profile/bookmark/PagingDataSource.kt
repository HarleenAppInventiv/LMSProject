package com.selflearningcoursecreationapp.ui.profile.bookmark


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
import org.json.JSONObject
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 10

class PagingDataSource(val api: ApiService) : PagingSource<Int, CourseData>() {



    override fun getRefreshKey(state: PagingState<Int, CourseData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CourseData> {
        val pageIndex = params.key ?: STARTING_PAGE_INDEX
        val map = hashMapOf<String, Any>()
        map["PageNumber"] = pageIndex
        map["PageSize"] = NETWORK_PAGE_SIZE

        return try {
            val response = api.getWishListData(
                map
            )
            if (response.isSuccessful) {
                val responseData = mutableListOf<CourseData>()

                val resource = response.body()?.resource
                val count = resource?.totalCount ?: 0
                val totalPages = count / (NETWORK_PAGE_SIZE ?: 1)

                val data = response.body()?.resource?.list
                val prevKey = if (pageIndex == 1) null else pageIndex - 1


                val nextKey =
                    if (pageIndex < totalPages) {
                        pageIndex.plus(1)
                    } else {
                        null
                    }
                if (data?.isNotEmpty() == true || data != null) {
                    responseData.addAll(data)


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
}