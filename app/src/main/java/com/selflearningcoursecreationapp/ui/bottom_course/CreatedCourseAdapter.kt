package com.selflearningcoursecreationapp.ui.bottom_course

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyCourseBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseType
import com.selflearningcoursecreationapp.utils.CreatedCourseStatus
import com.selflearningcoursecreationapp.utils.builderUtils.ImageViewBuilder
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils


class CreatedCourseAdapter(private val list: ArrayList<CourseData>) :
    BaseAdapter<AdapterMyCourseBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_my_course
    }

    override fun getItemCount() = list.size

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterMyCourseBinding
        val context = binding.root.context
        val data = list[position]


        binding.bookmarkTimeG.gone()
        binding.priceG.visible()
//        binding.tvCoin.gone()
        binding.btBuy.visible()
        binding.tvDuration.gone()
        binding.ivBookmark.gone()
        binding.progressG.invisible()
        binding.btBuy.setCreateCourseText(list[position].status)



        binding.ivPreview.loadImage(
            list[position].courseBannerUrl,
            R.drawable.ic_home_default_banner,
            position
        )

        ImageViewBuilder.builder(binding.ivPreview)
            .placeHolder(R.drawable.ic_home_default_banner)
//            .blurhash(list[position].courseBannerHash)
            .setImageUrl(list[position].courseBannerUrl)
            .colorIndex(position)
            .loadImage()
        binding.tvName.text = list.get(position).courseTitle
        binding.tvRating.text = list[position].averageRating
//        binding.tvReviewCount.setText(list!![position].totalReviews)
        binding.tvAuthor.setLimitedName(list[position].createdByName)
        binding.ivCertification.text = list[position].categoryName
        binding.ivLang.text = list[position].languageName

        binding.ivDelete.visibleView(list[position].publishAttempt == 0 && list[position].status == CreatedCourseStatus.DRAFT)


        binding.btBuy.setOnClickListener {
            onItemClick(Constant.CLICK_EDIT, position, list.get(position).status ?: 0)

        }
        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)

        }

        binding.ivDelete.setOnClickListener {
            onItemClick(Constant.CLICK_DELETE, position)
        }


        val msg =
            SpanUtils.with(context, "10m left in lesson").endPos(8).themeColor().getSpanString()
//
        binding.tvDuration.setSpanString(msg)
//
        binding.pbProgress.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
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


    }
}