package com.selflearningcoursecreationapp.ui.authentication.login_signup

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourceCategoriesBinding
import com.selflearningcoursecreationapp.models.ProfessionModel
import com.selflearningcoursecreationapp.utils.Constant

class AdapterProfession(private val list: ArrayList<ProfessionModel>) :
    BaseAdapter<AdapterCourceCategoriesBinding>() {
    override fun getLayoutRes() = R.layout.adapter_cource_categories
    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        binding.tvRemoveSection.apply {


            text = list[position].professionName
            setOnClickListener {
                onItemClick(Constant.CLICK_VIEW, list[position])
            }
        }


    }

}