package com.selflearningcoursecreationapp.ui.moderator.dialog.filter

import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.databinding.ItemFilterRequestDateBinding
import com.selflearningcoursecreationapp.databinding.ItemModeratorFilterCatItemBinding
import com.selflearningcoursecreationapp.extensions.getStringDate
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.openDatePickerDialog
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants

class ModeratorFilterChildAdapter(
    var baseActivity: BaseActivity,
    var clickListener: (clickType: Int, position: Int) -> Unit

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var arrayOf: ArrayList<Payload>? = null
    private var type: Int? = null

    override fun getItemCount(): Int {
        return if (type == Constant.TYPE_CATEGORY) arrayOf?.size ?: 0
        else
            1

    }

    fun setAdapterList(payloadList: ArrayList<Payload>?, type: Int) {
        if (payloadList != null) {
            arrayOf = ArrayList()
            arrayOf?.clear()
            this.type = type
            arrayOf?.addAll(payloadList)
            notifyDataSetChanged()
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (type) {
            Constant.TYPE_CATEGORY -> {

                val binding = DataBindingUtil.inflate<ItemModeratorFilterCatItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_moderator_filter_cat_item,
                    parent,
                    false
                )
                return CategoryViewHolder(binding)

            }
            Constant.TYPE_REQUEST_DATE -> {

                val binding = DataBindingUtil.inflate<ItemFilterRequestDateBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_filter_request_date,
                    parent,
                    false
                )
                return RequestDateViewHolder(binding)

            }
            Constant.TYPE_FEE_RANGE -> {

                val binding = DataBindingUtil.inflate<ItemFilterRequestDateBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_filter_request_date,
                    parent,
                    false
                )
                return FeeRangeViewHolder(binding)

            }
            Constant.TYPE_CREATOR_NAME -> {

                val binding = DataBindingUtil.inflate<ItemFilterRequestDateBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_filter_request_date,
                    parent,
                    false
                )
                return NameOfCreatorViewHolder(binding)

            }
            else -> {
                val binding = DataBindingUtil.inflate<ItemModeratorFilterCatItemBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_moderator_filter_cat_item,
                    parent,
                    false
                )
                return CategoryViewHolder(binding)

            }


        }
    }

    override fun getItemViewType(position: Int): Int {
        return type ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> {
                holder.onBind(
                    arrayOf?.get(position) ?: Payload("", false, 0),
                    clickListener,
                    position,
                )


            }
            is RequestDateViewHolder -> {
                holder.onBind(
                    arrayOf?.get(position) ?: Payload("", false, 0),
                    clickListener,
                    position, baseActivity
                )
            }
            is FeeRangeViewHolder -> {
                holder.onBind(
                    arrayOf?.get(position) ?: Payload("", false, 0),
                    clickListener,
                    position
                )
            }
            is NameOfCreatorViewHolder -> {
                holder.onBind(
                    arrayOf?.get(position) ?: Payload("", false, 0),
                    clickListener,
                    position
                )
            }
        }
    }

    class CategoryViewHolder(var binding: ItemModeratorFilterCatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            payload: Payload,
            clickListener: (clickType: Int, position: Int) -> Unit,
            position: Int
        ) {
            binding.rbText.changeFontType(if (payload.isSelected) ThemeConstants.FONT_SEMI_BOLD else ThemeConstants.FONT_REGULAR)

            binding.rbText.text = payload.name
            binding.rbText.isChecked = payload.isSelected
            binding.rbText.setOnClickListener {
                clickListener.invoke(payload.type, position)

            }


        }


    }

    class RequestDateViewHolder(var binding: ItemFilterRequestDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            payload: Payload,
            clickListener: (clickType: Int, position: Int) -> Unit,
            position: Int,
            baseActivity: BaseActivity
        ) {
            binding.root.setOnClickListener {
                clickListener.invoke(payload.type, position)

            }
            binding.evStartDate.setOnClickListener {
                baseActivity.openDatePickerDialog(false) {
                    binding.evStartDate.setText(it.time.getStringDate("yyyy-MM-dd"))

                }

            }

            binding.etEnterEndDate.setOnClickListener {
                baseActivity.openDatePickerDialog(false) {
                    binding.etEnterEndDate.setText(it.time.getStringDate("yyyy-MM-dd"))

                }

            }

        }


    }

    class FeeRangeViewHolder(var binding: ItemFilterRequestDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            payload: Payload,
            clickListener: (clickType: Int, position: Int) -> Unit,
            position: Int
        ) {
            binding.root.setOnClickListener {
                clickListener.invoke(payload.type, position)

            }
            binding.evStartDate.isFocusableInTouchMode = true
            binding.etEnterEndDate.isFocusableInTouchMode = true

            binding.evStartDate.inputType = InputType.TYPE_CLASS_NUMBER
            binding.etEnterEndDate.inputType = InputType.TYPE_CLASS_NUMBER


        }

    }


    class NameOfCreatorViewHolder(var binding: ItemFilterRequestDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(
            payload: Payload,
            clickListener: (clickType: Int, position: Int) -> Unit,
            position: Int
        ) {
            binding.root.setOnClickListener {
                clickListener.invoke(payload.type, position)

            }
            binding.evStartDate.isFocusableInTouchMode = true
            binding.etEnterEndDate.isFocusableInTouchMode = true
            binding.tvTo.gone()
            binding.etEnterEndDate.gone()
            binding.tvCardNumber.text =
                binding.root.context.getString(R.string.enter_name_of_creator)

            binding.evStartDate.hint = binding.root.context.getString(R.string.enter_name)

        }

    }


}
