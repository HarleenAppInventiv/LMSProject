package com.selflearningcoursecreationapp.ui.bottom_home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.databinding.AdapterHomeCourseTypeBinding
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants

class MyAdapter(val listener: BaseAdapter.IViewClick) :
    ListAdapter<CategoryData, MyAdapter.MyViewHolder>(ITEM_COMPARATOR) {
    var selectedPos = 0

    fun from(parent: ViewGroup): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AdapterHomeCourseTypeBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return from(parent)
    }

    inner class MyViewHolder(val binding: AdapterHomeCourseTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryData) {
            binding.apply {
                tvCourseName.text = item.name
//                val getColor = ThemeUtils.getPrimaryTextColor(context = binding.root.context)
//                if (selectedPos)
                if (item.isSelected) {
                    binding.tvCourseName.changeBackgroundColor(ThemeConstants.TYPE_THEME)
                } else {
                    binding.tvCourseName.setBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.offwhite
                        )
                    )
                }

                itemPos = adapterPosition
                selectedPosition = selectedPos
                tvCourseName.setOnClickListener {
                    selectedPos = adapterPosition
                    listener.onItemClick(Constant.CLICK_VIEW, adapterPosition)
                }
            }
            binding.executePendingBindings()
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    object ITEM_COMPARATOR : DiffUtil.ItemCallback<CategoryData>() {
        override fun areItemsTheSame(oldItem: CategoryData, newItem: CategoryData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CategoryData, newItem: CategoryData): Boolean {
            return oldItem == newItem
        }
    }
}
/*

class AdapterCourseType(private val list: ArrayList<String>) :
    BaseAdapter<AdapterHomeCourseTypeBinding>() {

    var selectedPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        return super.onCreateViewHolder(parent, viewType)
    }

    override fun getLayoutRes() = R.layout.adapter_home_course_type
    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        //super.onBindViewHolder(holder, position)
        val ctx = binding.root.context

        if (selectedPos == position) {
            binding.tvCourseName.setBackgroundColor(getColor(ctx, R.color.accent_color_fc6d5b))
            binding.tvCourseName.setTextColor(getColor(ctx, R.color.white))
        } else {
            binding.tvCourseName.setBackgroundColor(getColor(ctx, R.color.white))
            binding.tvCourseName.setTextColor(getColor(ctx, R.color.black))
        }

        binding.tvCourseName.apply {
            text = list[position]
            setOnClickListener {
                selectedPos = position
                onItemClick(Constant.CLICK_VIEW, text)
            }
        }
        binding.executePendingBindings()
        // super.onBindViewHolder(holder, position)
    }

    object ITEM_COMPARATOR : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
}*/
