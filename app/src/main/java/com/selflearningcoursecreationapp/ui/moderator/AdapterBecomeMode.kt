package com.selflearningcoursecreationapp.ui.moderator

import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.ItemQualificationBinding
import java.text.DecimalFormat

class AdapterBecomeMode(
    var dataList: ArrayList<ModeCertificate>,
    var onClick: (position: Int, type: Int) -> Unit
) : BaseAdapter<ItemQualificationBinding>() {
    override fun getLayoutRes() = R.layout.item_qualification

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as ItemQualificationBinding
        if (dataList[position].id.isNotEmpty()) {

            binding.tvDocName.text = dataList.get(position).filePath.name
            binding.tvDocSize.text =
                getStringSizeLengthFile(dataList.get(position).filePath.length())
            binding.grpDoc.visibility = View.VISIBLE
            binding.imgAdd.visibility = View.GONE
            binding.imgRemove.visibility = View.VISIBLE

        } else {
            binding.grpDoc.visibility = View.GONE
            binding.imgAdd.visibility = View.VISIBLE
            binding.imgRemove.visibility = View.GONE


        }


        binding.imgRemove.setOnClickListener {

            if (dataList.get(position).id.isNotEmpty()) {
                onClick.invoke(position, 0)
            }


        }
        binding.imgAdd.setOnClickListener {
            onClick.invoke(position, 1)
        }

    }


    override fun getItemCount() = dataList.size
    fun addPayload(dataList: ArrayList<ModeCertificate>) {
        this.dataList = dataList
        notifyDataSetChanged()

    }


    private fun getStringSizeLengthFile(size: Long): String {
        val df = DecimalFormat("0.00")
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        return when {
            size < sizeMb -> df.format(size / sizeKb)
                .toString() + " Kb"
            size < sizeGb -> df.format(size / sizeMb)
                .toString() + " Mb"
            size < sizeTerra -> df.format(size / sizeGb)
                .toString() + " Gb"
            else -> ""
        }
    }


}
