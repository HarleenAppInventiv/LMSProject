package com.selflearningcoursecreationapp.ui.notification

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterNotificationChildBinding
import com.selflearningcoursecreationapp.extensions.getTimeAgo
import com.selflearningcoursecreationapp.models.NotificationData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

class AdapterNotificationChild(private val list: ArrayList<NotificationData>) :
    BaseAdapter<AdapterNotificationChildBinding>() {
    override fun getLayoutRes() = R.layout.adapter_notification_child
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterNotificationChildBinding
        val context = binding.root.context

//        binding.tvNotificationName.text = list[position].title
        binding.notificationModel = list[position]
        binding.executePendingBindings()

//        binding.tvNotificaitonDesc.text = list[position].desciption
        binding.tvTimeAgo.text = getTimeAgo(list.get(position).sentDate.toString())
        binding.ivCross.setOnClickListener {
            onItemClick(Constant.CLICK_DELETE, position)
        }
        binding.cvNotification.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position, list[position])
        }

        binding.cvNotification.setCardBackgroundColor(
            if (list[position].isRead == false) ThemeUtils.getSecondaryBgColor(
                context
            ) else ThemeUtils.getPrimaryBgColor(context)
        )
    }
}