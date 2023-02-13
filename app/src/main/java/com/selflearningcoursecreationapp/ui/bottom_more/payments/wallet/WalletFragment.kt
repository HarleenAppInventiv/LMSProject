package com.selflearningcoursecreationapp.ui.bottom_more.payments.wallet

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentEarningBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.bottom_more.payments.PaymentsVM
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletFragment : BaseFragment<FragmentEarningBinding>(), BaseAdapter.IListEnd,
    BaseAdapter.IViewClick {

    private val viewModel: PaymentsVM by viewModel()
    private var mAdapter: WalletAdapter? = null

    override fun getLayoutRes(): Int {
        return R.layout.fragment_earning
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        callMenu()
    }


    private fun initUi() {
        mAdapter?.notifyDataSetChanged()
        mAdapter = null

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        setWalletObserver()

        viewModel.getWalletHistory()

    }

    private fun setAdapter() {
        binding.rvList.visibleView(!viewModel.walletHistoryLiveData.value.isNullOrEmpty())
        binding.noDataTV.visibleView(viewModel.walletHistoryLiveData.value.isNullOrEmpty())


        if (viewModel.walletHistoryLiveData.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter =
                    WalletAdapter(
                        viewModel.walletHistoryLiveData.value ?: ArrayList()
                    )
                binding.rvList.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
                mAdapter?.setOnPageEndListener(this)
            }
        }
    }

    private fun setWalletObserver() {
        viewModel.walletHistoryLiveData.observe(viewLifecycleOwner) {
            setAdapter()

        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onPageEnd(vararg items: Any) {
        viewModel.getWalletHistory()
    }

    override fun onItemClick(vararg items: Any) {

    }

}