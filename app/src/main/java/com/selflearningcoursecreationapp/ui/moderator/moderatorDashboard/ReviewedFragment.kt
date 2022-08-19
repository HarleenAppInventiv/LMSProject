package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentReviewedBinding
import com.selflearningcoursecreationapp.ui.profile.profileDetails.ProfileDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReviewedFragment : BaseFragment<FragmentReviewedBinding>() {
    private val viewModel: ProfileDetailViewModel by viewModel()

    override fun getLayoutRes() = R.layout.fragment_reviewed
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {

        binding.rvReviewedList.adapter = ModratorDashAdapter()
    }

    override fun onApiRetry(apiCode: String) {
    }


}