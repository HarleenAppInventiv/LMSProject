package com.selflearningcoursecreationapp.ui.notification

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterNotificationParentBinding
import com.selflearningcoursecreationapp.extensions.getTimeAgo
import com.selflearningcoursecreationapp.models.NotificationData

class AdapterNotificationParent(private val list: ArrayList<NotificationData>) :
    BaseAdapter<AdapterNotificationParentBinding>() {
    override fun getLayoutRes() = R.layout.adapter_notification_parent
    override fun getItemCount() = list?.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        binding.tvTime.text = getTimeAgo(list?.get(position)?.sentDate.toString())
//        binding.rvNotificationChild.adapter = AdapterNotificationChild(list)

//        binding.rvNotificationChild.adapter =
//            AdapterNotificationChild(list).apply {
//                setOnAdapterItemClickListener(object : IViewClick {
//                    override fun onItemClick(vararg items: Any) {
//                        if (items.isNotEmpty()) {
//                            val type = items[0] as Int
//                            val childPosition = items[1] as Int
//                            onItemClick(type, holder.adapterPosition, childPosition)
//                        }

//                    }
//                })
//            }
    }
}