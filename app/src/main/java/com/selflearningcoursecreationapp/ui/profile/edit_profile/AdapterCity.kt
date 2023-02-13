package com.selflearningcoursecreationapp.ui.profile.edit_profile

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourceCategoriesBinding
import com.selflearningcoursecreationapp.models.CityModel
import com.selflearningcoursecreationapp.utils.Constant

class AdapterCity(private val cityList: ArrayList<CityModel>) :
    BaseAdapter<AdapterCourceCategoriesBinding>() {
    override fun getLayoutRes() = R.layout.adapter_cource_categories
    override fun getItemCount() = cityList.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterCourceCategoriesBinding

        binding.tvRemoveSection.apply {

            text = cityList[position].cityName
            setOnClickListener {
                onItemClick(Constant.CLICK_VIEW, cityList[position])
            }
        }

    }
}