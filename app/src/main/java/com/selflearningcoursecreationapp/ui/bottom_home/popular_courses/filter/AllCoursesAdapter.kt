package com.selflearningcoursecreationapp.ui.bottom_home.popular_courses.filter

import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCoursesLayoutBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseType
import com.selflearningcoursecreationapp.utils.builderUtils.ImageViewBuilder
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants


class AllCoursesAdapter(
    private var list: ArrayList<CourseData>,
    private var isViOn: Boolean = false
) :
    BaseAdapter<AdapterCoursesLayoutBinding>() {
    override fun getLayoutRes() = R.layout.adapter_courses_layout

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterCoursesLayoutBinding
        val context = binding.root.context
        if (position < list.size) {
            binding.tvOldPrice.paintFlags =
                binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            try {


                binding.tvCourseLevel.setComplexityLevel(list[position].courseComplexityId ?: 0)
            } catch (e: Exception) {
                e.printStackTrace()
                binding.tvCourseLevel.setComplexityLevel(0)
            }
            val data = list[position]
            binding.courseData = data
            binding.executePendingBindings()

            ImageViewBuilder.builder(binding.ivBanner)
                .placeHolder(R.drawable.ic_home_default_banner)
                .setImageUrl(data.courseBannerUrl)
                .loadImage()



            binding.tvReviewCount.text = data.totalReviews.getReviewCount()
            binding.btnEnroll.setCourseButton(
                data.courseTypeId,
                data.userCourseStatus,
                data.paymentStatus
            )

            if (data.courseWishlisted == 0) {
                binding.ivBookmark.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.transBlack
                    )
                )
            } else {
                binding.ivBookmark.changeBgColor(ThemeConstants.TYPE_BACKGROUND)
            }

            binding.root.setOnClickListener {
                onItemClick(Constant.CLICK_DETAILS, position)
            }

            binding.ivBookmark.setOnClickListener {
                onItemClick(Constant.CLICK_BOOKMARK, position)
            }
            binding.btnEnroll.setOnClickListener {
                onItemClick(Constant.CLICK_BUYBUTTON, position)
            }


            if (position + 1 == list.size) {
                onPageEnd()
            }


            binding.tvCoin.text = data.rewardPoints + " " + context.getString(R.string.points_)

            binding.tvNewPrice.gone()
            binding.tvCoin.gone()

            when (data.courseTypeId) {
                CourseType.REWARD_POINTS -> {
                    binding.tvCoin.visible()
                    binding.tvCoin.setTextColor(ContextCompat.getColor(context, R.color.black))

                }
                CourseType.PAID -> {
                    binding.tvNewPrice.visible()
                    binding.tvNewPrice.text =
                        String.format("%s %s", data.currencySymbol, data.courseFee)
                    binding.tvCoin.setTextColor(
                        ContextCompat.getColor(
                            context,
                            context.getAttrColor(R.attr.accentColor_Green)
                        )
                    )
                }
                CourseType.FREE -> {
                    binding.tvNewPrice.visible()
                    binding.tvNewPrice.text = context.getString(R.string.free)

                }
                CourseType.RESTRICTED -> {
                    binding.tvNewPrice.visible()
                    binding.tvNewPrice.text = context.getString(R.string.restricted)

                }
            }


//        when(data.paymentStatus)
//        {
//            PaymentStatus.IN_PROGRESS->{
//                binding.btnEnroll.text = context.getString(R.string.resume)
//                binding.btnEnroll.setBtnEnabled(false)
//            }
//            else->{
//                binding.btnEnroll.setBtnEnabled(true)
//
//            }
//        }
            binding.tvAuthorName.setLimitedName(data.createdByName)

        }
    }
}