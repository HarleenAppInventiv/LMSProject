package com.selflearningcoursecreationapp.ui.course_details.info

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterAuthorBioBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils


class AuthorBioAdapter(private var list: ArrayList<UserProfile>) :
    BaseAdapter<AdapterAuthorBioBinding>() {

    override fun getLayoutRes() = R.layout.adapter_author_bio

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterAuthorBioBinding
        val context = binding.root.context
        val data = list[position]
        binding.lineV.visibleView(position + 1 != list.size)
        binding.ivProfileImage.loadImage(data.profileUrl, R.drawable.ic_default_user_grey)
        binding.ivProfileImage.loadImage(
            data.profileUrl,
            R.drawable.ic_default_user_grey,
            data.profileBlurHash
        )
        binding.ivLogo.loadImage(data.courseLogoURL, R.drawable.ic_logo_default)
        binding.tvBio.setSpanString(
            SpanUtils.with(
                context,
                "${data.name} ${data.bio ?: ""}"
            )
                .startPos(0)
                .endPos(data.name.length)
                .isBold()
                .getSpanString()
        )


    }


    override fun getItemCount() = list.size


}