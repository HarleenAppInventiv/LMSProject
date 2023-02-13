package com.selflearningcoursecreationapp.ui.moderator.qualification

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.ItemModeDocumentsBinding
import com.selflearningcoursecreationapp.models.DocModelItem
import com.selflearningcoursecreationapp.utils.Constant
import java.text.CharacterIterator
import java.text.StringCharacterIterator

class ModeCertificateDocumentsAdapter(private val list: ArrayList<DocModelItem>) :
    BaseAdapter<ItemModeDocumentsBinding>() {

    override fun getLayoutRes(): Int {


        return R.layout.item_mode_documents
    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        binding.tvDocName.text = list[position].contentName
        binding.tvDocSize.text = humanReadableByteCountSI(list[position].contentSize)

        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position, list[position].contentURL)
        }
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