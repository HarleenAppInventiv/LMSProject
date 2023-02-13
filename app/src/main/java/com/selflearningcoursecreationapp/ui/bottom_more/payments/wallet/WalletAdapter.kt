package com.selflearningcoursecreationapp.ui.bottom_more.payments.wallet

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyWalletSpendBinding
import com.selflearningcoursecreationapp.extensions.changeDateFormat
import com.selflearningcoursecreationapp.extensions.getAttrResource
import com.selflearningcoursecreationapp.utils.WalletHistoryStatus
import com.selflearningcoursecreationapp.utils.WithdrawalType
import com.selflearningcoursecreationapp.utils.convertPaiseToRs


class WalletAdapter(var list: ArrayList<AmountList>) : BaseAdapter<AdapterMyWalletSpendBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_my_wallet_spend
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterMyWalletSpendBinding
        var context = binding.root.context

        binding.tvPrice.text = "₹ ${list[position].amount.convertPaiseToRs()}"
        binding.tvDate.text = list[position].modifiedDate.changeDateFormat(
            "yyyy-MM-dd'T'hh:mm:ss",
            "dd/MM/yyyy "
        )
        if (list[position].transactionTypeId == WithdrawalType.MANUAL_WITHDRAW_AMOUNT) {
            binding.tvTitle.text =
                context.getString(R.string.money_withdrawal) + " (${context.getString(R.string.manual)})"
        }
//        binding.tvTitle.text = context.getString(R.string.money_withdrawal)


        if (position + 1 == list.size) {
            onPageEnd()
        }
        when (list[position].status) {
            WalletHistoryStatus.FAILED -> {
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Red))

                binding.tvStatus.text = context.getString(R.string.failed)
            }
            WalletHistoryStatus.PENDING -> {
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Yellow))

                binding.tvStatus.text = context.getString(R.string.pending)
            }
            WalletHistoryStatus.REJECTED -> {
                binding.tvStatus.text = context.getString(R.string.rejected)
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Red))
            }
            WalletHistoryStatus.PROCESSING -> {
                binding.tvStatus.text = context.getString(R.string.processing)
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Yellow))
            }
            WalletHistoryStatus.PROCESSED -> {
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Green))
                binding.tvStatus.text = context.getString(R.string.processed)
            }
            WalletHistoryStatus.REVERSED -> {
                binding.tvStatus.text = context.getString(R.string.reversed)
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Red))
            }
            WalletHistoryStatus.QUEUED -> {
                binding.tvStatus.text = context.getString(R.string.queued)
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.accentColor_Yellow))
            }


        }

    }

}