package com.selflearningcoursecreationapp.ui.course_details.video

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSignLanguageBinding

class SignLanguageFragment : BaseFragment<FragmentSignLanguageBinding>() {

    override fun getLayoutRes() = R.layout.fragment_sign_language
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    fun initUI() {
        binding.recylerSignLanguage.adapter = AdapterSignLanguage()
    }

    override fun onApiRetry(apiCode: String) {

    }

}