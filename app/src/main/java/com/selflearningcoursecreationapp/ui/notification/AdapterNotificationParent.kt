package com.selflearningcoursecreationapp.ui.notification

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterNotificationParentBinding

class AdapterNotificationParent : BaseAdapter<AdapterNotificationParentBinding>() {
    override fun getLayoutRes() = R.layout.adapter_notification_parent
    override fun getItemCount() = 5
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        binding.rvNotificationChild.adapter = AdapterNotificationChild()

    }
}