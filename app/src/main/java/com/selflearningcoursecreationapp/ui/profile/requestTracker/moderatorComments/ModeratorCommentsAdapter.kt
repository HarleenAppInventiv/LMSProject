package com.selflearningcoursecreationapp.ui.profile.requestTracker.moderatorComments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.ItemModCommentsBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestrackerVM


class ModeratorCommentsAdapter(
    var viewModel: RequestrackerVM,
    private val clickListener: (type: Int, wishListItem: CourseData, position: Int) -> Unit
) :
    PagingDataAdapter<CourseData, ModeratorCommentsAdapter.ViewHolder>(DiffUtilCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemModCommentsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_mod_comments,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }


    inner class ViewHolder(var binding: ItemModCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CourseData) {
            binding.tvUserName.text = data.createdByName
            binding.tvCourseName.text = data.courseTitle
            binding.tvCourseDesc.text = data.categoryName
            binding.tvLanguage.text = data.languageName

            binding.ivCourseImage.loadImage(
                data.courseBannerUrl,
                R.drawable.ic_course_dummy,
                data.courseBannerHash
            )

            binding.tvRatings.text = data.averageRating
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




