package com.selflearningcoursecreationapp.ui.custom_camera

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.databinding.ItemFlashCameraBinding

class CameraArAdapter(val viewType: Int, var listener: (position: Int) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var rationPos = 0
    var flashPos = 0
    var timerPos = 0

    val ratioList = arrayListOf<Int>(R.drawable.ratio_34, R.drawable.ratio_34, R.drawable.ratio_34)
    val flashList = arrayListOf<String>("AUTO", "ON", "OFF")
    val timerList = arrayListOf<String>("OFF", "3S", "10S")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (this.viewType) {
            1 -> {
                val binding = ItemFlashCameraBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return FlashCamerViewHolder(binding)
            }
//            2->{
//                val binding = ItemTextAligmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                return RatioViewHolder(binding)
//            }
            3 -> {
                val binding = ItemFlashCameraBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return TimerCamerViewHolder(binding)
            }
            else -> {
                val binding = ItemFlashCameraBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return FlashCamerViewHolder(binding)
            }
        }
    }

    inner class TimerCamerViewHolder(var binding: ItemFlashCameraBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData() {
            binding.tvAuto.text = timerList[bindingAdapterPosition]
            itemView.setOnClickListener {
                timerPos = bindingAdapterPosition
                listener.invoke(bindingAdapterPosition)
                notifyDataSetChanged()
            }
        }

    }

    inner class FlashCamerViewHolder(var binding: ItemFlashCameraBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData() {
            binding.tvAuto.text = flashList[bindingAdapterPosition]
            itemView.setOnClickListener {
                flashPos = bindingAdapterPosition
                listener.invoke(bindingAdapterPosition)
                notifyDataSetChanged()
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder is RatioViewHolder){
//            holder.setData()
//            if (rationPos == position) {
//                holder.binding.ivAlign.backgroundTintList = ContextCompat.getColorStateList(holder.binding.ivAlign.context, R.color.subscribe_color)
//            } else {
//                holder.binding.ivAlign.backgroundTintList = ContextCompat.getColorStateList(holder.binding.ivAlign.context, R.color.black)
//            }
//        }


        if (holder is FlashCamerViewHolder) {
            holder.setData()
            if (flashPos == position) {
                holder.binding.tvAuto.backgroundTintList = ContextCompat.getColorStateList(
                    holder.binding.tvAuto.context,
                    R.color.error_red
                )
            } else {
                holder.binding.tvAuto.backgroundTintList =
                    ContextCompat.getColorStateList(holder.binding.tvAuto.context, R.color.black)
            }
        }

        if (holder is TimerCamerViewHolder) {
            holder.setData()
            if (timerPos == position) {
                holder.binding.tvAuto.backgroundTintList = ContextCompat.getColorStateList(
                    holder.binding.tvAuto.context,
                    R.color.error_red
                )
            } else {
                holder.binding.tvAuto.backgroundTintList =
                    ContextCompat.getColorStateList(holder.binding.tvAuto.context, R.color.black)
            }
        }


    }

    override fun getItemCount(): Int {
        return ratioList.size
    }

//    inner class RatioViewHolder(var binding: ItemTextAligmentBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun setData() {
//            binding.ivAlign.setImageResource(ratioList[bindingAdapterPosition])
//            itemView.setOnClickListener {
//                rationPos = bindingAdapterPosition
//                listener.invoke(bindingAdapterPosition)
//                notifyDataSetChanged()
//            }
//
//        }
//    }

}

