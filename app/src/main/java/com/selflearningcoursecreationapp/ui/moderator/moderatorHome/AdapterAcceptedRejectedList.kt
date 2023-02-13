package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.AdapterRequestListBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseType
import com.selflearningcoursecreationapp.utils.MODSTATUS

class AdapterAcceptedRejectedList(
    val viewModel: ModHomeVM,
    val type: Int,
    private val clickListener: (type: Int, wishListItem: CourseData, position: Int) -> Unit,
) : PagingDataAdapter<CourseData, AdapterAcceptedRejectedList.ViewHolder>(
    DiffUtilCallBack
) {

    override fun onBindViewHolder(holder: AdapterAcceptedRejectedList.ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, position, viewModel) }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AdapterAcceptedRejectedList.ViewHolder {
        val binding = DataBindingUtil.inflate<AdapterRequestListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_request_list,
            parent,
            false
        )
        return ViewHolder(binding)
    }


    inner class ViewHolder(var binding: AdapterRequestListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: CourseData, position: Int, viewModel: ModHomeVM) {

            when (type) {
                MODSTATUS.ACCEPTED -> {
                    binding.btApprove.gone()
                    binding.btReject.gone()
                    binding.tvStatusApproved.visible()
                    binding.tvStatusRejected.gone()
                    binding.tvTextReason.gone()
                    binding.tvReason.gone()

                }
                MODSTATUS.REJECTED -> {
                    binding.btApprove.gone()
                    binding.btReject.gone()
                    binding.tvStatusApproved.gone()
                    binding.tvStatusRejected.visible()
                    binding.tvTextReason.visible()
                    binding.tvReason.visible()
                }
            }
            binding.root.setOnClickListener {
                clickListener.invoke(Constant.CLICK_VIEW, data, position)
            }

            binding.tvId.text = binding.root.context.getString(R.string.id) + " " + data.requestId
            binding.tvDate.text = data.createdDate.changeDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "dd MMM yyyy, hh:mm a"
            )
            binding.tvName.text = data.courseTitle
            binding.tvAuthor.text = data.createdByName
            binding.ivCertification.text = data.categoryName
            binding.ivLang.text = data.languageName
            binding.tvReason.text = data.comment
            when (data.courseTypeId) {
                CourseType.FREE -> {
                    binding.tvOldPrice.text = binding.root.context.getString(R.string.free)
                }
                CourseType.PAID -> {
                    binding.tvOldPrice.text =
                        String.format("%s %s", data.currencySymbol, data.courseFee)

                }
                CourseType.REWARD_POINTS -> {
                    binding.tvOldPrice.text = binding.root.context.getQuantityString(
                        R.plurals.point_quantity,
                        data.rewardPoints?.toIntOrNull() ?: 0
                    )

                }
                CourseType.RESTRICTED -> {
                    binding.tvOldPrice.text = binding.root.context.getString(R.string.restricted)
                }
                else -> {
                    binding.tvOldPrice.text = binding.root.context.getString(R.string.free)

                }
            }


            binding.ivPreview.loadImage(
                data.courseBannerUrl,
                R.drawable.ic_logo_default,
                data.courseLogoHash
            )

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