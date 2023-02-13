package com.selflearningcoursecreationapp.ui.moderator.myCategories

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyLanguagesBinding

class ModeMyLanguagesAdapter(private val list: ArrayList<Languages>) :
    BaseAdapter<AdapterMyLanguagesBinding>() {

    override fun getLayoutRes(): Int {


        return R.layout.adapter_my_languages
    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        binding.tvTitle.text = list[position].languageName


//        binding.root.setOnClickListener {
//            onItemClick(Constant.CLICK_VIEW, position, list[position].contentURL)
//        }
    }


}