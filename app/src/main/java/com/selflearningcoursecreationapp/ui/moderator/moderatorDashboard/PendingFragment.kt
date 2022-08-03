package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPendingBinding


class PendingFragment : BaseFragment<FragmentPendingBinding>() {
    override fun getLayoutRes() = R.layout.fragment_pending
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.rvPendingList.adapter = ModratorDashAdapter()

    }

    override fun onApiRetry(apiCode: String) {
    }

}