package com.selflearningcoursecreationapp.ui.moderator

import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.ItemQualificationBinding
import java.text.CharacterIterator
import java.text.StringCharacterIterator

class AdapterBecomeMode(
    var dataList: ArrayList<ModeCertificate>,
    var onClick: (position: Int, type: Int) -> Unit
) : BaseAdapter<ItemQualificationBinding>() {
    override fun getLayoutRes() = R.layout.item_qualification

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as ItemQualificationBinding
        if (dataList[position].id.isNotEmpty()) {

            binding.constParent.setBackgroundResource(R.drawable.rounded_corner)

            binding.tvDocName.text = dataList.get(position).filePath.name
            binding.tvDocSize.text =
                humanReadableByteCountSI(dataList.get(position).filePath.length())
            binding.grpDoc.visibility = View.VISIBLE
            binding.imgAdd.visibility = View.GONE
            binding.imgRemove.visibility = View.VISIBLE

        } else {
            binding.constParent.setBackgroundResource(R.drawable.ic_dotted_square)

            binding.grpDoc.visibility = View.GONE
            binding.imgAdd.visibility = View.VISIBLE
            binding.imgRemove.visibility = View.GONE


        }


        binding.imgRemove.setOnClickListener {

            if (dataList[position].id.isNotEmpty()) {
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

    fun humanReadableByteCountSI(byte: Long): String {
        var bytes = byte
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current())
    }


}
