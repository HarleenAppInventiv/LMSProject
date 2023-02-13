package com.selflearningcoursecreationapp.ui.moderator.moderatorHome.pending

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
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentPendingModBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.moderator.dialog.ReasonForRejectionDialogue
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.ModHomeVM
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.accepted.AdapterRequestList
import com.selflearningcoursecreationapp.utils.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PendingModFragment : BaseFragment<FragmentPendingModBinding>() {

    private val viewModel: ModPendingVM by viewModel()
    private val parentVM: ModHomeVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })

    private val adapter by lazy {
        AdapterRequestList(ModHomeConst.PENDING) { type, data, _ ->
            when (type) {
                Constant.CLICK_ACCEPT -> {
                    if (data.isCommentAdded == true) {
                        showToastShort(baseActivity.getString(R.string.you_cant_approve_commented_course))
                    } else {
                        viewModel.updateCourseStatus(CoAuthorStatus.ACCEPT, data)
                    }
                }
                Constant.CLICK_REJECT -> {
                    ReasonForRejectionDialogue().apply {
                        setOnDialogClickListener(object : BaseBottomSheetDialog.IDialogClick {
                            override fun onDialogClick(vararg items: Any) {
                                val comment = items[0] as String
                                viewModel.updateCourseStatus(CoAuthorStatus.REJECT, data, comment)

                            }

                        })
                    }.show(childFragmentManager, "")

                }
                Constant.CLICK_VIEW -> {
                    findNavController().navigateTo(
                        R.id.action_moderatorBaseFragment_to_modCourseDetailsFragment,
                        bundleOf(
                            "courseId" to data.courseId,
                            "status" to data.status,
                            "requestType" to ModHomeConst.PENDING,
                            "id" to data.id
                        )
                    )
                }
            }

        }


    }

    override fun getLayoutRes() = R.layout.fragment_pending_mod

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
//            viewModel.filterData.status=MODSTATUS.PENDING
            showLog("MOD_HOME", "parent pending refresh")

            adapter.refresh()
        })
        viewModel.refreshData.observe(viewLifecycleOwner, Observer {
            if (adapter.itemCount == 0) {
                binding.noDataTV.visible()
                binding.swipeRefresh.isRefreshing = false

            }

        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        listenParentData()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
        viewModel.filterData.pageSize = 20
        viewModel.filterData.pageNumber = 1
        viewModel.filterData.status = MODSTATUS.PENDING

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pendingCourse().observe(viewLifecycleOwner) {

                adapter.submitData(lifecycle, it)
                showLog("MOD_HOME", "paging data >>> ${adapter.itemCount} ")
            }
        }

        binding.rvPendingList.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            binding.noDataTV.isVisible = isListEmpty
            binding.rvPendingList.isVisible = !isListEmpty
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

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_UPDATE_MOD_COURSE_STATUS -> {
                showToastShort((value as BaseResponse<CourseData>).message)
            }
        }
    }

}

