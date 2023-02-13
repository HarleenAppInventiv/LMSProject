package com.selflearningcoursecreationapp.ui.profile.requestTracker.paymentWithdrawls

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModeratorsCommentBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestrackerVM
import org.koin.androidx.viewmodel.ext.android.viewModel


class PaymentWithdrawlsFRagment : BaseFragment<FragmentModeratorsCommentBinding>(), MenuProvider,
    BaseAdapter.IListEnd {
    private val viewModel: RequestrackerVM by viewModel()
    private var mAdapter: PaymentWithdrawlsAdapter? = null

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


        mAdapter?.notifyDataSetChanged()
        mAdapter = null

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        paymentWithdrawlListObserver()

        viewModel.getPaymentWithdrawlList()

    }

    private fun paymentWithdrawlListObserver() {
        viewModel.paymentWithdrawLiveDataList.observe(viewLifecycleOwner) {
            setAdapter()

        }
    }

    private fun setAdapter() {
        binding.rvComments.visibleView(!viewModel.paymentWithdrawLiveDataList.value.isNullOrEmpty())
        binding.noDataTV.visibleView(viewModel.paymentWithdrawLiveDataList.value.isNullOrEmpty())


        if (viewModel.paymentWithdrawLiveDataList.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter = PaymentWithdrawlsAdapter(
                    viewModel.paymentWithdrawLiveDataList.value ?: ArrayList()
                )
                binding.rvComments.adapter = mAdapter

                mAdapter?.setOnPageEndListener(this)
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onPageEnd(vararg items: Any) {
        viewModel.getPaymentWithdrawlList()
    }


}