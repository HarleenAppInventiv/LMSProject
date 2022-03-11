package com.selflearningcoursecreationapp.ui.bottom_more.payments.wallet

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentEarningBinding

class WalletFragment : BaseFragment<FragmentEarningBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_earning
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
binding.rvList.adapter= WalletAdapter()
    }

}