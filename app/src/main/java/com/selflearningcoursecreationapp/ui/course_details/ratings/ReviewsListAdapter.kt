package com.selflearningcoursecreationapp.ui.course_details.ratings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.AdapterReviewListBinding
import com.selflearningcoursecreationapp.extensions.getTimeAgo
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.profile.bookmark.PagerViewEvents
import com.selflearningcoursecreationapp.ui.profile.bookmark.WishListViewModel
import com.selflearningcoursecreationapp.utils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants


class ReviewsListAdapter(
    var viewModel: WishListViewModel,
    private val wishClickListener: (type: Int, wishListItem: CourseData, position: Int) -> Unit
) :
    PagingDataAdapter<CourseData, ReviewsListAdapter.ViewHolder>(DiffUtilCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<AdapterReviewListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_review_list,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, position, viewModel) }
    }


    inner class ViewHolder(var binding: AdapterReviewListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CourseData, position: Int, viewModel: WishListViewModel) {
            binding.tvUserName.text = data.name
//            binding.tvDescription.text = data.contentDescription
            binding.tvLikeCount.text = data.totalLikes.toString()
            binding.tvDislike.text = data.totalDislikes.toString()
            binding.tvDate.text = getTimeAgo(data.createdDate.toString())
            binding.ivUserLogo.loadImage(data.profileUrl, R.drawable.ic_default_user_grey)
            binding.rating.rating = data.courseRating?.toFloat() ?: 0f
            binding.ivDislike.isClickable = true
            binding.ivLike.isClickable = true
            ResizeableUtils.builder(binding.tvDescription).isBold(false)
                .isUnderline(false)
                .setFullText(data.contentDescription)
                .setFullText(R.string.read_more_arrow)
                .setLessText(R.string.read_less_arrow)
                .setSpanSize(0.9f)
                .showDots(true)
                .build()
            if (data.userDisLiked == 1) {
                binding.ivDislike.changeBackground(ThemeConstants.TYPE_THEME)
            } else {
                binding.ivDislike.imageTintList = null


            }

            if (data.userLiked == 1) {

                binding.ivLike.changeBackground(ThemeConstants.TYPE_THEME)
            } else {
                binding.ivLike.imageTintList = null


            }

//            increaseTouch( binding.ivLike,50f)
//           increaseTouch( binding.ivDislike,50f)
            binding.ivLike.setOnClickListener {
                wishClickListener.invoke(0, data, position)
                viewModel.onViewEvent(PagerViewEvents.EditLike(data))
                binding.ivLike.isClickable = false


            }


            binding.ivDislike.setOnClickListener {
                wishClickListener.invoke(1, data, position)
                viewModel.onViewEvent(PagerViewEvents.EditDisLike(data))
                binding.ivDislike.isClickable = false

            }


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




