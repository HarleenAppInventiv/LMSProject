package com.selflearningcoursecreationapp.ui.profile.requestTracker.coAuthorRequest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.ItemCoAuthorRequestBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestrackerVM
import com.selflearningcoursecreationapp.ui.profile.requestTracker.acceptRejectRequest.DiffUtilCallBack


class CoAuthorRequestAdapter(
    var viewModel: RequestrackerVM,
    private val clickListener: (type: Int, wishListItem: CourseData, position: Int) -> Unit,
) : PagingDataAdapter<CourseData, CoAuthorRequestAdapter.ViewHolder>(
    DiffUtilCallBack
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CoAuthorRequestAdapter.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemCoAuthorRequestBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_co_author_request,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoAuthorRequestAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, position, viewModel) }
    }

    inner class ViewHolder(var binding: ItemCoAuthorRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CourseData, position: Int, viewModel: RequestrackerVM) {
            binding.tvCourseName.text = data.courseTitle
            binding.tvCourseCategory.text = data.categoryName
            binding.tvLanguage.text = data.languageName
            binding.tvUserName.text = data.createdByName
            binding.ivUser.loadImage(
                data.profileUrl,
                R.drawable.ic_default_user_grey,
                data.profileBlurHash
            )

            binding.btnAccept.setOnClickListener {
                clickListener.invoke(0, data, position)
//                viewModel.onViewEvent(PagerViewEventsRequest.Remove(data))

            }
            binding.btnReject.setOnClickListener {
                clickListener.invoke(1, data, position)

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