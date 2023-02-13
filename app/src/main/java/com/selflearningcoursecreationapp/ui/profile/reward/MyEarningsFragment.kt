package com.selflearningcoursecreationapp.ui.profile.reward

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentEarningBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SearchFieldsItem
import com.selflearningcoursecreationapp.ui.profile.reward.adapter.RewardListAdapter
import com.selflearningcoursecreationapp.ui.profile.reward.viewModel.RewardViewModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CommonPayload
import com.selflearningcoursecreationapp.utils.CourseType
import kotlinx.coroutines.launch
import java.util.*


class MyEarningsFragment : BaseFragment<FragmentEarningBinding>() {
    override fun getLayoutRes() = R.layout.fragment_earning
    private val viewModel: RewardViewModel by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })

    private var model = GetReviewsRequestModel()
    private var filterFields = arrayListOf<SearchFieldsItem>()

    private lateinit var date: Date

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }

    //6633221100
    private val rewardListAdapter by lazy {
        RewardListAdapter(0) {
        }
    }


    private fun initUI() {

        binding.rvList.visible()
        binding.tvMonth.visible()
        binding.imgRight.visible()
        binding.imgLeft.visible()
        binding.tvTitle.visible()
        binding.tvMonthCourse.visible()
        model.transactionTypeId = 4
        addDataIntoPayload(true)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRewardList(model).observe(viewLifecycleOwner) {
                rewardListAdapter.submitData(lifecycle, it)
            }
        }
        binding.imgLeft.setOnClickListener {
            performFilterOptions(false)


        }
        binding.imgRight.setOnClickListener {
            performFilterOptions(true)


        }

//        binding.tvMonth.setOnClickListener {
//            requireContext().openDatePickerDialog {
//                date = it.time
//                addDataIntoPayload(false)
//
//            }
//
//        }

        binding.rvList.adapter = rewardListAdapter

        rewardListAdapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && rewardListAdapter.itemCount == 0
            binding.noDataTV.isVisible = isListEmpty

            // Only show the list if refresh succeeds.

            // Show loading spinner during initial load or refresh.
            handleLoading(loadState.source.refresh is LoadState.Loading)

            // Show the retry state if initial load or refresh fails.
            // binding.btnRetry.isVisible = loadState.source.refresh is LoadState.Error

            // If we have an error, show a toast
            val errorState = when {
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }

            errorState?.let {
                handlePagingError(it, ApiEndPoints.API_REWARDS_POINTS)

            }

        }

    }

    private fun performFilterOptions(isClicked: Boolean) {
        date = increaseDecrease(isClicked, date)
        addDataIntoPayload(false)
    }

    private fun addDataIntoPayload(isFirstTime: Boolean) {
        if (isFirstTime) {
            val c = System.currentTimeMillis()
            date = Date(c)
        }
        filterFields.clear()
        binding.tvMonthCourse.text =
            "${getString(R.string.courses_list_for)} ${date.getStringDate("MMMM yyyy")}"
        binding.tvMonth.text = date.getStringDate("MMMM")
        model.courseType = CourseType.REWARD_POINTS_EARNED_COURSES
        filterFields.add(
            SearchFieldsItem(
                CommonPayload.CREATED_DATE,
                CommonPayload.OPERATOR_TYPE_6,
                arrayListOf(
                    "'${
                        getFirstLastDateOfMonth(true, date).getStringDate("yyyy-MM-dd")
                            .convertToUtc()
                    }'"
                )
            )
        )
        filterFields.add(
            SearchFieldsItem(
                CommonPayload.CREATED_DATE,
                CommonPayload.OPERATOR_TYPE_7,
                arrayListOf(
                    "'${
                        getFirstLastDateOfMonth(false, date).getStringDate("yyyy-MM-dd")
                            .convertToUtc()
                    }'"
                )
            )
        )
        model.searchFields = filterFields

        if (!isFirstTime) {
            rewardListAdapter.refresh()
        }
    }

    override fun onApiRetry(apiCode: String) {
        rewardListAdapter.retry()

    }

    private fun handleLoading(loading: Boolean) {

        if (loading) {
            showLoading()
        } else {
            hideLoading()
            viewModel.rewardPoints.value = RewardPagingDataSource.rewardPoints
            viewModel.totalSpendRewards.value = RewardPagingDataSource.totalSpendRewards
            viewModel.totalEarnAsACreatorRewards.value =
                RewardPagingDataSource.totalEarnAsACreatorRewards
            viewModel.totalEarnAsALearnerRewards.value =
                RewardPagingDataSource.totalEarnAsALearnerRewards


        }
    }

    override fun onResume() {
        super.onResume()
        rewardListAdapter.refresh()
    }

    fun onRefreshData() {
        rewardListAdapter.refresh()
    }

}