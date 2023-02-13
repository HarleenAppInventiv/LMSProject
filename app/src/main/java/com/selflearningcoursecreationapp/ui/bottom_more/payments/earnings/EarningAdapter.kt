package com.selflearningcoursecreationapp.ui.bottom_more.payments.earnings

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyEarningBinding
import com.selflearningcoursecreationapp.extensions.changeDateFormat
import com.selflearningcoursecreationapp.extensions.getAttrResource
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.PaymentEarningsData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import com.selflearningcoursecreationapp.utils.convertPaiseToRs

class EarningAdapter(var list: ArrayList<PaymentEarningsData>) :
    BaseAdapter<AdapterMyEarningBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_my_earning
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterMyEarningBinding
        var context = binding.root.context

        binding.tvName.text = list[position].courseTitle

        binding.tvDate.text = list[position].createdDate.changeDateFormat()
        binding.tvPrice.text =
            "${list[position].currencySymbol.toString()} ${list[position].amount.convertPaiseToRs()}"

        binding.tvLearners.text = SpanUtils.with(
            context,
            context.getString(R.string.no_of_learners) + " ${list[position].totalLearner}"
        ).startPos(16)
            .textColor(context.getAttrResource(R.attr.bodyTextColor))
            .isBold()
            .getSpanString()





        binding.ivPreview.loadImage(
            list[position].courseBannerContentURL,
            R.drawable.ic_home_default_banner,
            position
        )

        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }

        binding.tvMoreDetails.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }

        if (position + 1 == list.size) {
            onPageEnd()
        }
    }


}