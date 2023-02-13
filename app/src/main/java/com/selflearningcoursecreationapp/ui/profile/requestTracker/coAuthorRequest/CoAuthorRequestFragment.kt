package com.selflearningcoursecreationapp.ui.profile.requestTracker.coAuthorRequest

import android.os.Bundle
import android.view.View
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.databinding.FragmentCoAuthorRequestBinding
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestrackerVM
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CoAuthorStatus
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class CoAuthorRequestFragment : BaseFragment<FragmentCoAuthorRequestBinding>(), MenuProvider {
    private val viewModel: RequestrackerVM by viewModel()
    var courseData: CourseData? = null
    private val adapter by lazy {
        CoAuthorRequestAdapter(viewModel) { type, wishListItem, pos ->
            viewModel.courseId = wishListItem.courseId ?: 0
            viewModel.position = pos
            courseData = wishListItem

            when (type) {
                0 -> {
                    viewModel.status = CoAuthorStatus.ACCEPT
                    viewModel.manageInvitation()
                }
                1 -> {
                    viewModel.status = CoAuthorStatus.REJECT
                    viewModel.manageInvitation()

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callMenu()
        init()
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_co_author_request
    }

    private fun init() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        arguments?.let {
            viewModel.coAuthorRequestId = it.getInt("coAuthorRequestId")
        }

        val filterData = GetReviewsRequestModel()
        filterData.pageSize = 20
        filterData.pageNumber = 1

        filterData.RequestType = viewModel.coAuthorRequestId



        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRequestResponse(filterData).observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
            }
        }

        binding.rvRequests.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            binding.noDataTV.isVisible = isListEmpty
            binding.rvRequests.isVisible = !isListEmpty
            handleLoading(loadState.source.refresh is LoadState.Loading)

            val errorState = when {
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            errorState?.let {

            }
        }


    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_COAUTHOR_INVITATION -> {
                showToastShort((value as BaseResponse<UserProfile>).message)
//                viewModel.onViewEvent(PagerViewEventsRequest.Remove(courseData ?: CourseData()))
//                if (adapter.itemCount == 0) {
//                    binding.noDataTV.visible()
//                    binding.rvRequests.gone()
                adapter.refresh()
//                }
            }

        }
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        super.onException(isNetworkAvailable, exception, apiCode)
        when (apiCode) {
            ApiEndPoints.API_COAUTHOR_INVITATION -> {


                adapter.refresh()

            }

        }
    }

    private fun handleLoading(b: Boolean) {

        if (b) {
            showLoading()
        } else {
            hideLoading()
        }
    }


    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)

    }

    fun refreshData() {

        adapter.refresh()

    }


}