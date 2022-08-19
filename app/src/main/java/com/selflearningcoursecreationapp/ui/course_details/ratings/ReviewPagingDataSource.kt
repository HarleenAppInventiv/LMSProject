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


            val resource = response.body()?.resource
            count = resource?.totalCount ?: 0
            rating = resource?.averageReview ?: 0f
            val totalPages = count / (this.data.pageSize ?: 1)
            userAlreadyRated = resource?.userAlreadyRated ?: false

            if (data?.isNotEmpty() == true || data != null && data.size != 0) {
                responseData.addAll(data)


            }
            val prevKey = if (pageIndex == 1) null else pageIndex - 1


            val nextKey =
                if ((this.data.pageNumber ?: 1) < totalPages) {
                    pageIndex.plus(1)
                } else {
                    null
                }
            LoadResult.Page(
                data = responseData,
                prevKey = prevKey,
                nextKey = nextKey
            )

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