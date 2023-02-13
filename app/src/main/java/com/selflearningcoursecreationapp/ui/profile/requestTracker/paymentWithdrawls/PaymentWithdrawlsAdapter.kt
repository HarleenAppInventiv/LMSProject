package com.selflearningcoursecreationapp.ui.profile.requestTracker.paymentWithdrawls

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterPaymentWithdrawlsBinding
import com.selflearningcoursecreationapp.extensions.changeDateFormat
import com.selflearningcoursecreationapp.extensions.getAttrResource
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.utils.PaymentWithdrawlStatys
import com.selflearningcoursecreationapp.utils.convertPaiseToRs

class PaymentWithdrawlsAdapter(var list: ArrayList<PaymentWithdrawList>) :
    BaseAdapter<AdapterPaymentWithdrawlsBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_payment_withdrawls
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterPaymentWithdrawlsBinding
        var context = binding.root.context
        binding.tvRequestId.text = "#${list[position].requestId}"
        binding.tvAmount.text = "₹ ${list[position].amount.convertPaiseToRs()}"
        binding.tvAccountNumber.text = "${list[position].maskAccountNumber}"
        binding.tvBankName.text = "${list[position].bankName}"
        binding.tvDate.text =
            list[position].createdDate.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", "dd MMM yyyy")
        binding.tvActionDate.text =
            list[position].modifiedDate.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", "dd MMM yyyy")

        when (list[position].status) {
            PaymentWithdrawlStatys.BLOCKED -> {
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Black))

                binding.tvStatus.text = context.getString(R.string.blocked)
            }
            PaymentWithdrawlStatys.ACCEPTED -> {
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Green))

                binding.tvStatus.text = context.getString(R.string.approved)
            }
            PaymentWithdrawlStatys.PENDING -> {
                binding.tvStatus.text = context.getString(R.string.pending)
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Yellow))
                binding.tvActionDate.gone()
                binding.textActionDate.gone()
            }
            PaymentWithdrawlStatys.REJECTED -> {
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Red))
                binding.tvStatus.text = context.getString(R.string.rejected)
            }

            PaymentWithdrawlStatys.CANCELLED -> {
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Red))
                binding.tvStatus.text = context.getString(R.string.cancelled)
            }
        }

        if (position + 1 == list.size) {
            onPageEnd()
        }
    }

}

