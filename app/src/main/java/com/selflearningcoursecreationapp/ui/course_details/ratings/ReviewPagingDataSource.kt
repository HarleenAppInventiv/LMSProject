package com.selflearningcoursecreationapp.ui.course_details.ratings


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import java.io.IOException

private const val TMDB_STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 10

class ReviewPagingDataSource(
    val api: ApiService,
    val data: GetReviewsRequestModel

) : PagingSource<Int, CourseData>() {


    override fun getRefreshKey(state: PagingState<Int, CourseData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CourseData> {
        val pageIndex = params.key ?: TMDB_STARTING_PAGE_INDEX
        data.pageNumber = pageIndex
        data.pageSize = NETWORK_PAGE_SIZE



        return try {
            val response = api.getReviewList(data)
            val responseData = mutableListOf<CourseData>()
            val data = response.body()?.resource?.list
            val nextKey =
                if (data == null || data.isEmpty()) {
                    null
                } else {
                    pageIndex.plus(1)
                }
            if (data?.isNotEmpty() == true || data != null && data.size != 0) {
                responseData.addAll(data)
                count = response.body()?.resource?.totalCount ?: 0
                rating = response.body()?.resource?.averageReview ?: 0f
                userAlreadyRated = response.body()?.resource?.userAlreadyRated ?: false
                val prevKey = if (pageIndex == 1) null else pageIndex - 1


                LoadResult.Page(
                    data = responseData,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } else {

                val prevKey = if (pageIndex == 1) null else pageIndex - 1

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

    companion object ReviewTotalCount {
        var count = 0
        var rating = 0f
        var userAlreadyRated: Boolean = false

        fun clear() {
            count = 0
            rating = 0f
            userAlreadyRated = false
        }

    }
}