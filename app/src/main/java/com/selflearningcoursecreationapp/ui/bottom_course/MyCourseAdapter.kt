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
import com.selflearningcoursecreationapp.utils.CourseScreenType
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
        binding.tvLesson.text =
            context.getQuantityString(R.plurals.section_quantity, list[position].totalSections)
        binding.tvTime.text = list[position].courseDuration.getTime(context)
        when (type) {

            CourseScreenType.COMPLETED_COURSES -> {
                binding.bookmarkTimeG.gone()
                binding.priceG.gone()
                binding.tvCoin.gone()
                binding.btBuy.visible()
                binding.tvDuration.gone()
                binding.tvDuration.gone()
                binding.progressG.invisible()
                binding.btBuy.text = context.getString(R.string.completed)
                binding.ivSignLanguage.visibleView(list[position].isSignLanguage ?: false)
            }
            else -> {
                binding.bookmarkTimeG.gone()
                binding.priceG.gone()
                binding.tvCoin.gone()
                binding.btBuy.visible()
                binding.ivSignLanguage.visibleView(list[position].isSignLanguage ?: false)

                if (list[position].totalPlayedTime == 0) {
                    binding.btBuy.text = context.getString(R.string.start)
                    binding.progressG.gone()
                    binding.tvDuration.gone()

                } else {
                    binding.btBuy.icon = ContextCompat.getDrawable(context, R.drawable.ic_resume)
                    binding.btBuy.text = context.getString(R.string.resume)
                    binding.tvProgress.text = (list[position].percentageCompleted)?.toInt()
                        .toString() + "% " + context.getString(R.string.completed)

                    binding.progressG.visible()
                    binding.tvDuration.apply {
                        visibleView(
                            list[position].percentageCompleted?.toInt().isCourseInProgress()
                        )
                        val duration = list[position].totalDurationLeft.milliSecToMin().toString()
                        val msg =
                            SpanUtils.with(
                                context,
                                "${duration}${context.getString(R.string.m_left_in_course)}"
                            )
                                .endPos(duration.length + 9)
                                .themeColor().getSpanString()
                        binding.tvDuration.setSpanString(msg)
                    }


                    binding.pbProgress.apply {

                        max = 100
                        progress = if ((list[position].percentageCompleted
                                ?: 0.0) > 0 && (list[position].percentageCompleted ?: 0.0) < 1
                        )
                            1
                        else
                            list[position].percentageCompleted?.toInt() ?: 0

                    }

                }
            }


        }



        binding.ivPreview.loadImage(
            list[position].courseBannerUrl,
            R.drawable.ic_home_default_banner,
            position
        )

        if (position + 1 == list.size) {
            onPageEnd()
        }

        ImageViewBuilder.builder(binding.ivPreview)
            .placeHolder(R.drawable.ic_home_default_banner)
            .setImageUrl(list[position].courseBannerUrl)
            .colorIndex(position)
            .loadImage()

        binding.tvName.text = list.get(position).courseTitle
        binding.tvRating.apply {
            text = list[position].averageRating
            contentDescription = "$text ${context.getString(R.string.reviews_small)}"
        }
        binding.tvAuthor.text = list[position].createdByName
        binding.ivCertification.text = list[position].categoryName
        binding.ivLang.text = list[position].languageName


        binding.ivBookmark.gone()

        binding.btBuy.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)

        }
        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)

        }

        binding.pbProgress.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }

    }
}