package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRejectedModBinding
import com.selflearningcoursecreationapp.ui.profile.profileDetails.ProfileDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RejectedModFragment : BaseFragment<FragmentRejectedModBinding>() {
    private val viewModel: ProfileDetailViewModel by viewModel()

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
