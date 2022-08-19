package com.selflearningcoursecreationapp.base

import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<DB : ViewDataBinding> : RecyclerView.Adapter<BaseViewHolder>() {
    open lateinit var binding: DB
    private var itemClickListener: IViewClick? = null
    private var pageEndListener: IListEnd? = null
    private var filter: InputFilter? = null
    private var holderList: HashMap<Int, BaseViewHolder> = HashMap()
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
        holderList[position] = holder
    }

    fun getViewHolder(position: Int): BaseViewHolder? {
        return holderList[position]
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

    fun onTouch(v: View, event: MotionEvent): Boolean {
        if (v is AppCompatEditText && v.hasFocus()) {
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_SCROLL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return true
                }
            }
        }
        return false
    }

    fun setCharLimit(et: EditText, max: Int) {
        filter = InputFilter.LengthFilter(max)
        et.filters = arrayOf<InputFilter>(filter as InputFilter.LengthFilter)
    }

    fun removeFilter(et: EditText) {
        if (filter != null) {
            et.filters = arrayOfNulls(0)
            filter = null
        }
    }
}