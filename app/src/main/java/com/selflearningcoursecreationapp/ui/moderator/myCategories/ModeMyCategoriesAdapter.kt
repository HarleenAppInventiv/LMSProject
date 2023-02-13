package com.selflearningcoursecreationapp.ui.moderator.myCategories

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSelectCategoryBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.loadImage

class ModeMyCategoriesAdapter(private val list: ArrayList<Categories>) :
    BaseAdapter<AdapterSelectCategoryBinding>() {

    override fun getLayoutRes(): Int {


        return R.layout.adapter_select_category
    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        binding.tvTitle.text = list[position].categoryName
        binding.ivPreview.loadImage(
            list[position].imageURL,
            R.drawable.ic_home_default_banner,
            position
        )
        binding.ivSelected.gone()


//        binding.root.setOnClickListener {
//            onItemClick(Constant.CLICK_VIEW, position, list[position].contentURL)
//        }
    }


}