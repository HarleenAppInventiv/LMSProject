package com.selflearningcoursecreationapp.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.EventObserver
import com.selflearningcoursecreationapp.data.network.LiveDataObserver
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.showLog
import java.util.*


abstract class BaseFragment<DB : ViewDataBinding> : Fragment(), LiveDataObserver {
    protected lateinit var binding: DB

    @LayoutRes
    abstract fun getLayoutRes(): Int
    abstract fun onApiRetry(apiCode: String)

    lateinit var baseActivity: BaseActivity
    var spokenTextLiveData = MutableLiveData<EventObserver<String>>()

    private val startForResult = this.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val spokenText: String? =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    .let { text -> text?.get(0) }
            spokenTextLiveData.value = EventObserver(spokenText ?: "")


        }
    }


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
        showLog("BaseFragment", "onException: ${exception.message}  $apiCode ")
        hideLoading()
        baseActivity.handleOnException(isNetworkAvailable, exception, apiCode)
    }


    override fun onError(error: ToastData, apiCode: String?) {
        hideLoading()
        baseActivity.handleOnError(error)
    }

    override fun onRetry(apiCode: String, networkError: Boolean, exception: ApiError) {
        showLog("BaseFragment", "onException: ${exception.message}  $apiCode ")
        baseActivity.retryHandling(apiCode, networkError, {
            onApiRetry(it)
        }, exception)

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


    fun displaySpeechRecognizer(fragment: Fragment) {

        startForResult.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, Locale.getDefault())
        })
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


}