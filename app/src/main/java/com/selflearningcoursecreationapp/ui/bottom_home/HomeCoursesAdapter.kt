package com.selflearningcoursecreationapp.ui.bottom_home

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCoursesViewBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseType
import com.selflearningcoursecreationapp.utils.PaymentStatus
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants


class HomeCoursesAdapter(private var list: List<CourseData>?) :
    BaseAdapter<AdapterCoursesViewBinding>() {

    override fun getLayoutRes() = R.layout.adapter_courses_view

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterCoursesViewBinding
        val context = binding.root.context
        val data = list?.get(position)
        binding.appCompatTextView6.paintFlags =
            binding.appCompatTextView6.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


        (binding.cvPopularCourse.layoutParams as RecyclerView.LayoutParams).apply {
            if (list?.size == 1) {
                width = RecyclerView.LayoutParams.MATCH_PARENT
                marginEnd = context.resources.getDimensionPixelOffset(R.dimen._15sdp)

            } else {
                width = context.resources.getDimensionPixelOffset(R.dimen._240sdp)
                marginEnd = context.resources.getDimensionPixelOffset(R.dimen._10sdp)
            }
        }
        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_DETAILS, position)

        }
        binding.tvCourseLevel.setComplexityLevel(data?.courseComplexityId)


        binding.tvReviewCount.text = data?.totalReviews.getReviewCount()

//        binding.appCompatImageView2.setImageResource(list[position].)
        binding.appCompatImageView2.apply {
            loadImage(data?.courseBannerUrl, R.drawable.ic_home_default_banner, position)
        }
        binding.textView4.text = list?.get(position)?.courseTitle
        binding.tvCourseLevel.text = data?.courseComplexityName
        binding.tvRating.text = data?.averageRating
//        binding.tvReviewCount.setText(data.totalReviews)
        binding.tvAuthorName.setLimitedName(data?.getCreatedName())
        binding.tvCertification.text = data?.categoryName
        binding.tvLanguage.text = data?.languageName
        binding.tvCoin.text = data?.rewardPoints + " Points"

        binding.tvNewPrice.gone()
        binding.tvCoin.gone()

        when (data?.courseTypeId) {
            CourseType.REWARD_POINTS -> {
                binding.tvCoin.visible()
                binding.tvCoin.setTextColor(ContextCompat.getColor(context, R.color.black))

            }
            CourseType.PAID -> {
                binding.tvNewPrice.visible()
                binding.tvNewPrice.text =
                    String.format("%s %s", data.currencySymbol, data.courseFee)

            }
            CourseType.FREE -> {
                binding.tvNewPrice.visible()
                binding.tvNewPrice.text = context.getString(R.string.free)
                binding.tvNewPrice.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        }


        when (data?.paymentStatus) {
            PaymentStatus.IN_PROGRESS -> {
                binding.btBuy.text = context.getString(R.string.resume)
                binding.btBuy.setBtnEnabled(false)
            }
            else -> {
                binding.btBuy.setBtnEnabled(true)

            }
        }

        if (data?.courseWishlisted == 0) {
            binding.ivBookmark.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.transBlack
                )
            )
        } else {
            binding.ivBookmark.changeBgColor(ThemeConstants.TYPE_BACKGROUND)
        }
        binding.ivBookmark.setOnClickListener {
            onItemClick(Constant.CLICK_BOOKMARK, position)
        }
        binding.btBuy.apply {


            list?.get(position)?.let {

                setCourseButton(it.courseTypeId, it.userCourseStatus, it.paymentStatus)
//                text = context.getButtonText(it.courseTypeId, it.userCourseStatus)

            }

            setOnClickListener {
                onItemClick(Constant.CLICK_BUYBUTTON, position)
            }
        }

    }


    override fun getItemCount() = list?.size ?: 0


}