package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.view.View
import androidx.core.view.isVisible

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSectionViewBinding
import com.selflearningcoursecreationapp.utils.Constant

class AddSectionAdapter() :
    BaseAdapter<AdapterSectionViewBinding>() {

    override fun getLayoutRes() = R.layout.adapter_section_view

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSectionViewBinding

        binding.ivMore.setOnClickListener {
            onItemClick(Constant.CLICK_MORE, holder.adapterPosition,0)
        }

        binding.ivVisible.setOnClickListener {
            if (binding.rvLecture.isVisible) {
                binding.rvLecture.visibility = View.GONE
            } else {
                binding.rvLecture.visibility = View.VISIBLE
            }
        }
        binding.rvLecture.adapter = ChildLectureAdapter().apply {
            setOnAdapterItemClickListener(object : IViewClick {
                override fun onItemClick(vararg items: Any) {
                    if (items.isNotEmpty()) {
                        val type = items[0] as Int
                        val childPosition = items[1] as Int
                        this@AddSectionAdapter.onItemClick(type,
                            holder.adapterPosition,
                            childPosition)
                    }
                }

            })
        }

    }

    override fun getItemCount() = 5


}