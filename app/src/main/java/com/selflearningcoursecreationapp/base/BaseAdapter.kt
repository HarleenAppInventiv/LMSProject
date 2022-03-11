package com.selflearningcoursecreationapp.base

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<DB : ViewDataBinding>() : RecyclerView.Adapter<BaseViewHolder>() {
    open lateinit var binding: DB
    private var itemClickListener: IViewClick? = null
    private var pageEndListener: IListEnd? = null

    @LayoutRes
    abstract fun getLayoutRes(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            getLayoutRes(),
            parent,
            false
        )

        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Log.d("main", "BaseAdapter")
    }

    override fun getItemCount(): Int {
        return 0
    }

    interface IViewClick {
        fun onItemClick(vararg items: Any)
    }

    interface IListEnd {
        fun onPageEnd(vararg items: Any)
    }

    fun onItemClick(vararg items: Any) {
        itemClickListener?.onItemClick(*items)
    }

    fun onPageEnd(vararg items: Any) {
        pageEndListener?.onPageEnd(*items)
    }

    fun setOnAdapterItemClickListener(listener: IViewClick) {
        itemClickListener = listener
    }

    fun setOnPageEndListener(listener: IListEnd) {
        pageEndListener = listener
    }


}