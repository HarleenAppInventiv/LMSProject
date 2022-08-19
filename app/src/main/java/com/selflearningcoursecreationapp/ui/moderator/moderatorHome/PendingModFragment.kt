package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPendingModBinding
import com.selflearningcoursecreationapp.utils.MODSTATUS
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PendingModFragment : BaseFragment<FragmentPendingModBinding>() {

    private val viewModel: ModHomeVM by viewModel()

    private val adapter by lazy {
        AdapterRequestList(viewModel) { type, wishListItem, _ ->
            when (type) {
                0 -> {
//                    InviteCoAuthorDialog().apply {
//                        arguments = bundleOf("courseId" to wishListItem.courseId)
//                    }.show(childFragmentManager, "")
                }
            }
        }
    }

    override fun getLayoutRes() = R.layout.fragment_pending_mod

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        val hashMap = HashMap<String, Int>()
        hashMap["PageNumber"] = 1
        hashMap["PageSize"] = 20
        hashMap["status"] = MODSTATUS.PENDING
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pendingCourse(hashMap).observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
            }
        }

        binding.rvPendingList.adapter = adapter

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

}

