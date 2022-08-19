package com.selflearningcoursecreationapp.ui.profile.requestTracker.sentRequests

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSentRequestBinding
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.create_course.co_author.InviteCoAuthorDialog
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestrackerVM
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SentRequestFragment : BaseFragment<FragmentSentRequestBinding>(), BaseDialog.IDialogClick {
    private val viewModel: RequestrackerVM by viewModel()
    var courseData: CourseData? = null

    private val adapter by lazy {
        SentRequestAdapter(viewModel) { type, data, _ ->
            courseData = data
            when (type) {
                0 -> {
                    InviteCoAuthorDialog().apply {
                        arguments = bundleOf("courseId" to data.courseId)
                        setOnDialogClickListener(this@SentRequestFragment)
                    }.show(childFragmentManager, "")
                }
            }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_sent_request

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        arguments?.let {
            viewModel.sentRequestId = it.getInt("sentRequestId")
        }
        val hashMap = HashMap<String, Int>()
        hashMap["PageNumber"] = 1
        hashMap["PageSize"] = 20
        hashMap["RequestType"] = viewModel.sentRequestId


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getRequestResponse(hashMap).observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
            }


        }

        binding.recyelerSentList.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
            binding.noDataTV.isVisible = isListEmpty
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

    private fun handleLoading(b: Boolean) {

        if (b) {
            showLoading()
        } else {
            hideLoading()
        }
    }


    override fun onApiRetry(apiCode: String) {
    }

    override fun onDialogClick(vararg items: Any) {
//        viewModel.onViewEvent(PagerViewEventsRequest.Remove(courseData ?: CourseData()))
        initUI()
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)

    }

}