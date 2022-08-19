package com.selflearningcoursecreationapp.ui.moderator.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.AdapterFilterModeratorBinding
import com.selflearningcoursecreationapp.ui.moderator.dialog.filter.Payload
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants

class ModeratorFilterParentAdapter(var clickListener: (clickType: Int, position: Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var arrayOf: ArrayList<Payload>? = null


    override fun getItemCount() = arrayOf?.size ?: 0

    fun setAdapterList(payloadList: ArrayList<Payload>?) {
        if (payloadList != null) {
            arrayOf = ArrayList()
            arrayOf?.addAll(payloadList)
            notifyDataSetChanged()


        }
    }

    override fun getItemViewType(position: Int): Int {
        return arrayOf?.get(position)?.type ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is CategoryViewHolder) {
            holder.onBind(arrayOf?.get(position) ?: Payload("", false, 0), clickListener, position)


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = DataBindingUtil.inflate<AdapterFilterModeratorBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_filter_moderator,
            parent,
            false
        )
        return CategoryViewHolder(binding)

    }


}


class CategoryViewHolder(var binding: AdapterFilterModeratorBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(
        payload: Payload,
        clickListener: (adapterType: Int, type: Int) -> Unit,
        position: Int
    ) {

        binding.tvCatName.text = payload.name
        binding.tvCatName.changeFontType(
            if (payload.isSelected) ThemeConstants.FONT_MEDIUM else ThemeConstants.FONT_REGULAR
        )

        if (payload.isSelected) {
            binding.constParent.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.white
                )
            )
            binding.tvCatName.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.black
                )
            )
        } else {
            binding.constParent.setBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.color_view
                )
            )

            binding.tvCatName.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.hintColor
                )
            )
        }
        binding.tvCatName.setOnClickListener {
            clickListener.invoke(payload.type, position)
        }
    }
}


