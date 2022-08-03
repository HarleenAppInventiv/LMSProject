package com.selflearningcoursecreationapp.ui.my_bank

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.databinding.AdapterRecyclerBankBinding

class MyBankAdapter : BaseAdapter<AdapterRecyclerBankBinding>() {
    override fun getLayoutRes() = R.layout.adapter_recycler_bank

    override fun getItemCount() = 10
}