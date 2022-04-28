package com.selflearningcoursecreationapp.ui.bottom_more.payments.wallet

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
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
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }


    private fun initUi() {
        binding.rvList.adapter = WalletAdapter()
    }

}