package com.hd.videoEditor.customViews

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hd.videoEditor.R
import com.hd.videoEditor.databinding.AdapterFilterOptionBinding
import com.hd.videoEditor.model.FilterData

class FilterAdapter(
    private var filterList: ArrayList<FilterData>,
    private var bitmap: Bitmap?,
    private var onItemClick: (position: Int) -> Unit
) :
    RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    inner class FilterViewHolder(var binding: AdapterFilterOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = DataBindingUtil.inflate<AdapterFilterOptionBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_filter_option, parent, false
        )
        return FilterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val binding = holder.binding as AdapterFilterOptionBinding

        binding.ivPreview.setBackgroundResource(filterList[position].icon)
//        binding.ivFilter.filter= filterList[position].filter?.createFilterForType(binding.root.context)
        binding.tvFilter.text = filterList[position].name
        binding.root.setOnClickListener { onItemClick(position) }
        if (filterList[position].isSelected) {
            binding.ivPreview.strokeColor = ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.purple_200
                )
            )
            binding.ivPreview.strokeWidth =
                binding.root.context.resources.getDimension(R.dimen._2sdp)
        } else {
            binding.ivPreview.strokeColor = ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.trimmer_line_color
                )
            )
            binding.ivPreview.strokeWidth =
                binding.root.context.resources.getDimension(R.dimen._2sdp)
        }

    }

    override fun getItemCount(): Int {
        return filterList.size
    }
}