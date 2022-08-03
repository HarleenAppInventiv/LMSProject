package com.selflearningcoursecreationapp.ui.profile.requestTracker.coAuthorRequest

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCoAuthorRequestBinding


class CoAuthorRequestFragment : BaseFragment<FragmentCoAuthorRequestBinding>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_co_author_request
    }

    private fun init() {
        binding.rvRequests.adapter = CoAuthorRequestAdapter()
    }

    override fun onApiRetry(apiCode: String) {

    }

}