package com.selflearningcoursecreationapp.ui.bottom_home.downloaded

import android.text.Html
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterDownloadedCourseBinding
import com.selflearningcoursecreationapp.models.offline.OfflineCourseData
import com.selflearningcoursecreationapp.utils.Constant

class DownloadedCourseAdapter(private val list: ArrayList<OfflineCourseData>) :
    BaseAdapter<AdapterDownloadedCourseBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_downloaded_course
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterDownloadedCourseBinding
        val context = binding.root.context

        binding.tvName.text = list[position].title
        binding.tvDescription.text = Html.fromHtml(list[position].description)
        binding.categoryTypeNameTv.text = list[position].categoryTypeName
        binding.createdByNameTv.text = list[position].createdByName
        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)

        }
    }
}