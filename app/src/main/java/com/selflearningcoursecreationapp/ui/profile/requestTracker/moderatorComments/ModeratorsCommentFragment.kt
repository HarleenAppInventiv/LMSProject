package com.selflearningcoursecreationapp.ui.profile.requestTracker.moderatorComments

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModeratorsCommentBinding


class ModeratorsCommentFragment : BaseFragment<FragmentModeratorsCommentBinding>(),
    ModCommentsAdapter.OnClickListener {

    private val isComments: Boolean by lazy { arguments?.getBoolean("isComments", false) ?: false }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_moderators_comment
    }

    private fun init() {
        binding.rvComments.adapter = ModCommentsAdapter(isComments, this)
    }

    override fun onItemClick() {
        val bundle = Bundle().apply {
            "isComments" to isComments
        }
//        findNavController().navigate(R.id.action_moderatorsCommentFragment_to_courseDetailsFragment,bundle)
    }

    override fun onApiRetry(apiCode: String) {

    }

}