package com.selflearningcoursecreationapp.ui.profile.requestTracker.acceptRejectRequest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.ItemAcceptedRequestsBinding
import com.selflearningcoursecreationapp.extensions.getAttrResource
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestrackerVM
import com.selflearningcoursecreationapp.utils.CoAuthorStatus


class AcceptedRequestsAdapter(
    var viewModel: RequestrackerVM,
    private val clickListener: (type: Int, wishListItem: CourseData, position: Int) -> Unit
) :
    PagingDataAdapter<CourseData, AcceptedRequestsAdapter.ViewHolder>(DiffUtilCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemAcceptedRequestsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_accepted_requests,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }


    inner class ViewHolder(var binding: ItemAcceptedRequestsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CourseData) {
            binding.tvUserName.text = data.createdByName
            binding.tvCourseName.text = data.courseTitle
            binding.tvCourseDesc.text = data.categoryName
            binding.tvLanguage.text = data.languageName

            when (data.requestStatus) {
                CoAuthorStatus.SUBMITTED or CoAuthorStatus.CANCELLEDWITHSUBMIT -> {
                    binding.tvStatus.apply {
                        text = binding.root.context?.getString(R.string.submitted)
                        setTextColor(
                            binding.root.context.getAttrResource(R.attr.accentColor_Green)
                        )
                    }
                }
                CoAuthorStatus.ACCEPT ->
                    binding.tvStatus.apply {
                        text = binding.root.context?.getString(R.string.accepted)
                        setTextColor(
                            binding.root.context.getAttrResource(R.attr.accentColor_Green)
                        )
                    }
                CoAuthorStatus.REJECT -> {
                    binding.tvStatus.apply {
                        text = binding.root.context?.getString(R.string.rejected)
                        setTextColor(
                            binding.root.context.getAttrResource(R.attr.accentColor_Red)
                        )
                    }
                }

            }
            binding.ivUser.loadImage(
                data.profileUrl,
                R.drawable.ic_default_user_grey,
                data.profileBlurHash
            )
//            if (viewModel.isRejected)
//                binding.tvStatus.apply {
//                    text = binding.root.context?.getString(R.string.rejected)
//                    setTextColor(
//                        binding.root.context.getAttrResource(R.attr.accentColor_Red)
//                    )
//                }
//            else
//                binding.tvStatus.apply {
//                    text = binding.root.context?.getString(R.string.accepted)
//                    setTextColor(
//                        binding.root.context.getAttrResource(R.attr.accentColor_Green)
//                    )
//                }

            binding.root.setOnClickListener {
                if (data.requestStatus == CoAuthorStatus.ACCEPT) {
                    clickListener.invoke(1, data, position)
                }

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




