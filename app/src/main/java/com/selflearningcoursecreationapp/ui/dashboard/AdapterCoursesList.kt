package com.selflearningcoursecreationapp.ui.dashboard

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyCourseBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.utils.SpanUtils

class AdapterCoursesList : BaseAdapter<AdapterMyCourseBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_my_course
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterMyCourseBinding
        val context = binding.root.context

        binding.bookmarkTimeG.gone()
        binding.priceG.gone()
        binding.tvCoin.gone()
        binding.progressG.gone()
        binding.tvDuration.gone()
//        when (type) {
//            Constant.COURSE_COMPLETED -> {
//                binding.btBuy.text = context.getString(R.string.certificate)
//                binding.btBuy.icon =
//                    ContextCompat.getDrawable(context, R.drawable.ic_download_small)
//                binding.tvProgress.text = "100% Completed"
//                binding.pbProgress.progress = 100
//                binding.tvCoin.visible()
//                binding.progressG.visible()
//
//            }
//            Constant.COURSE_BOOKMARKED -> {
//
//                when (position) {
//                    2 -> {
//                        binding.btBuy.text = context.getString(R.string.certificate)
//                        binding.btBuy.icon =
//                            ContextCompat.getDrawable(context, R.drawable.ic_download_small)
//                        binding.tvProgress.text = "100% Completed"
//                        binding.pbProgress.progress = 100
//                        binding.tvCoin.visible()
//                        binding.progressG.visible()
//                    }
//                    1 -> {
//                        binding.btBuy.text = context.getString(R.string.resume)
//                        binding.btBuy.icon =
//                            ContextCompat.getDrawable(context, R.drawable.ic_resume)
//                        binding.tvProgress.text = "8% Completed"
//                        binding.pbProgress.progress = 10
//                        binding.tvDuration.visible()
//                        binding.progressG.visible()
//                    }
//                    else -> {
//                        binding.bookmarkTimeG.visible()
//                        binding.priceG.visible()
//                        binding.btBuy.text = context.getString(R.string.buy_now)
//                        binding.btBuy.icon = null
//                    }
//                }
//            }
//            else -> {
//                binding.btBuy.text = context.getString(R.string.resume)
//                binding.btBuy.icon = ContextCompat.getDrawable(context, R.drawable.ic_resume)
//                binding.tvProgress.text = "8% Completed"
//                binding.pbProgress.progress = 10
//                binding.tvDuration.visible()
//                binding.progressG.visible()
//            }
//        }


        val msg =
            SpanUtils.with(context, "10m left in lesson").endPos(8).themeColor().getSpanString()

        binding.tvDuration.setSpanString(msg)

        binding.pbProgress.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }


        binding.cvOngoing.contentDescription =
            "this ongoing course ux and web design course by allen wen is only 8 percent completed."
    }
}