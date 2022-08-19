package com.selflearningcoursecreationapp.ui.content_creator.course_detail

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCoauthorListBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.models.user.UserProfile

class CoAuthorListAdapter(private val courseCoAuthors: ArrayList<UserProfile>?) :
    BaseAdapter<AdapterCoauthorListBinding>() {
    override fun getLayoutRes() = R.layout.adapter_coauthor_list
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        binding.ivProfileImage.loadImage(
            courseCoAuthors?.get(position)?.profileUrl,
            R.drawable.ic_default_user_grey
        )
        binding.ivLogo.loadImage(
            courseCoAuthors?.get(position)?.courseLogoURL,
            R.drawable.ic_logo_default
        )
        binding.tvAuthorName.text = courseCoAuthors?.get(position)?.name
    }

    override fun getItemCount() = courseCoAuthors?.size ?: 0
}