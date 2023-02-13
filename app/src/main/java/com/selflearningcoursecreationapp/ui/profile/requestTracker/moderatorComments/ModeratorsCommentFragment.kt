package com.selflearningcoursecreationapp.ui.profile.requestTracker.moderatorComments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModeratorsCommentBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestrackerVM
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CreatedCourseStatus
import com.selflearningcoursecreationapp.utils.ModeratorDashboard
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ModeratorsCommentFragment : BaseFragment<FragmentModeratorsCommentBinding>(), MenuProvider {
    private val viewModel: RequestrackerVM by viewModel()
    private val adapter by lazy {
        ModeratorCommentsAdapter(viewModel.modComment) { type, courseData, position ->
            when (type) {
                Constant.CLICK_VIEW -> {
                    if (courseData.status != CreatedCourseStatus.DRAFT) {

                        findNavController().navigateTo(
                            R.id.action_global_contentCourseDetailFragment,
                            bundleOf(
                                "courseId" to courseData?.courseId,
                                "status" to "moderatorComments",
                                "ModeratorRequestId" to courseData?.courseModeratorId

                            )
                        )
                    }
                }
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        val menuHost: MenuHost = baseActivity
        menuHost.addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.course_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_read -> {
                baseActivity.checkAccessibilityService()

                true
            }
            else -> false
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_moderators_comment
    }


    private fun init() {

        val filterData = GetReviewsRequestModel()
        filterData.pageSize = 20
        filterData.pageNumber = 1


        arguments?.let {
            viewModel.modComment = it.getBoolean("modComment")

            baseActivity.setToolbar(
                title = if (viewModel.modComment) getString(R.string.mod_comments) else getString(
                    R.string.rejectedCourses
                )
            )


            if (!viewModel.modComment) {
                filterData.RequestType = ModeratorDashboard.REJECTED_COURSES_COUNT

                binding.noDataTV.text = getString(R.string.no_course_found)
            } else {
                filterData.RequestType = ModeratorDashboard.MODERATOR_COMMENTS

            }

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRequestResponse(filterData).observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
            }


        }

        binding.rvComments.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            // show empty list
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            binding.noDataTV.isVisible = isListEmpty

            // Only show the list if refresh succeeds.

            // Show loading spinner during initial load or refresh.
            handleLoading(loadState.source.refresh is LoadState.Loading)

            // Show the retry state if initial load or refresh fails.
            // binding.btnRetry.isVisible = loadState.source.refresh is LoadState.Error

            /**
             * loadState.refresh - represents the load state for loading the PagingData for the first time.
             * loadState.prepend - represents the load state for loading data at the start of the list.
             * loadState.append - represents the load state for loading data at the end of the list.
             * */
            // If we have an error, show a toast
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

    private fun handleLoading(b: Boolean) {

        if (b) {
            showLoading()
        } else {
            hideLoading()
        }
    }

    override fun onApiRetry(apiCode: String) {

    }

    fun refreshData() {
        adapter.refresh()
    }

}