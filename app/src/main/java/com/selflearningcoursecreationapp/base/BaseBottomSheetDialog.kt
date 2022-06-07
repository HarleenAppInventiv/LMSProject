package com.selflearningcoursecreationapp.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.LiveDataObserver
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.showLog

abstract class BaseBottomSheetDialog<DB : ViewDataBinding> : BottomSheetDialogFragment(),
    LiveDataObserver {
    protected lateinit var binding: DB
    private var clickListener: IDialogClick? = null
    lateinit var baseActivity: BaseActivity

    @LayoutRes
    abstract fun getLayoutRes(): Int
    abstract fun initUi()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val contextThemeWrapper =
            ContextThemeWrapper(activity, R.style.AppTheme) // your app theme here
        inflater.cloneInContext(contextThemeWrapper)
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
        binding.lifecycleOwner = this
        dialog?.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    interface IDialogClick {
        fun onDialogClick(vararg items: Any)
    }

    fun onDialogClick(vararg items: Any) {
        clickListener?.onDialogClick(*items)
    }

    fun setOnDialogClickListener(listener: IDialogClick) {
        clickListener = listener
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            baseActivity = context
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        hideLoading()
        showLog("API_RESPONSE", "base response")

    }


    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        hideLoading()
        baseActivity.handleOnException(isNetworkAvailable, exception, apiCode)
    }


    override fun onError(error: ToastData, apiCode: String?) {
        hideLoading()
        baseActivity.handleOnError(error)
    }


    override fun onLoading(message: String, apiCode: String?) {
        showLoading()
    }

    fun showLoading() {
        baseActivity.showProgressBar()
    }

    fun hideLoading() {
        baseActivity.hideProgressBar()
    }


    fun showToastShort(message: String) {
        baseActivity.showToastShort(message)
    }

    fun showToastLong(message: String) {
        baseActivity.showToastLong(message)
    }
}