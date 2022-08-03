package com.selflearningcoursecreationapp.ui.profile.edit_profile

import android.graphics.Typeface
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourceCategoriesBinding
import com.selflearningcoursecreationapp.models.GenderModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants


class AdapterSelectGender(private val list: ArrayList<GenderModel>) :
    BaseAdapter<AdapterCourceCategoriesBinding>() {
    override fun getLayoutRes() = R.layout.adapter_cource_categories
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        binding.tvRemoveSection.apply {
            changeTextColor(if (list[position].isSelected) ThemeConstants.TYPE_THEME else ThemeConstants.TYPE_PRIMARY)
            typeface =
                if (list[position].isSelected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            text = list[position].genderName
            setOnClickListener {
                onItemClick(Constant.CLICK_VIEW, list[position])
            }
        }

    }
}