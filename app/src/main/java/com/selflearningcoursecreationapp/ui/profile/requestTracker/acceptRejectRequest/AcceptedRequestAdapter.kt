package com.selflearningcoursecreationapp.ui.profile.requestTracker.acceptRejectRequest

import android.content.Context
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.ItemAcceptedRequestsBinding

class AcceptedRequestAdapter(private var isRejected: Boolean, var context: Context?) :
    BaseAdapter<ItemAcceptedRequestsBinding>() {
    override fun getLayoutRes() = R.layout.item_accepted_requests

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (isRejected)
            binding.tvStatus.text = context?.getString(R.string.reject)
        else
            binding.tvStatus.text = context?.getString(R.string.accepted)
    }

    override fun getItemCount() = 10
}