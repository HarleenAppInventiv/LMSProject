package com.selflearningcoursecreationapp.ui.moderator.qualification

import android.os.Bundle
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModeDocumentsBinding

class ModeCertificateFragment : BaseFragment<FragmentModeDocumentsBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_mode_documents
    }

    override fun onApiRetry(apiCode: String) {

    }

    private fun init() {
        binding.rvModeDocuments.adapter = ModeCertificateDocumentsAdapter()

    }
}