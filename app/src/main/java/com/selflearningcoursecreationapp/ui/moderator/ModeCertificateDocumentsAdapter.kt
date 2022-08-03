package com.selflearningcoursecreationapp.ui.moderator

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.ItemModeDocumentsBinding

class ModeCertificateDocumentsAdapter :
    BaseAdapter<ItemModeDocumentsBinding>() {

    override fun getLayoutRes(): Int {


        return R.layout.item_mode_documents
    }

    override fun getItemCount(): Int {
        return 4

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as ItemModeDocumentsBinding


    }
}