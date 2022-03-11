package com.selflearningcoursecreationapp.ui.splash.intro_slider

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterPrefPagerDotBinding
import com.selflearningcoursecreationapp.utils.Constant

class DotAdapter(private var type: Int, private var list: ArrayList<Boolean>) :
    BaseAdapter<AdapterPrefPagerDotBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_pref_pager_dot
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterPrefPagerDotBinding
        if (list[position]) {
            when (type) {
                Constant.TYPE_ROUND -> {
                    binding.ivDot.setImageResource(R.drawable.selected_pref_dot)
                }
                Constant.TYPE_LINE -> {
                    binding.ivDot.setImageResource(R.drawable.selected_pref_line)

                }
            }
        } else {
            when (type) {
                Constant.TYPE_ROUND -> {
                    binding.ivDot.setImageResource(R.drawable.unselected_dot)
                }
                Constant.TYPE_LINE -> {
                    binding.ivDot.setImageResource(R.drawable.unselected_line)


                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}