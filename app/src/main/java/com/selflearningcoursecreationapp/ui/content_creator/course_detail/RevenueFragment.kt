package com.selflearningcoursecreationapp.ui.content_creator.course_detail

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRevenueBinding


class RevenueFragment : BaseFragment<FragmentRevenueBinding>() {
    override fun getLayoutRes() = R.layout.fragment_revenue

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {

        binding.recyerRevenueList.adapter = RevenueListAdapter()
    }

    override fun onApiRetry(apiCode: String) {
    }


}