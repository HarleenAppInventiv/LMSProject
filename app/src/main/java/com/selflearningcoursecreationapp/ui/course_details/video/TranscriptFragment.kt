package com.selflearningcoursecreationapp.ui.course_details.video

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentTranscriptBinding

class TranscriptFragment : BaseFragment<FragmentTranscriptBinding>() {

    override fun getLayoutRes() = R.layout.fragment_transcript
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {

    }

    override fun onApiRetry(apiCode: String) {
    }


}