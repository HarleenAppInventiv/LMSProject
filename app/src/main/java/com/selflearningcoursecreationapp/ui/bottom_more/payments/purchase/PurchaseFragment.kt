package com.selflearningcoursecreationapp.ui.bottom_more.payments.purchase

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentEarningBinding

class PurchaseFragment : BaseFragment<FragmentEarningBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_earning
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
binding.rvList.adapter= PurchaseAdapter()
    }

}