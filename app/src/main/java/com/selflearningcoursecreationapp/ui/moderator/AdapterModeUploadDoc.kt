//package com.selflearningcoursecreationapp.ui.moderator
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.selflearningcoursecreationapp.R
//import com.selflearningcoursecreationapp.databinding.ItemModeratorFilterCatItemBinding
//import com.selflearningcoursecreationapp.databinding.ItemQualificationBinding
//import com.selflearningcoursecreationapp.ui.moderator.dialog.filter.ModeratorFilterChildAdapter
//import com.selflearningcoursecreationapp.utils.Constant
//
//class AdapterModeUploadDoc(var type:Int):RecyclerView.Adapter<ParentViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
//        when (type) {
//            Constant.TYPE_CATEGORY -> {
//
//                val binding = DataBindingUtil.inflate<ItemQualificationBinding>(
//                    LayoutInflater.from(parent.context),
//                    R.layout.item_qualification,
//                    parent,
//                    false
//                )
//                return ParentViewHolder(binding)
//
//            }
//
//            else -> {
//                val binding = DataBindingUtil.inflate<ItemModeratorFilterCatItemBinding>(
//                    LayoutInflater.from(parent.context),
//                    R.layout.item_moderator_filter_cat_item,
//                    parent,
//                    false
//                )
//                return ModeratorFilterChildAdapter.CategoryViewHolder(binding)
//
//            }
//
//
//        }
//    }
//
//    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun getItemCount(): Int {
//
//        return  if (type==1)
//        TODO("Not yet implemented")
//    }
//}
//
//class ParentViewHolder( var binding: ItemQualificationBinding) :RecyclerView.ViewHolder(binding.root){
//
//}
