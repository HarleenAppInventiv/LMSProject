package com.selflearningcoursecreationapp.ui.moderator.moderatorDashboard

import ModDashboardDataList
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.ItemModeratorCoursesBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.utils.Constant

class ModeratorDashAdapter(
    private var type: Int,
    private val list: ArrayList<ModDashboardDataList>
) : BaseAdapter<ItemModeratorCoursesBinding>() {
    override fun getLayoutRes() = R.layout.item_moderator_courses
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        val binding = holder.binding as ItemModeratorCoursesBinding
        val context = binding.root.context

        binding.tvCourseName.text = list[position].courseTitle
        binding.tvUserName.text = list[position].createdByName
        binding.tvLanguage.text = list[position].languageName
        binding.tvCourseDesc.text = list[position].categoryTypeName
        binding.tvRatings.text = list[position].averageRating.toString()


        binding.ivCourseImage.loadImage(
            list[position].courseBannerContentURL,
            R.drawable.ic_home_default_banner,
            position
        )

        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)

        }

        if (position + 1 == list.size) {
            onPageEnd()
        }


    }

}