package com.selflearningcoursecreationapp.ui.course_details.quiz

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentMatchColumnBinding

class MatchColumnFragment : BaseFragment<FragmentMatchColumnBinding>() {
    override fun getLayoutRes() = R.layout.fragment_match_column

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {

    }

    override fun onApiRetry(apiCode: String) {

    }


}