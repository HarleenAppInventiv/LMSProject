package com.selflearningcoursecreationapp.ui.profile.reward


import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.selflearningcoursecreationapp.data.network.ApiService
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.Rating
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import java.io.IOException

private const val TMDB_STARTING_PAGE_INDEX = 1
private const val NETWORK_PAGE_SIZE = 10

class RewardPagingDataSource(
    val api: ApiService,
    val data: GetReviewsRequestModel

) : PagingSource<Int, CourseData>() {

    var liveData = MutableLiveData<Rating>()

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
            val response = api.getRewardList(
                data
            )
            val responseData = mutableListOf<CourseData>()

            val data = response.body()?.resource?.courses?.list
            val nextKey =
                if (data == null || data.isEmpty()) {
                    null
                } else {
                    // By default, initial load size = 3 * NETWORK PAGE SIZE
                    // ensure we're not requesting duplicating items at the 2nd request
                    pageIndex.plus(1)


                }

            userProfile = response.body()?.resource?.userProfile
            rewardPoints = response.body()?.resource?.rewardPoints ?: 0
            if (data?.isNotEmpty() == true || data != null && data.size != 0) {
                responseData.addAll(data)


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
        var userProfile: UserProfile? = null
        var rewardPoints: Long = 0


    }
}