package com.selflearningcoursecreationapp.ui.splash.intro_slider


import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.SlideScreenBinding
import com.selflearningcoursecreationapp.extensions.setSpanText
import com.selflearningcoursecreationapp.models.WalkthroughData

class SlideViewPagerAdapter( var list: ArrayList<WalkthroughData>) :BaseAdapter<SlideScreenBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.slide_screen
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding= holder.binding as SlideScreenBinding
        binding.desc.setSpanText(list[position].descrtion)
        binding.title.setSpanText(list[position].title)
        binding.logo.setImageDrawable(ContextCompat.getDrawable(binding.root.context,list[position].icon))



//        binding.cbVisual.setOnClickListener {
//            onItemClick(Constant.CLICK_VIEW,position)
//        }
    }
}
