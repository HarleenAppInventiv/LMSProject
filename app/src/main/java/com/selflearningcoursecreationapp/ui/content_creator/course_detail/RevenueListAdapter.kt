package com.selflearningcoursecreationapp.ui.content_creator.course_detail

import RevenueDataList
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterRevenueListBinding
import com.selflearningcoursecreationapp.extensions.changeDateFormat
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.RevenueType
import com.selflearningcoursecreationapp.utils.convertPaiseToRs

class RevenueListAdapter(
    var list: ArrayList<RevenueDataList>,
    var revenueType: Int,
) : BaseAdapter<AdapterRevenueListBinding>() {
    override fun getLayoutRes() = R.layout.adapter_revenue_list
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        val binding = holder.binding as AdapterRevenueListBinding
        val context = binding.root.context


        binding.tvUserImage.text = list[position].learnerName


        binding.tvEnrollDate.text =
            "${context.getString(R.string.enrolled)} ${list[position].enrolledDate.changeDateFormat()}"

        binding.tvRating.visibleView(list[position].courseRating != 0f)
        binding.tvRatingNa.visibleView(list[position].courseRating == 0f)
        binding.tvRating.text = "${list[position].courseRating.toString()}"


        binding.ivProfileImage.loadImage(
            list[position].profileUrl,
            R.drawable.ic_default_user_grey,
            list[position].profileBlurHash
        )


        if (revenueType == RevenueType.PURCHASED) {
            binding.tvRevenue.text =
                "${list[position].currencySymbol.toString()} ${list[position].amount?.convertPaiseToRs()}"
        } else if (revenueType == RevenueType.REWARD_POINTS) {
            binding.tvRevenue.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_rewards_points,
                0,
                0,
                0
            )
            binding.tvRevenue.text = " ${list[position].amount?.toInt()}"
        }

        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }

        binding.btInvoice.setOnClickListener {
            onItemClick(Constant.CLICK_INVOICE, position)
        }

        if (position + 1 == list.size) {
            onPageEnd()
        }


    }

    override fun getItemCount() = list.size


}