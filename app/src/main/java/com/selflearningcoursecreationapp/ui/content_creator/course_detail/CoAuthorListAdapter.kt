package com.selflearningcoursecreationapp.ui.content_creator.course_detail

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterAuthorNameBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.setLimitedName
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.Constant

class CoAuthorListAdapter(private val courseCoAuthors: ArrayList<UserProfile>?) :
    BaseAdapter<AdapterAuthorNameBinding>() {
    override fun getLayoutRes() = R.layout.adapter_author_name
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
        binding.tvAuthorName.setLimitedName(courseCoAuthors?.get(position)?.name)

        binding.ivProfileImage.setOnClickListener {
            onItemClick(Constant.CLICK_AUTHOR_PROFILE, position)
        }

        binding.ivLogo.setOnClickListener {
            onItemClick(Constant.CLICK_AUTHOR_PROFILE, position)
        }

        binding.tvAuthorName.setOnClickListener {
            onItemClick(Constant.CLICK_AUTHOR_PROFILE, position)
        }

    }

    override fun getItemCount() = courseCoAuthors?.size ?: 0
}