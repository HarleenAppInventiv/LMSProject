package com.selflearningcoursecreationapp.ui.moderator.moderatorHome.request

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
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentRequestBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.SearchFieldsItem
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.ModHomeVM
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.accepted.AdapterRequestList
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CoAuthorStatus
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ModHomeConst
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class RequestFragment : BaseFragment<FragmentRequestBinding>() {
    private val viewModel: ModRequestVM by viewModel()
    private val parentVM: ModHomeVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    private val adapter by lazy {
        AdapterRequestList(ModHomeConst.REQUEST) { type, data, _ ->
            showLog("MOD_HOME", " CALLBACK")
            when (type) {
                Constant.CLICK_ACCEPT -> {
                    viewModel.updateRequestStatus(CoAuthorStatus.ACCEPT, data)

                }
                Constant.CLICK_REJECT -> {
                    viewModel.updateRequestStatus(CoAuthorStatus.REJECT, data)
//                    parentVM.switchTab.postValue(true)
                }
                Constant.CLICK_VIEW -> {
                    findNavController().navigateTo(
                        R.id.action_moderatorBaseFragment_to_modCourseDetailsFragment,
                        bundleOf(
                            "courseId" to data.courseId,
                            "status" to data.statusName,
                            "requestType" to ModHomeConst.REQUEST,
                            "id" to data.id
                        )
                    )
                }
            }
        }
    }

    //    lateinit var adapter: AdapterRequestList
    override fun getLayoutRes() = R.layout.fragment_request
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
        }
        listenParentData()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.filterData.pageSize = 20
        viewModel.filterData.pageNumber = 1
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.courseRequest().observe(viewLifecycleOwner) {
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
                handlePagingError(it, ApiEndPoints.API_COURSE_REQUEST)

            }
        }


    }


    private fun listenParentData() {
        parentVM.refreshData.observe(viewLifecycleOwner, Observer {


            viewModel.filterData.searchFields =
                parentVM.filterData.searchFields?.clone() as ArrayList<SearchFieldsItem>?
            viewModel.filterData.searchFields?.forEach {
                if (it.fieldName == "modifiedDate") it.fieldName = "createdDate"
            }
            viewModel.filterData.generalSearchField = parentVM.filterData.generalSearchField
//            viewModel.filterData.pageSize=20
//            viewModel.filterData.pageNumber=1
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


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_UPDATE_MOD_REQUEST -> {
                showToastShort((value as BaseResponse<CourseData>).message)
                parentVM.switchTab.postValue(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.refresh()
    }


}