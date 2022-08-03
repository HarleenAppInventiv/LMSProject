package com.selflearningcoursecreationapp.ui.bottom_more.cards

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSavedCardBinding
import com.selflearningcoursecreationapp.utils.Constant


class CardsAdapter : BaseAdapter<AdapterSavedCardBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.adapter_saved_card
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSavedCardBinding
        binding.btEditCard.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }
        binding.btDeleteCard.setOnClickListener {
            onItemClick(Constant.CLICK_DELETE, position)
        }

    }


}