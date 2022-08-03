package com.selflearningcoursecreationapp.ui.profile.requestTracker

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment


class RequestTrackerDashboardFragment :
    BaseFragment<com.selflearningcoursecreationapp.databinding.FragmentRequestTrackerDashboardBinding>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_request_tracker_dashboard
    }

    private fun init() {


        binding.buttonBecomeModerator.setOnClickListener {
            findNavController().navigate(R.id.action_requestTrackerDashboardFragment_to_becomeModeratorFragment)
        }
        binding.cardViewAcceptedRequest.setOnClickListener {
            findNavController().navigate(R.id.action_requestTrackerDashboardFragment_to_AcceptedRequestFragment)
        }


        binding.cardViewRejectedReq.setOnClickListener {
            val bundle = Bundle().apply {
                "isRejected" to true
            }
            findNavController().navigate(
                R.id.action_requestTrackerDashboardFragment_to_AcceptedRequestFragment,
                bundle
            )
        }

        binding.cardViewCoAuthor.setOnClickListener {
            findNavController().navigate(R.id.action_requestTrackerDashboardFragment_to_coAuthorRequestFragment)
        }

        binding.cardViewModeratorComments.setOnClickListener {
            findNavController().navigate(R.id.action_requestTrackerDashboardFragment_to_moderatorsCommentFragment)
        }

        binding.cardViewRejectedCourses.setOnClickListener {
            val bundle = Bundle().apply { "isComments" to true }
            findNavController().navigate(
                R.id.action_requestTrackerDashboardFragment_to_moderatorsCommentFragment,
                bundle
            )
        }

    }

    override fun onApiRetry(apiCode: String) {

    }

}