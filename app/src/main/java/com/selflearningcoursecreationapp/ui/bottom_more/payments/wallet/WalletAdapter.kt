package com.selflearningcoursecreationapp.ui.bottom_more.payments.wallet

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyWalletBinding


class WalletAdapter() :BaseAdapter<AdapterMyWalletBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_my_wallet
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding= holder.binding as AdapterMyWalletBinding
binding.rvWallet.adapter=WalletSpendAdapter()
    }

}