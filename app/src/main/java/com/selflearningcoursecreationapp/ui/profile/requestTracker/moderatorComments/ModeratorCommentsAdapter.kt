package com.selflearningcoursecreationapp.ui.profile.requestTracker.moderatorComments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.ItemModCommentsBinding
import com.selflearningcoursecreationapp.extensions.changeDateFormat
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.builderUtils.ImageViewBuilder


class ModeratorCommentsAdapter(
    private var modComment: Boolean,
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
        getItem(position)?.let { holder.bind(it, position) }
    }


    inner class ViewHolder(var binding: ItemModCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CourseData, position: Int) {
            binding.tvUserName.text = data.createdByName
            binding.tvCourseName.text = data.courseTitle
            binding.tvCourseDesc.text = data.categoryName
            binding.tvLanguage.text = data.languageName
            binding.tvRejectedDate.text =
                data.rejectedDate.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", "MMM dd, yyyy HH:mm:ss")
            binding.tvTotalComments.text = data.totalComment.toString()

            binding.root.setOnClickListener {
                clickListener(Constant.CLICK_VIEW, data, position)
            }

            ImageViewBuilder.builder(binding.ivCourseImage)
                .placeHolder(R.drawable.ic_home_default_banner)
                .colorIndex(position)
                .blurhash(data.courseBannerHash)
                .setImageUrl(data.courseBannerUrl)
                .loadImage()


            if (!modComment) {
//                binding.constRejectDate.gone()
                binding.constTotalComment.gone()
                binding.tvRejectedDate.text =
                    data.permanentRejectedDate.changeDateFormat(
                        "yyyy-MM-dd'T'hh:mm:ss",
                        "MMM dd, yyyy HH:mm:ss"
                    )
            }

//            if (viewModel.modComment) {
//                binding.tvTextReason.visible()
//                binding.tvReason.visible()
//                binding.view1.visible()
//            }
//            binding.tvReason.text = data.comment
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




