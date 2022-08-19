package com.selflearningcoursecreationapp.ui.profile.requestTracker.moderatorComments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModeratorsCommentBinding
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestrackerVM
import com.selflearningcoursecreationapp.utils.ModeratorDashboard
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ModeratorsCommentFragment : BaseFragment<FragmentModeratorsCommentBinding>() {
    private val viewModel: RequestrackerVM by viewModel()
    private val adapter by lazy {
        ModeratorCommentsAdapter(viewModel) { type, wishListItem, _ ->
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_moderators_comment
    }


    private fun init() {
        val hashMap = HashMap<String, Int>()
        hashMap["PageNumber"] = 1
        hashMap["PageSize"] = 20


        arguments?.let {
            viewModel.modComment = it.getBoolean("modComment")
            if (!viewModel.modComment) {
                hashMap["RequestType"] = ModeratorDashboard.REJECTED_COURSES_COUNT

                binding.noDataTV.text = getString(R.string.no_course_found)
            } else {
                hashMap["RequestType"] = ModeratorDashboard.MODERATOR_COMMENTS

            }
            baseActivity.setToolbar(
                title = if (viewModel.modComment) getString(R.string.mod_comments) else getString(
                    R.string.rejectedCourses
                )
            )

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRequestResponse(hashMap).observe(viewLifecycleOwner) {
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

}