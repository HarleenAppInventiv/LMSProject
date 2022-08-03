package com.selflearningcoursecreationapp.ui.bottom_more.payments.wallet

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.databinding.AdapterMyWalletSpendBinding


class WalletSpendAdapter : BaseAdapter<AdapterMyWalletSpendBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_my_wallet_spend
    }

    override fun getItemCount(): Int {
        return 5
    }


}