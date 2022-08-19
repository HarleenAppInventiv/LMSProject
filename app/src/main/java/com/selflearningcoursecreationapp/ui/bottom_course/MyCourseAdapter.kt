package com.selflearningcoursecreationapp.ui.bottom_course

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyCourseBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.builderUtils.ImageViewBuilder
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils

class MyCourseAdapter(private var type: Int, private val list: ArrayList<CourseData>) :
    BaseAdapter<AdapterMyCourseBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_my_course
    }

    override fun getItemCount() = list.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterMyCourseBinding
        val context = binding.root.context

        binding.tvOldPrice.paintFlags = binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        when (type) {
            Constant.COURSE_IN_PROGRESS -> {
                binding.bookmarkTimeG.gone()
                binding.priceG.gone()
                binding.tvCoin.gone()
                binding.progressG.gone()
                binding.btBuy.visible()
                binding.tvDuration.gone()
                binding.btBuy.text = context.getString(R.string.resume)
                binding.tvDuration.visible()
                binding.progressG.visible()
                binding.btBuy.icon = ContextCompat.getDrawable(context, R.drawable.ic_resume)
            }
            Constant.MYCOURSES -> {
                binding.bookmarkTimeG.gone()
                binding.priceG.gone()
                binding.tvCoin.gone()
                binding.btBuy.visible()
                binding.tvDuration.gone()
                binding.tvDuration.gone()
                binding.progressG.invisible()
                binding.btBuy.text = context.getString(R.string.edit_course)

            }


        }


//        binding.bookmarkTimeG.gone()
//        binding.priceG.gone()
//        binding.tvCoin.gone()
//        binding.progressG.gone()
//        binding.btBuy.visible()
//        binding.tvDuration.gone()


//        binding.tvProgress.text = "8% COMPLETED"
//        binding.pbProgress.progress = 10


        binding.ivPreview.loadImage(
            list[position].courseBannerUrl,
            R.drawable.ic_home_default_banner,
            position
        )

        ImageViewBuilder.builder(binding.ivPreview)
            .placeHolder(R.drawable.ic_home_default_banner)
            .blurhash(list[position].courseBannerHash)
            .setImageUrl(list[position].courseBannerUrl)
            .colorIndex(position)
            .loadImage()
        binding.tvName.text = list.get(position).courseTitle
        binding.tvRating.text = list[position].averageRating
//        binding.tvReviewCount.setText(list!![position].totalReviews)
        binding.tvAuthor.text = list[position].createdByName
        binding.ivCertification.text = list[position].categoryName
        binding.ivLang.text = list[position].languageName

//        if (list[position].courseWishlisted == 0) {
//            binding.ivBookmark.setBackgroundColor(
//                ContextCompat.getColor(
//                    context,
//                    R.color.transBlack
//                )
//            )

//        }
//        else {
//            binding.ivBookmark.changeBgColor(ThemeConstants.TYPE_BACKGROUND)
//        }
        binding.ivBookmark.apply {
//            setImageResource(if (list[position].courseWishlisted == 0) R.drawable.ic_home_bookmark else R.drawable.ic_blue_bookmark)
//            setOnClickListener {
//                onItemClick(Constant.CLICK_BOOKMARK, position)
//            }
            gone()
        }
        binding.btBuy.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)

        }
        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)

        }

//        when (type) {
//            Constant.COURSE_COMPLETED -> {
//
//                binding.btBuy.gone()
//
//            }
//            Constant.COURSE_COMPLETED_REWARD -> {
//
//                binding.btBuy.gone()
//                binding.btCertificate.visible()
//                binding.tvCoin.visible()
//
//            }
//            Constant.COURSE_BOOKMARKED -> {
//
//                when (position) {
//                    2 -> {
//                        binding.btBuy.gone()
//                    }
//                    1 -> {
//                        binding.btBuy.text = context.getString(R.string.resume)
//                        binding.btBuy.icon =
//                            ContextCompat.getDrawable(context, R.drawable.ic_resume)
//                        binding.tvProgress.text = "8% COMPLETED"
//                        binding.pbProgress.progress = 10
//                        binding.tvDuration.visible()
//                        binding.progressG.visible()
//                    }
//                    3 -> {
//                        binding.bookmarkTimeG.visible()
//                        binding.priceG.visible()
//                        binding.btBuy.text = context.getString(R.string.enroll_now)
//                        binding.btBuy.icon = null
//                    }
//                    else -> {
//                        binding.bookmarkTimeG.visible()
//                        binding.priceG.visible()
//                        binding.btBuy.text = context.getString(R.string.buy_now)
//                        binding.btBuy.icon = null
//                    }
//                }
//            }
//            else -> {
//                binding.btBuy.text = context.getString(R.string.resume)
//                binding.btBuy.icon = ContextCompat.getDrawable(context, R.drawable.ic_resume)
//                binding.tvProgress.text = "8% COMPLETED"
//                binding.pbProgress.progress = 10
//                binding.tvDuration.visible()
//                binding.progressG.visible()
//            }
//        }


        val msg =
            SpanUtils.with(context, "10m left in lesson").endPos(8).themeColor().getSpanString()
//
        binding.tvDuration.setSpanString(msg)
//
//        binding.pbProgress.setOnTouchListener { _, _ ->
//            return@setOnTouchListener true
//        }


        binding.cvOngoing.contentDescription =
            "this ongoing course ux and web design course by allen wen is only 8 percent completed."
    }
}