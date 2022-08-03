package com.selflearningcoursecreationapp.ui.course_details.video

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSignLanguageBinding

class AdapterSignLanguage : BaseAdapter<AdapterSignLanguageBinding>() {
    override fun getLayoutRes() = R.layout.adapter_sign_language
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

    }

    override fun getItemCount() = 2
}