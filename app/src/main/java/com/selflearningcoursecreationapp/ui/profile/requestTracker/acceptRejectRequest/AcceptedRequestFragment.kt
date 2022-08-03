package com.selflearningcoursecreationapp.ui.profile.requestTracker.acceptRejectRequest

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentAcceptedRequestBinding

class AcceptedRequestFragment : BaseFragment<FragmentAcceptedRequestBinding>() {

    private val isRejected: Boolean by lazy { arguments?.getBoolean("isRejected", false) ?: false }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_accepted_request
    }

    private fun init() {
        binding.rvRequests.adapter = AcceptedRequestAdapter(isRejected, context)
    }

    override fun onApiRetry(apiCode: String) {

    }
}