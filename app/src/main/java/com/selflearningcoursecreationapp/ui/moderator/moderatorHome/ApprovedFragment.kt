package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentApprovedBinding
import com.selflearningcoursecreationapp.utils.ModeratorListType


class ApprovedFragment : BaseFragment<FragmentApprovedBinding>() {

    override fun getLayoutRes() = R.layout.fragment_approved
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.rvRequestedList.adapter = AdapterRequestList(ModeratorListType.APPROVED)

    }

    override fun onApiRetry(apiCode: String) {
    }


}