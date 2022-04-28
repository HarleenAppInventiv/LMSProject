package com.selflearningcoursecreationapp.ui.profile.edit_profile

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourceCategoriesBinding
import com.selflearningcoursecreationapp.models.StateModel
import com.selflearningcoursecreationapp.utils.Constant
import java.util.*

class AdapterState(private val stateList: ArrayList<StateModel>) :
    BaseAdapter<AdapterCourceCategoriesBinding>() {
    override fun getLayoutRes() = R.layout.adapter_cource_categories
    override fun getItemCount() = stateList.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        binding.tvRemoveSection.apply {

            text = stateList[position].stateName
            setOnClickListener {
                onItemClick(Constant.CLICK_VIEW, stateList[position])
            }
        }
    }
}