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
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils

class WishListAdapter(
    var viewModel: WishListViewModel,
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
        binding.progressG.gone()
        binding.btBuy.visible()
        binding.tvDuration.gone()


        val msg =
            SpanUtils.with(context, "10m left in lesson").endPos(8).themeColor().getSpanString()

        binding.tvDuration.setSpanString(msg)

        binding.pbProgress.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }




        getItem(position)?.let { holder.bind(it, position, viewModel) }
    }


    inner class ViewHolder(var binding: AdapterMyCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CourseData, position: Int, viewModel: WishListViewModel) {
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

            binding.ivPreview.loadImage(
                data.courseBannerUrl,
                R.drawable.ic_home_default_banner,
                position
            )

            binding.ivPreview.loadImage(
                data.courseBannerUrl,
                R.drawable.ic_home_default_banner,
                data.courseBannerHash
            )
            binding.tvLesson.text =
                context.getQuantityString(R.plurals.lecture_quantity, data.totalSections)
            "${data.totalSections} " + binding.root.context.getString(R.string.lesson)
            when (data.userCourseStatus) {
                CourseStatus.ENROLLED -> {
                    binding.progressG.visible()
                    binding.bookmarkTimeG.gone()
                    binding.priceG.gone()
                    binding.tvDuration.visible()

                }
                else -> {
                    binding.tvCoin.text = data.rewardPoints

                    binding.tvNewPrice.gone()
                    binding.tvCoin.gone()
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
                            binding.tvNewPrice.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )


                        }
                    }
                }
            }


            binding.btBuy.setCourseButton(
                data.courseTypeId,
                data.userCourseStatus,
                data.paymentStatus
            )

            binding.ivBookmark.setOnClickListener {
                viewModel.onViewEvent(PagerViewEvents.Remove(data))

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