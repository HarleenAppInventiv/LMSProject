package com.selflearningcoursecreationapp.base

import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.view.*
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.LiveDataObserver
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog

abstract class BaseBottomSheetDialog<DB : ViewDataBinding> : BottomSheetDialogFragment(),
    LiveDataObserver {
    protected lateinit var binding: DB
    private var clickListener: IDialogClick? = null
    lateinit var baseActivity: BaseActivity
    private var filter: InputFilter? = null

    @LayoutRes
    abstract fun getLayoutRes(): Int
    abstract fun initUi()
    open fun onApiRetry(apiCode: String) {}


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val contextThemeWrapper =
            ContextThemeWrapper(activity, R.style.AppTheme) // your app theme here
        inflater.cloneInContext(contextThemeWrapper)
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
        binding.lifecycleOwner = this
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()

        expandLayout()
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

    override fun onRetry(apiCode: String, networkError: Boolean, exception: ApiError) {
        baseActivity.retryHandling(apiCode, networkError, {
            onApiRetry(it)
        }, exception)
    }

    override fun onLoading(message: String, apiCode: String?) {
        showLoading()
    }

    private fun showLoading() {
        baseActivity.showProgressBar()
    }

    private fun hideLoading() {
        baseActivity.hideProgressBar()
    }


    fun showToastShort(message: String) {
        baseActivity.showToastShort(message)
    }

    fun showToastLong(message: String) {
        baseActivity.showToastLong(message)
    }

    fun expandLayout() {
        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet)
            // Right here!
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
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

    fun showDefaultDialog(message: String) {

        CommonAlertDialog.builder(baseActivity)
            .hideNegativeBtn(true)
            .title(this.getString(R.string.coming_soon))
            .getCallback {

            }.icon(null)
            .build()
    }
}