package com.selflearningcoursecreationapp.ui.profile.requestTracker.sentRequests

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.AdapterSentRequestBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestrackerVM
import com.selflearningcoursecreationapp.utils.SentRequestsStatus

class SentRequestAdapter(
    var viewModel: RequestrackerVM,
    private val clickListener: (type: Int, wishListItem: CourseData, position: Int) -> Unit,
) : PagingDataAdapter<CourseData, SentRequestAdapter.ViewHolder>(
    DiffUtilCallBack
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SentRequestAdapter.ViewHolder {
        val binding = DataBindingUtil.inflate<AdapterSentRequestBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_sent_request,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SentRequestAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, position, viewModel) }
    }

    inner class ViewHolder(var binding: AdapterSentRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CourseData, position: Int, viewModel: RequestrackerVM) {
            var context = binding.root.context
            binding.tvCourseName.text = data.courseTitle
            binding.tvCourseCategory.text = data.categoryName
            binding.tvLanguage.text = data.languageName
            binding.tvUserName.text = data.coCreatorName
            binding.ivUser.loadImage(
                data.coAuthorUrl,
                R.drawable.ic_default_user_grey,
                data.coAuthorBlurHash
            )

            if (data.requestStatus == SentRequestsStatus.ACCEPTED) {
                binding.ivAddCoAuthor.gone()
                binding.btnCancel.text = context.getString(R.string.withdraw)
            }


            binding.ivAddCoAuthor.setOnClickListener {
                clickListener.invoke(0, data, position)
            }

            binding.btnCancel.setOnClickListener {
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