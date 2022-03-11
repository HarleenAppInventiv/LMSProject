package com.selflearningcoursecreationapp.ui.bottom_more.payments.earnings

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyEarningBinding

class EarningAdapter() :BaseAdapter<AdapterMyEarningBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_my_earning
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

    }

}