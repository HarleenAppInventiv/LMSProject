package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRejectedBinding
import com.selflearningcoursecreationapp.utils.ModeratorListType

class RejectedFragment : BaseFragment<FragmentRejectedBinding>() {

    override fun getLayoutRes() = R.layout.fragment_rejected

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.rvRequestedList.adapter = AdapterRequestList(ModeratorListType.REJECTED)


    }

    override fun onApiRetry(apiCode: String) {
    }


}