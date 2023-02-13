package com.selflearningcoursecreationapp.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.data.network.*
import com.selflearningcoursecreationapp.data.network.exception.ApiException
import com.selflearningcoursecreationapp.data.network.exception.UnAuthorizedException
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.isInternetAvailable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


abstract class BaseFragment<DB : ViewDataBinding> : Fragment(), LiveDataObserver, MenuProvider {
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
        showLog("LAGGING_ISSE", "onCreateView ")

        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
        binding.lifecycleOwner = this
        return binding.root

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            baseActivity = context
            showLog("LAGGING_ISSUE", "onAttach ")
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        baseActivity.lifecycleScope.launch {
            delay(150)
            baseActivity.runOnUiThread {
                hideLoading()
            }

        }
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

    fun callMenu() {
        val menuHost: MenuHost = baseActivity
        menuHost.addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    override fun onLoading(message: String, apiCode: String?) {
        showLog("onHomeLoading", "onLoading")
        showLoading()
    }

    fun showLoading(message: String? = null) {
        baseActivity.showProgressBar(message)
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

    fun showCommingSoonDialog(message: String = "") {

        CommonAlertDialog.builder(baseActivity)
            .hideNegativeBtn(true)
            .title(this.getString(R.string.coming_soon))
            .getCallback {

            }.icon(null)
            .build()
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


    override fun onDestroyView() {
        super.onDestroyView()
        showLog("LAGGING_ISSE", "onDestroyView ")
        binding.unbind()
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.course_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_read -> {
                baseActivity.checkAccessibilityService()

                true
            }
            else -> false
        }
    }


    fun handlePagingError(error: LoadState.Error, apiCode: String) {
        val isNetworkAvailable = !isInternetAvailable(baseActivity)
        when (error.error) {
            is UnAuthorizedException -> {
                baseActivity.handleOnException(isNetworkAvailable, ApiError().apply {
                    statusCode = HTTPCode.TOKEN_EXPIRED
                    exception = error.error
                }, apiCode)

            }
            is ApiException -> {
                baseActivity.handleOnException(isNetworkAvailable, ApiError().apply {
                    exception = error.error
                }, apiCode)
            }
            else -> {
                onRetry(apiCode, isNetworkAvailable, ApiError().apply {
                    this.exception = error.error
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showLog("LAGGING_ISSE", "onResume ")

    }

    override fun onPause() {
        super.onPause()
        showLog("LAGGING_ISSE", "onPause ")

    }


}