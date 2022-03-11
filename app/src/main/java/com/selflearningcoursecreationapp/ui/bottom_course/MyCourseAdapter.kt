package com.selflearningcoursecreationapp.ui.bottom_course

import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyCourseBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.utils.SpanUtils

class MyCourseAdapter(private var isCompleted: Boolean) : BaseAdapter<AdapterMyCourseBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_my_course
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterMyCourseBinding
        val context = binding.root.context
        if (isCompleted) {
            binding.btBuy.setText(context.getString(R.string.certificate))
            binding.btBuy.icon = ContextCompat.getDrawable(context, R.drawable.ic_download_small)
            binding.tvProgress.text = "100% Completed"
            binding.pbProgress.progress = 100
        } else {
            binding.btBuy.setText(context.getString(R.string.resume))
            binding.btBuy.icon = ContextCompat.getDrawable(context, R.drawable.ic_resume)
            binding.tvProgress.text = "8% Completed"
            binding.pbProgress.progress = 10

        }
        val msg =
            SpanUtils.with(context, "10m left in lesson").endPos(8).themeColor().getSpanString()

        binding.tvDuration.setSpanString(msg)
        binding.tvCoin.visibleView(isCompleted)
        binding.tvDuration.visibleView(!isCompleted)

        binding.pbProgress.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }

    }
}