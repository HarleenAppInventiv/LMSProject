package com.selflearningcoursecreationapp.ui.profile.bookmark

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.AdapterMyCourseBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseStatus
import com.selflearningcoursecreationapp.utils.CourseType
import com.selflearningcoursecreationapp.utils.builderUtils.ImageViewBuilder
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils

class WishListAdapter(

    private val wishClickListener: (type: Int, wishListItem: CourseData, position: Int) -> Unit,
) :
    PagingDataAdapter<CourseData, WishListAdapter.ViewHolder>(DiffUtilCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<AdapterMyCourseBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_my_course,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val binding = holder.binding
        val context = binding.root.context

        binding.tvOldPrice.paintFlags = binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


        binding.bookmarkTimeG.visible()
        binding.priceG.gone()
        binding.tvCoin.gone()
//        binding.progressG.gone()
        binding.btBuy.visible()
//        binding.tvDuration.gone()


        binding.pbProgress.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }




        getItem(position)?.let { holder.bind(it, position) }
    }


    inner class ViewHolder(var binding: AdapterMyCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CourseData, position: Int) {
            val context = binding.root.context

            binding.tvName.text = data.courseTitle
            binding.ivLang.text = data.languageName
            binding.ivCertification.text = data.categoryName
            binding.tvAuthor.text = data.createdByName
            binding.tvTime.text = data.courseDuration.getTime(context)

//            binding.tvNewPrice.text = "$ ${DecimalFormat("##,##0.00").format(data.courseFee)};"
            binding.tvRating.text = data.averageRating
            binding.tvNewPrice.text =
                String.format(context.getString(R.string.course_fee_symbol), data.courseFee)

            data.isSignLanguage?.let { binding.ivSignLanguage.visibleView(it) }


            ImageViewBuilder.builder(binding.ivPreview)
                .placeHolder(R.drawable.ic_home_default_banner)
                .setImageUrl(data.courseBannerUrl)
                .colorIndex(position)
                .blurhash(data.courseBannerHash)
                .loadImage()

            binding.tvLesson.text =
                context.getQuantityString(R.plurals.section_quantity, data.totalSections)

            when (data.userCourseStatus) {
                CourseStatus.NOT_ENROLLED -> {

                    binding.tvCoin.text = data.rewardPoints

                    binding.tvNewPrice.gone()
                    binding.tvCoin.gone()
                    binding.progressG.gone()
                    binding.tvProgress.gone()
                    when (data.courseTypeId) {
                        CourseType.REWARD_POINTS -> {
                            binding.tvCoin.visible()
                            binding.tvCoin.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }
                        CourseType.PAID -> {
                            binding.priceG.visible()
                            binding.tvNewPrice.text =
                                String.format("%s %s", data.currencySymbol, data.courseFee)
                        }
                        CourseType.FREE -> {
                            binding.priceG.visible()
                            binding.tvNewPrice.text = context.getString(R.string.free)

                        }
                        CourseType.RESTRICTED -> {
                            binding.priceG.visible()
                            binding.tvNewPrice.text = context.getString(R.string.restricted)

                        }
                    }

                }

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
                    binding.bookmarkTimeG.gone()
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
                        binding.btBuy.icon =
                            ContextCompat.getDrawable(context, R.drawable.ic_resume)
                        binding.btBuy.text = context.getString(R.string.resume)
                        binding.progressG.visible()
//                        binding.tvProgress.text = (data.percentageCompleted?.toInt()).toString() + "% " + context.getString(
//                                R.string.completed
//                            )
//                        binding.pbProgress.apply {
//                            max = 100
//                            progress = data.percentageCompleted?.toInt() ?: 0
//                        }
                        binding.progressG.visibleView((data.percentageCompleted ?: 0.0) > 0.0)
//                        binding.tvProgress.text = (data.percentageCompleted).toString() + "% " + context.getString(R.string.completed)

                        binding.tvProgress.text =
                            if ((data.percentageCompleted ?: 0.0) > 0 && (data.percentageCompleted
                                    ?: 0.0) < 1
                            ) {
                                (data.percentageCompleted).toString() + "% " + context.getString(R.string.completed)

                            } else {
                                (data.percentageCompleted)?.toInt()
                                    .toString() + "% " + context.getString(R.string.completed)

                            }
                        binding.pbProgress.apply {

                            max = 100
                            progress = if ((data.percentageCompleted
                                    ?: 0.0) > 0 && (data.percentageCompleted ?: 0.0) < 1
                            ) 1
                            else
                                data.percentageCompleted?.toInt() ?: 0

                        }
                        binding.tvDuration.apply {
                            visibleView(
                                (data.percentageCompleted?.toInt()
                                    ?: 0) > 0 && (data.percentageCompleted?.toInt()
                                    ?: 0) < 100
                            )
                            val duration = data.totalDurationLeft.milliSecToMin().toString()
                            val msg =
                                SpanUtils.with(
                                    context,
                                    "${duration}${context.getString(R.string.m_left_in_course)}"
                                )
                                    .endPos(duration.length + 9)
                                    .themeColor().getSpanString()

                            binding.tvDuration.setSpanString(msg)

                        }

                    }
                }
            }


            binding.btBuy.setCourseButton(
                data.courseTypeId,
                data.userCourseStatus,
                data.paymentStatus,
                status = data.totalPlayedTime
            )

            binding.ivBookmark.setOnClickListener {
                wishClickListener.invoke(Constant.CLICK_BOOKMARK, data, position)
            }
            binding.btBuy.setOnClickListener {
                wishClickListener.invoke(Constant.CLICK_BUYBUTTON, data, position)
            }

            binding.root.setOnClickListener {
                wishClickListener.invoke(Constant.CLICK_VIEW, data, position)
            }


        }

    }

    object DiffUtilCallBack : DiffUtil.ItemCallback<CourseData>() {
        override fun areItemsTheSame(oldItem: CourseData, newItem: CourseData): Boolean {
            return oldItem.courseId == newItem.courseId
        }

        override fun areContentsTheSame(oldItem: CourseData, newItem: CourseData): Boolean {
            return oldItem == newItem
        }
    }


}