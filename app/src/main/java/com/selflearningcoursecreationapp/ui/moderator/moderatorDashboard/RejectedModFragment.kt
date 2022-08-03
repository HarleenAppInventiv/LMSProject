package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRejectedModBinding

class RejectedModFragment : BaseFragment<FragmentRejectedModBinding>() {
    override fun getLayoutRes() = R.layout.fragment_rejected_mod

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.rvRejectedList.adapter = ModratorDashAdapter()

    }


    override fun onApiRetry(apiCode: String) {
    }


}
