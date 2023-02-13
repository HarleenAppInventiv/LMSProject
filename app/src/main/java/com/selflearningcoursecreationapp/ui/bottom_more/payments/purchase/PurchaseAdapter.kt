package com.selflearningcoursecreationapp.ui.bottom_more.payments.purchase

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterMyPaymentBinding
import com.selflearningcoursecreationapp.extensions.changeDateFormat
import com.selflearningcoursecreationapp.extensions.getAttrResource
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.ui.bottom_more.payments.model.MyPurchaseList
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.PaymentStatus
import com.selflearningcoursecreationapp.utils.convertPaiseToRs


class PurchaseAdapter(var list: ArrayList<MyPurchaseList>) :
    BaseAdapter<AdapterMyPaymentBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_my_payment
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterMyPaymentBinding
        val context = binding.root.context

        binding.tvName.text = list[position].courseTitle
        binding.tvId.text =
            context.getString(R.string.trans_id) + " ${list[position].transactionId}"
        binding.tvDate.text = list[position].createdDate.changeDateFormat()

        binding.tvPrice.text =
            "- ${list[position].currencySymbol.toString()} ${list[position].amount?.convertPaiseToRs()}"
        setPaymentState(list[position], context)

        binding.ivPreview.loadImage(
            list[position].courseBannerContentURL,
            R.drawable.ic_home_default_banner,
            position
        )

        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }

        binding.tvInvoice.setOnClickListener {
            onItemClick(Constant.CLICK_INVOICE, position)
        }

        if (position + 1 == list.size) {
            onPageEnd()
        }
    }

    private fun setPaymentState(data: MyPurchaseList, context: Context) {
        when (data.paymentStatus) {
            PaymentStatus.SUCCESS -> {

                binding.tvStatus.text = context.getString(R.string.success)
                binding.tvStatus.setTextColor(
                    context.getAttrResource(R.attr.accentColor_Green)
                )
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(
                    context.getAttrResource(R.attr.accentColor_GreenTintAlpha)

                )


            }
            PaymentStatus.FAILED -> {

                binding.tvStatus.text = context.getString(R.string.failed)
                binding.tvStatus.setTextColor(
                    context.getAttrResource(R.attr.accentColor_Red)

                )
                binding.tvStatus.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            context.getAttrResource(R.attr.accentColor_RedTintAlpha)
                        )
                    )

            }
            PaymentStatus.IN_PROGRESS -> {

                binding.tvStatus.text = context.getString(R.string.in_progress)
                binding.tvStatus.setTextColor(
                    context.getAttrResource(R.attr.accentColor_Yellow)

                )
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(
                    context.getAttrResource(R.attr.accentColor_YellowTintAlpha)

                )

            }
        }
    }


}