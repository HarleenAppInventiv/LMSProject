package com.selflearningcoursecreationapp.ui.moderator.courseDetails

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterAuthorNameBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.models.user.UserProfile

class CourseAuthorAdapter(private val list: ArrayList<UserProfile>) :
    BaseAdapter<AdapterAuthorNameBinding>(),
    BaseAdapter.IViewClick {
    override fun getLayoutRes() = R.layout.adapter_author_name
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterAuthorNameBinding
        val data = list[position]
        binding.tvAuthorName.text = data.name
        binding.ivProfileImage.loadImage(data.profileUrl, R.drawable.dummy_user)
        binding.ivLogo.loadImage(data.courseLogoURL, R.drawable.ic_logo_default)
    }

}