package com.selflearningcoursecreationapp.ui.bottom_home.categories

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterHomeCategoriesBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.Constant


class HomeCategoryAdapter(private var list: ArrayList<CategoryData>) :
    BaseAdapter<AdapterHomeCategoriesBinding>() {

    override fun getLayoutRes() = R.layout.adapter_home_categories

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterHomeCategoriesBinding
        val context = binding.root.context
        binding.ivImg.loadImage(list[position].imageUrl, R.drawable.ic_all_courses)
        binding.tvTitle.text = list[position].name ?: list[position].codeId?.let {
            context.getString(
                it
            )
        }
        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }

    }

    override fun getItemCount() = list.size


}