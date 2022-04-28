package com.selflearningcoursecreationapp.ui.bottom_more.payments.purchase

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyPaymentBinding
import com.selflearningcoursecreationapp.utils.Constant


class PurchaseAdapter() :BaseAdapter<AdapterMyPaymentBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_my_payment
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterMyPaymentBinding
        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }
    }

}