package com.selflearningcoursecreationapp.ui.dashboard

import LearnerDashboardDataList
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyCourseBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseStatus
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils

class LearnerDashAdapter(private val list: ArrayList<LearnerDashboardDataList>) :
    BaseAdapter<AdapterMyCourseBinding>() {
    override fun getLayoutRes() = R.layout.adapter_my_course
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        val binding = holder.binding as AdapterMyCourseBinding
        val context = binding.root.context

        binding.ivBookmark.gone()
        var data = list[position]

        binding.tvName.text = data.courseTitle
        binding.tvAuthor.text = data.createdByName
        binding.ivLang.text = data.languageName
        binding.ivCertification.text = data.categoryTypeName
        binding.tvRating.text = data.averageRating.toString()


        binding.ivPreview.loadImage(
            list[position].courseBannerContentURL,
            R.drawable.ic_home_default_banner,
            position
        )
        binding.ivSignLanguage.visibleView(list[position].isSignLanguage ?: false)

        when (data.userCourseStatus) {

            CourseStatus.ENROLLED -> {

                binding.tvNewPrice.gone()
                binding.tvCoin.gone()
                binding.progressG.gone()
                binding.tvProgress.gone()
                binding.btBuy.text = context.getString(R.string.start)

            }

            CourseStatus.COMPELETD -> {

                binding.tvNewPrice.gone()
                binding.tvCoin.gone()
                binding.tvDuration.gone()
                binding.tvProgress.gone()
                binding.progressG.gone()
//                binding.btBuy.gone()
                binding.tvProgress.text =
                    (data.percentageCompleted?.toInt()).toString() + "% " + context.getString(R.string.completed)
                binding.pbProgress.apply {
                    max = 100
                    progress = data.percentageCompleted?.toInt() ?: max
                }
                binding.btBuy.text = context.getString(R.string.completed)

            }

            else -> {

                binding.progressG.visible()
                binding.bookmarkTimeG.gone()
                binding.priceG.gone()
//                    binding.tvDuration.visible()
                if (data.totalPlayedTime == 0) {
                    binding.btBuy.text = context.getString(R.string.start)
                    binding.progressG.gone()
                    binding.tvDuration.gone()

                } else {
                    binding.btBuy.icon = ContextCompat.getDrawable(context, R.drawable.ic_resume)
                    binding.btBuy.text = context.getString(R.string.resume)
                    binding.progressG.visible()
                    binding.tvProgress.text =
                        if ((list[position].percentageCompleted
                                ?: 0.0) > 0 && (list[position].percentageCompleted
                                ?: 0.0) < 1
                        ) {
                            (list[position].percentageCompleted).toString() + "% " + context.getString(
                                R.string.completed
                            )

                        } else {
                            (list[position].percentageCompleted)?.toInt()
                                .toString() + "% " + context.getString(R.string.completed)

                        }
                    binding.pbProgress.apply {
                        max = 100
                        progress = if ((data.percentageCompleted
                                ?: 0.0) > 0 && (data.percentageCompleted ?: 0.0) < 1
                        )
                            1
                        else
                            data.percentageCompleted?.toInt() ?: 0
                    }
                    binding.tvDuration.apply {
                        visibleView(
                            (data.percentageCompleted?.toInt()
                                ?: 0) > 0 && (data.percentageCompleted?.toInt()
                                ?: 0) < 100
                        )
                        val duration = data?.totalDurationLeft.milliSecToMin().toString()
                        val msg =
                            SpanUtils.with(
                                context,
                                "${duration}${context.getString(R.string.m_left_in_course)}"
                            )
                                .endPos(duration.length + 9)
                                .themeColor().getSpanString()
                        binding.tvDuration.setSpanString(msg)

                    }

                    binding.pbProgress.setOnTouchListener { _, _ ->
                        return@setOnTouchListener true
                    }

                }
            }
        }

        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)

        }

        binding.btBuy.setOnClickListener {
            onItemClick(Constant.CLICK_BUYBUTTON, position)
        }

        if (position + 1 == list.size) {
            onPageEnd()
        }

    }

}