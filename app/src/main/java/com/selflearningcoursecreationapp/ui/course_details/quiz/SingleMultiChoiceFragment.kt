package com.selflearningcoursecreationapp.ui.course_details.quiz

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSingleMultiChoiceBinding


class SingleMultiChoiceFragment : BaseFragment<FragmentSingleMultiChoiceBinding>() {
    override fun getLayoutRes() = R.layout.fragment_single_multi_choice

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
//        binding.recyclerQuizOption.adapter = QuizAnswerListAdapter()

    }

    override fun onApiRetry(apiCode: String) {

    }

}