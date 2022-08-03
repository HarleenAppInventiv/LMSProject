package com.selflearningcoursecreationapp.ui.profile.reward.adapter

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


class RewardListAdapter(
    var type: Int, private val wishClickListener: (wishListItem: CourseData) -> Unit
) :
    PagingDataAdapter<CourseData, RewardListAdapter.ViewHolder>(DiffUtilCallBack) {

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
        getItem(position)?.let { holder.bind(it, position) }
    }


    inner class ViewHolder(var binding: AdapterMyCourseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CourseData, position: Int) {
            val context = binding.root.context

            binding.tvOldPrice.paintFlags =
                binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


            binding.bookmarkTimeG.invisible()
            binding.priceG.gone()
            binding.tvCoin.visible()
            binding.progressG.gone()
            binding.btBuy.invisible()
            binding.ivBookmark.gone()


            binding.btBuy.text = context.getString(R.string.resume)
            binding.btBuy.icon = ContextCompat.getDrawable(context, R.drawable.ic_resume)
            binding.tvDuration.gone()
            binding.progressG.gone()
            when (type) {
                Constant.COURSE_COMPLETED_REWARD -> {
                    binding.tvCoin.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.accent_color_fc6d5b
                        )
                    )
                    binding.tvCoin.background =
                        ContextCompat.getDrawable(context, R.drawable.coins_stroked_red_bg)
                    binding.tvCoin.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_rewards_points_red,
                        0,
                        0,
                        0
                    )
                }
                else -> {
                    binding.tvCoin.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.accent_color_FFB800
                        )
                    )
                    binding.tvCoin.background =
                        ContextCompat.getDrawable(context, R.drawable.coins_stroked_bg)
                    binding.tvCoin.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        R.drawable.ic_rewards_points,
                        0,
                        0,
                        0
                    )

                }
            }

            binding.tvRating.text = data.averageRating
            binding.tvCoin.text = data.rewardPoints
            binding.tvName.text = data.courseTitle
            binding.tvAuthor.text = data.createdByName
            binding.ivLang.text = data.languageName
            binding.ivCertification.text = data.categoryName

            binding.ivPreview.loadImage(data.courseBannerUrl, R.drawable.ic_dummy_course)

            binding.root.setOnClickListener {

                wishClickListener.invoke(data)
            }

            binding.cvOngoing.contentDescription =
                "this ongoing course ux and web design course by allen wen is only 8 percent completed."
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




