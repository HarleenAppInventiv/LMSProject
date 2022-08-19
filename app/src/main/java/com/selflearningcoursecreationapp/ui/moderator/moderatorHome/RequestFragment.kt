package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRequestBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class RequestFragment : BaseFragment<FragmentRequestBinding>() {
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

    //    lateinit var adapter: AdapterRequestList
    override fun getLayoutRes() = R.layout.fragment_request
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        val hashMap = HashMap<String, Int>()
        hashMap["PageNumber"] = 1
        hashMap["PageSize"] = 20
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.courseRequest(hashMap).observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
            }
        }

        binding.rvRequestedList.adapter = adapter

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

//    override fun onItemClick(vararg items: Any) {
//        var type = items[0] as Int
//        var position = items[1] as Int
//
//        when (type) {
//            Constant.CLICK_ACCEPT -> {
//                findNavController().navigate(R.id.action_moderatorBaseFragment_to_modCourseDetailsFragment)
//            }
//            Constant.CLICK_REJECT -> {
////                findNavController().navigate(R.id.action_moderatorBaseFragment_to_notificationFragment)
//                ReasonForRejectionDialogue().show(childFragmentManager, "")
//            }
//
//        }
//    }


}