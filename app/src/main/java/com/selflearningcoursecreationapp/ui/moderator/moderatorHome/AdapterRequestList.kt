package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterRequestListBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ModeratorListType

class AdapterRequestList(private val type: Int) : BaseAdapter<AdapterRequestListBinding>(),
    BaseAdapter.IViewClick {
    override fun getLayoutRes() = R.layout.adapter_request_list
    override fun getItemCount() = 10
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        when (type) {
            ModeratorListType.REQUESTED -> {
                binding.btApprove.visible()
                binding.btReject.visible()
                binding.tvStatusApproved.gone()
                binding.tvStatusRejected.gone()
                binding.tvTextReason.gone()
                binding.tvReason.gone()

            }
            ModeratorListType.APPROVED -> {
                binding.btApprove.gone()
                binding.btReject.gone()
                binding.tvStatusApproved.visible()
                binding.tvStatusRejected.gone()
                binding.tvTextReason.gone()
                binding.tvReason.gone()
            }
            ModeratorListType.REJECTED -> {
                binding.btApprove.gone()
                binding.btReject.gone()
                binding.tvStatusApproved.gone()
                binding.tvStatusRejected.visible()
                binding.tvTextReason.visible()
                binding.tvReason.visible()
            }
        }

        binding.btApprove.setOnClickListener {
            onItemClick(Constant.CLICK_ACCEPT, position)
        }
        binding.btReject.setOnClickListener {
            onItemClick(Constant.CLICK_REJECT, position)
        }

    }

}