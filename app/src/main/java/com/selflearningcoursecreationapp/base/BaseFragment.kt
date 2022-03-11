package com.selflearningcoursecreationapp.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.LiveDataObserver
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.showLog


abstract class BaseFragment<DB : ViewDataBinding> : Fragment(), LiveDataObserver {
    protected lateinit var binding: DB

    @LayoutRes
    abstract fun getLayoutRes(): Int
    lateinit var baseActivity: BaseActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
        binding.lifecycleOwner = this
        return binding.root

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

    override fun onError(error: ToastData) {
        hideLoading()
        baseActivity.handleOnError(error)
    }

    override fun onLoading(message: String) {
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