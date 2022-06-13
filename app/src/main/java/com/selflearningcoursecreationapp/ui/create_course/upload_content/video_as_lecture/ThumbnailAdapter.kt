package com.selflearningcoursecreationapp.ui.create_course.upload_content.video_as_lecture

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterThumbnailBinding
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.utils.Constant

class ThumbnailAdapter(private val thumbnailList: ArrayList<ThumbnailModel>?) :
    BaseAdapter<AdapterThumbnailBinding>() {
    override fun getLayoutRes() = R.layout.adapter_thumbnail

    override fun getItemCount(): Int = thumbnailList?.size!!

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        binding.ivThumbnailImage.setImageURI(thumbnailList!![position].thumbUri)
        binding.ivThumbnailImage.setOnClickListener {
//            thumbnailList[position].checked = true
            binding.ivCheck.visible()
            onItemClick(Constant.CLICK_ADD, position)
        }

        binding.ivCross.setOnClickListener {
            onItemClick(Constant.CLICK_DELETE, position)
        }

    }

}