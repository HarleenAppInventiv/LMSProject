package com.selflearningcoursecreationapp.ui.profile.bookmark


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.models.course.CourseData
import java.io.IOException

private const val TMDB_STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 10

class PagingDataSource(val api: ApiService) : PagingSource<Int, CourseData>() {


    override fun getRefreshKey(state: PagingState<Int, CourseData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CourseData> {
        val pageIndex = params.key ?: TMDB_STARTING_PAGE_INDEX
        val map = hashMapOf<String, Any>()
        map["PageNumber"] = pageIndex
        map["PageSize"] = NETWORK_PAGE_SIZE

        return try {
            val response = api.getWishListData(
                map
            )
            val responseData = mutableListOf<CourseData>()

            val data = response.body()?.resource?.list
            val nextKey =
                if (data == null || data.isEmpty()) {
                    null
                } else {
                    // By default, initial load size = 3 * NETWORK PAGE SIZE
                    // ensure we're not requesting duplicating items at the 2nd request
                    pageIndex.plus(1)
                }
            if (data?.isNotEmpty() == true || data != null) {
                responseData.addAll(data)

                val prevKey = if (pageIndex == 0) null else pageIndex - 1

                LoadResult.Page(
                    data = responseData,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else {

                val prevKey = if (pageIndex == 0) null else pageIndex - 1

                LoadResult.Page(
                    data = responseData,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }


        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}