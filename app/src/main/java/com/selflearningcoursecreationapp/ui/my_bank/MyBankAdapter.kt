package com.selflearningcoursecreationapp.ui.my_bank

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterRecyclerBankBinding
import com.selflearningcoursecreationapp.extensions.getAttrResource
import com.selflearningcoursecreationapp.extensions.visibleView

class MyBankAdapter(
    var payloadList: ArrayList<BankDetails>,
    var onClick: ((id: BankDetails) -> Unit)
) :
    BaseAdapter<AdapterRecyclerBankBinding>() {
    override fun getLayoutRes() = R.layout.adapter_recycler_bank
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterRecyclerBankBinding
        var context = binding.root.context

        binding.imgThreeDots.setOnClickListener {

            onClick.invoke(payloadList[position])
        }


        binding.imgThreeDots.visibleView(!payloadList[position].isPrimaryChecked)
        binding.tvType.visibleView(payloadList[position].isPrimaryChecked)

        binding.data = payloadList[position]

        /**
         * @author 1=Pending ,2=completed ,3= failed
         */

        when (payloadList[position].status) {
            1 -> {
                binding.tvStatus.setBackgroundColor(context.getAttrResource(R.attr.accentColor_YellowTintAlpha))
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.colorVariant_DarkYellow))
                binding.tvStatus.text = binding.tvStatus.context.getString(R.string.pending)
            }
            2 -> {
                binding.tvStatus.setBackgroundColor(context.getAttrResource(R.attr.accentColor_GreenTintAlpha))
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.colorVariant_DarkGreen))
                binding.tvStatus.text = binding.tvStatus.context.getString(R.string.verified)


            }
            3 -> {
                binding.tvStatus.setBackgroundColor(
                    context.getAttrResource(R.attr.accentColor_WineRedTintAlpha)
                )
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.colorVariant_ErrorRed))
                binding.tvStatus.text = binding.tvStatus.context.getString(R.string.failed)

            }
            else -> {
                binding.tvStatus.setBackgroundColor(context.getAttrResource(R.attr.accentColor_YellowTintAlpha))
                binding.tvStatus.setTextColor(context.getAttrResource(R.attr.colorVariant_DarkYellow))
                binding.tvStatus.text = binding.tvStatus.context.getString(R.string.queued)
            }

        }

//        binding.appCompatImageView3.loadImage(payloadList[position].ima)


    }

    override fun getItemCount() = payloadList.size
}