package com.selflearningcoursecreationapp.ui.moderator.moderatorHome.accepted

import android.annotation.SuppressLint
import android.util.Log
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
import com.selflearningcoursecreationapp.utils.ModHomeConst


class AdapterRequestList(
    private val type: Int,
    private val clickListener: (type: Int, data: CourseData, position: Int) -> Unit,
) : PagingDataAdapter<CourseData, AdapterRequestList.ViewHolder>(
    DiffUtilCallBack
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = DataBindingUtil.inflate<AdapterRequestListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_request_list,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { data ->
            holder.bind(data, position)


        }

    }

    inner class ViewHolder(var binding: AdapterRequestListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: CourseData, position: Int) {
            val context = binding.root.context
            binding.btApprove.visible()
            binding.btReject.visible()
            binding.tvStatusApproved.gone()
            binding.tvStatusRejected.gone()
            binding.tvTextReason.gone()
            binding.tvReason.gone()
            binding.tvId.text = binding.root.context.getString(R.string.id) + " " + data.requestId
            Log.e("createddate in ", "detail" + data.createdDate)
            binding.tvDate.text =
                    /*if (type == ModHomeConst.REQUEST)*/
                data.createdDate.changeDateFormat(outputFormat = "dd MMM yyyy")/* else data.modifiedDate.changeDateFormat(
                    outputFormat = "dd MMM yyyy"
                )*/
            binding.tvName.text = data.courseTitle
            binding.tvAuthor.text = data.createdByName
            binding.ivCertification.text = data.categoryName
            binding.ivLang.text = data.languageName

//            binding.btApprove.setOnClickListener {
//                clickListener.invoke(Constant.CLICK_ACCEPT, data,position)
//            }
//         binding.btReject.setOnClickListener {
//                clickListener.invoke(Constant.CLICK_REJECT, data,position)
//            }

            binding.btApprove.setOnClickListener {
                showLog("MOD_HOME", "APPROVE CLICK")
                clickListener.invoke(Constant.CLICK_ACCEPT, data, position)
            }
            binding.btReject.setOnClickListener {
                showLog("MOD_HOME", "REJECT CLICK")
                clickListener.invoke(Constant.CLICK_REJECT, data, position)
            }
            binding.root.setOnClickListener {
//                showLog("MOD_HOME", "ROOT CLICK")
                clickListener.invoke(Constant.CLICK_VIEW, data, position)

            }

            when (type) {
                ModHomeConst.REQUEST -> {
                    binding.btApprove.text = context.getString(R.string.i_can_moderate)
                    binding.btReject.text = context.getString(R.string.i_am_busy)
                }
                else -> {
                    binding.btApprove.text = context.getString(R.string.approve)
                    binding.btReject.text = context.getString(R.string.reject)

                }
            }

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
//                        data.rewardPoints + " " + binding.root.context.getString(R.string.points)
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