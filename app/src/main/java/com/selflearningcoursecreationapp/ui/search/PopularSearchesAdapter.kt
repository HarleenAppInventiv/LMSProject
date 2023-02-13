package com.selflearningcoursecreationapp.ui.search

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.ItemPopularSearchBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.utils.Constant

class PopularSearchesAdapter(var list: ArrayList<SearchList>) :
    BaseAdapter<ItemPopularSearchBinding>() {
    override fun getLayoutRes() = R.layout.item_popular_search


    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        val binding = holder.binding as ItemPopularSearchBinding
        val context = binding.root.context

        binding.tvName.text = list[position].title
        binding.ivCertification.text = list[position].category.name
        binding.ivLang.text = list[position].language.name
        binding.tvCreator.text = list[position].creator.name


        binding.ivPreview.loadImage(
            list[position].banner.url,
            R.drawable.ic_home_default_banner,
            position
        )

        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_SUGGESTION, position)

        }

        if (position + 1 == list.size) {
            onPageEnd()
        }


    }
}