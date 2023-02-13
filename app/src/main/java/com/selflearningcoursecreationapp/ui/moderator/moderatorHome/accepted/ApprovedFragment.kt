package com.selflearningcoursecreationapp.ui.moderator.moderatorHome.accepted

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentApprovedBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.AdapterAcceptedRejectedList
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.ModHomeVM
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.MODSTATUS
import com.selflearningcoursecreationapp.utils.ModHomeConst
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ApprovedFragment : BaseFragment<FragmentApprovedBinding>() {
    private val viewModel: ModApprovedVM by viewModel()
    private val parentVM: ModHomeVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })

    private val adapter by lazy {
        AdapterAcceptedRejectedList(viewModel, MODSTATUS.ACCEPTED) { type, data, _ ->
            when (type) {
                0 -> {
//                    InviteCoAuthorDialog().apply {
//                        arguments = bundleOf("courseId" to wishListItem.courseId)
//                    }.show(childFragmentManager, "")
                }
                Constant.CLICK_VIEW -> {
                    findNavController().navigateTo(
                        R.id.action_moderatorBaseFragment_to_modCourseDetailsFragment,
                        bundleOf(
                            "courseId" to data.courseId,
                            "status" to data.status,
                            "requestType" to ModHomeConst.APPROVED,
                            "id" to data.id
                        )
                    )
                }
            }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_approved
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.filterData.pageSize = 20
        viewModel.filterData.pageNumber = 1
        viewModel.filterData.status = MODSTATUS.ACCEPTED
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.acceptCourse().observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
            }
        }

        binding.rvRequestedList.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            binding.noDataTV.isVisible = isListEmpty
            binding.rvRequestedList.isVisible = !isListEmpty
            handleLoading(loadState.source.refresh is LoadState.Loading)

            val errorState = when {
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            errorState?.let {
                handlePagingError(it, ApiEndPoints.API_MOD_COURSES)

            }
        }
        listenParentData()

    }


    override fun onResume() {
        super.onResume()
        adapter.refresh()
    }

    private fun listenParentData() {
        parentVM.refreshData.observe(viewLifecycleOwner, Observer {
            viewModel.filterData.searchFields = parentVM.filterData.searchFields
            viewModel.filterData.searchFields?.forEach {
                if (it.fieldName == "createdDate") it.fieldName = "modifiedDate"
            }
            viewModel.filterData.generalSearchField = parentVM.filterData.generalSearchField

//            viewModel.filterData.pageSize=20
//            viewModel.filterData.pageNumber=1
//            viewModel.filterData.status=MODSTATUS.ACCEPTED
            adapter.refresh()
        })

        viewModel.refreshData.observe(viewLifecycleOwner, Observer {
            if (adapter.itemCount == 0) {
                binding.noDataTV.visible()
                binding.swipeRefresh.isRefreshing = false

            }

        })
    }

    private fun handleLoading(b: Boolean) {
        if (b) {
            if (viewModel.filterData.pageNumber == 1) {
                binding.swipeRefresh.isRefreshing = true
            } else {
                showLoading()
            }
        } else {
            hideLoading()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onApiRetry(apiCode: String) {
        adapter.refresh()
    }


}