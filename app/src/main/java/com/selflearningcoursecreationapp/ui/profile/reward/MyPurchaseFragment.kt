package com.selflearningcoursecreationapp.ui.profile.reward

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentEarningBinding
import com.selflearningcoursecreationapp.extensions.getFirstLastDateOfMonth
import com.selflearningcoursecreationapp.extensions.getStringDate
import com.selflearningcoursecreationapp.extensions.increaseDecrease
import com.selflearningcoursecreationapp.extensions.openDatePickerDialog
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SearchFieldsItem
import com.selflearningcoursecreationapp.ui.profile.reward.adapter.RewardListAdapter
import com.selflearningcoursecreationapp.ui.profile.reward.viewModel.RewardViewModel
import com.selflearningcoursecreationapp.utils.CommonPayload
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseType
import kotlinx.coroutines.launch
import java.util.*


class MyPurchaseFragment : BaseFragment<FragmentEarningBinding>() {
    override fun getLayoutRes() = R.layout.fragment_earning
    private val viewModel: RewardViewModel by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })

    private var model = GetReviewsRequestModel()
    private var filterFields = arrayListOf<SearchFieldsItem>()

    private lateinit var date: Date

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()

    }

    //6633221  100
    private val rewardListAdapter by lazy {
        RewardListAdapter(Constant.COURSE_COMPLETED_REWARD) {
            findNavController().navigate(
                R.id.action_reward_to_courseDetailsFragment,
                bundleOf("courseId" to it.courseId)
            )
        }
    }


    private fun initUI() {


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

        binding.tvMonth.setOnClickListener {
            requireContext().openDatePickerDialog {
                date = it.time
                addDataIntoPayload(false)

            }

        }

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
        model.courseType = CourseType.REWARD_POINTS_PURCHASED_COURSES
        filterFields.add(
            SearchFieldsItem(
                CommonPayload.PUBLISHED_DATE,
                CommonPayload.OPERATOR_TYPE_6,
                arrayListOf("'${getFirstLastDateOfMonth(true, date).getStringDate("yyyy-MM-dd")}'")
            )
        )
        filterFields.add(
            SearchFieldsItem(
                CommonPayload.PUBLISHED_DATE,
                CommonPayload.OPERATOR_TYPE_7,
                arrayListOf("'${getFirstLastDateOfMonth(false, date).getStringDate("yyyy-MM-dd")}'")
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



        }
    }


}