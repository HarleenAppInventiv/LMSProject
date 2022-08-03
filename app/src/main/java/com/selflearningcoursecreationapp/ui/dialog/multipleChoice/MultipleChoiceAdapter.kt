package com.selflearningcoursecreationapp.ui.dialog.multipleChoice

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSingleChoiceBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.utils.Constant


class MultipleChoiceAdapter(
    private val list: List<SingleChoiceData>,
    private var showRadio: Boolean = false
) :
    BaseAdapter<AdapterSingleChoiceBinding>() {
    override fun getLayoutRes() = R.layout.adapter_single_choice

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding as AdapterSingleChoiceBinding
        binding.tvTitle.text = list[position].title
        binding.ivSelected.visibleView(list[position].isSelected == true)
        binding.rbChecked.isChecked = list[position].isSelected == true
        binding.rbChecked.visibleView(showRadio && list[position].isSelected == false)

        binding.root.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }
        binding.rbChecked.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }


    }

    override fun getItemCount() = list.size
}