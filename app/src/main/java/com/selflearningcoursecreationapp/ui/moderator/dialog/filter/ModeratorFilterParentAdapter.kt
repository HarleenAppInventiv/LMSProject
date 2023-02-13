package com.selflearningcoursecreationapp.ui.moderator.dialog.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.AdapterFilterModeratorBinding
import com.selflearningcoursecreationapp.extensions.getAttrResource
import com.selflearningcoursecreationapp.models.moderator.ModFilterOptionData
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants

class ModeratorFilterParentAdapter(var clickListener: (clickType: Int, position: Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var arrayOf: ArrayList<ModFilterOptionData>? = null


    override fun getItemCount() = arrayOf?.size ?: 0

    fun setAdapterList(payloadList: ArrayList<ModFilterOptionData>?) {
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
            holder.onBind(
                arrayOf?.get(position) ?: ModFilterOptionData(0, "", false, 0),
                clickListener,
                position
            )


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
        payload: ModFilterOptionData,
        clickListener: (adapterType: Int, type: Int) -> Unit,
        position: Int
    ) {

        binding.tvCatName.text = payload.name
        binding.tvCatName.changeFontType(
            if (payload.isSelected) ThemeConstants.FONT_MEDIUM else ThemeConstants.FONT_REGULAR
        )

        if (payload.isSelected) {
            binding.constParent.setBackgroundColor(
                binding.root.context.getAttrResource(R.attr.screenBackgroundColor)
            )
            binding.tvCatName.changeTextColor(ThemeConstants.TYPE_BLACK)
        } else {
            binding.constParent.setBackgroundColor(
                binding.root.context.getAttrResource(R.attr.secondaryScreenBgColor)
            )
            binding.tvCatName.changeTextColor(ThemeConstants.TYPE_BODY)

//            binding.constParent.setBackgroundColor(
//                ContextCompat.getColor(
//                    binding.root.context,
//                    R.color.mod_filter_stroke_color
//                )
//            )
//
//            binding.tvCatName.setTextColor(
//                ContextCompat.getColor(
//                    binding.root.context,
//                    R.color.hintColor
//                )
//            )
        }
        binding.tvCatName.setOnClickListener {
            clickListener.invoke(payload.type, position)
        }
    }
}


