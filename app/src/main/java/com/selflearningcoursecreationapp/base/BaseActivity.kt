package com.selflearningcoursecreationapp.base

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.data.network.LiveDataObserver
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.extensions.setTransparentLightStatusBar
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.authentication.InitialActivity
import com.selflearningcoursecreationapp.ui.dialog.ProgressDialog
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.NetworkFailDialogue
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.ui.moderator.ModeratorActivity
import com.selflearningcoursecreationapp.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.util.*

open class BaseActivity : AppCompatActivity(), LiveDataObserver, BaseDialog.IDialogClick,
    /*PaymentResultListener,*/ PaymentResultWithDataListener,
    ExternalWalletListener, RazorpayUtils.RazorPayCallback {

    private var progressDialog: ProgressDialog? = null
    private var onClickDialog: IBaseDialogClick? = null
    private var orderData: OrderData? = null
    val razorpayUtils = RazorpayUtils()

    var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        razorpayUtils.initRazorPay(this)
        setTransparentLightStatusBar()
        changeAppLanguage()
        getRefreshToken()
        val exceptionHandler by inject<ExceptionHandler>()
        exceptionHandler.initialize(this)
        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)
    }


    private fun getAccessibilityService(): Boolean {
        var accessibilityEnabled = 0
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            Log.v(TAG.ACCESSIBILITY, "accessibilityEnabled = $accessibilityEnabled")
        } catch (e: Settings.SettingNotFoundException) {
            Log.e(
                TAG.ACCESSIBILITY,
                "Error finding setting, default accessibility to not found: " + e.message
            )
        }

        return if (accessibilityEnabled == 1) {
            Log.v(TAG.ACCESSIBILITY, "Accessibility Is Enabled")
            true
        } else {
            false
        }
    }

    fun showProgressBar() {
        if (!isDestroyed) {
            if (progressDialog == null) {
                progressDialog = ProgressDialog(this)
                progressDialog?.let { dialog ->
                    dialog.setCancelable(false)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            } else if (progressDialog!!.isShowing)
                progressDialog!!.dismiss()

            progressDialog!!.show()
        }

    }

    fun hideProgressBar() {
        progressDialog?.let { if (it.isShowing) it.dismiss() }
    }


    fun handleOnException(networkError: Boolean, exception: ApiError, apiCode: String) {

        if (isDestroyed || isFinishing)
            return
        when (exception.statusCode) {
            HTTPCode.TOKEN_EXPIRED -> {
                goToInitialActivity()
            }

        }
        exception.message?.let {
            showLog("API_ERROR", it)
            showToastShort(it)
        }

    }

    fun goToInitialActivity() {
        SelfLearningApplication.token = ""
        lifecycleScope.launch {
            withContext(lifecycleScope.coroutineContext) {
                PreferenceDataStore.saveString(Constants.USER_TOKEN, "")
                PreferenceDataStore.saveString(Constants.USER_RESPONSE, null)
            }
        }
        startActivity(Intent(this@BaseActivity, InitialActivity::class.java))
        finish()
    }

    fun showToastShort(message: String) {
        showLog("SHOW_TOAST", message)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(message: String) {
        showLog("SHOW_TOAST", message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    fun handleOnError(error: ToastData) {
        error.errorCode?.let {
            showLog("ERROR", getString(it))
            showToastShort(getString(it))
        }
        error.errorString?.let {
            showToastShort(it)
        }

    }


    fun retryHandling(
        apiCode: String,
        networkError: Boolean,
        callBack: (apiCode: String) -> Unit,
        exception: ApiError
    ) {
        hideProgressBar()
        NetworkFailDialogue().apply {
            setOnDialogClickListener(object : BaseDialog.IDialogClick {
                override fun onDialogClick(vararg items: Any) {
                    callBack(apiCode)
                }

            })
            arguments = bundleOf(
                "apiCode" to apiCode,
                "networkError" to networkError,
                "exception" to exception
            )
        }.show(supportFragmentManager, "")
    }

    open fun onApiRetry(apiCode: String) {

    }


    fun setAppTheme() {
        lifecycleScope.launch {
            val themeValue: Int =
                PreferenceDataStore.getInt(Constants.APP_THEME) ?: ThemeConstant.BLUE
            val fontValue: Int =
                PreferenceDataStore.getInt(Constants.FONT_THEME) ?: FontConstant.IBM
            when (themeValue) {

                ThemeConstant.SEA -> {
                    val fontArray = listOf(
                        R.style.SeaTheme_Roboto,
                        R.style.SeaTheme,
                        R.style.SeaTheme_WorkSansTheme
                    )
                    fontArray[fontValue - 1]
                }
                ThemeConstant.BLACK -> {
                    val fontArray = listOf(
                        R.style.BlackTheme_Roboto,
                        R.style.BlackTheme,
                        R.style.BlackTheme_WorkSansTheme
                    )
                    fontArray[fontValue - 1]
                }
                ThemeConstant.WINE -> {
                    val fontArray = listOf(
                        R.style.WineTheme_Roboto,
                        R.style.WineTheme,
                        R.style.WineTheme_WorkSansTheme
                    )
                    fontArray[fontValue - 1]
                }
                else -> {
                    val fontArray = listOf(
                        R.style.AppTheme_Roboto,
                        R.style.AppTheme,
                        R.style.AppTheme_WorkSansTheme
                    )
                    fontArray[fontValue - 1]
                }
            }
//            if (Build.VERSION.SDK_INT >= 23) {
//                onApplyThemeResource(getTheme(), theme, false);
//            } else {
//                setTheme(theme);
//            }
        }
    }

    open fun setToolbar(
        title: String? = "",
        toolbarColor: Int? = R.attr.toolbarColor,
        showToolbar: Boolean = true,
        backIcon: Int = R.drawable.ic_arrow_left,
        showBackIcon: Boolean = true,
        subTitle: String? = "",
    ) {
        Log.d("main", "main")
    }

    fun changeAppLanguage() {
        showProgressBar()
        val languageCode = runBlocking {

            PreferenceDataStore.getString(Constants.LANGUAGE_THEME) ?: LanguageConstant.ENGLISH

        }
//        showToastShort(languageCode)
        val newLocale = Locale(languageCode)
        Locale.setDefault(newLocale)
        val tts: SpeechUtils by inject()
        tts.changeLanguage(newLocale)


        val resources: Resources = resources
        val configuration: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)
            configuration.setLocale(newLocale)

            configuration.setLocales(localeList)
        } else {
            configuration.setLocale(newLocale)
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            createConfigurationContext(configuration)
        } else {
            onConfigurationChanged(configuration)

        }


        hideProgressBar()

    }

    fun saveTheme(themeId: Int) {
        lifecycleScope.launch {
            withContext(lifecycleScope.coroutineContext) {
                PreferenceDataStore.saveInt(Constants.APP_THEME, themeId)
            }
            delay(1000)
            setAppTheme()
            delay(1000)
        }
    }

    fun checkAccessibilityService() {
        val isActive = getAccessibilityService()

        when {
            isActive -> {
                CommonAlertDialog.builder(this)
                    .title(getString(R.string.disable_screen_reading_mode))
                    .description(getString(R.string.disable_screen_reading_mode_desc))
                    .positiveBtnText(getString(R.string.disable))
                    .icon(null)
                    .getCallback {
                        if (it) {

                            try {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivityForResult(intent, RequestCode.ACCESSIBILITY)

                            } catch (e: ActivityNotFoundException) {
                                showToastLong(getString(R.string.you_dont_have_activity_to_perform_action))
                            }
                        }
                    }.build()


            }
            else -> {
                CommonAlertDialog.builder(this)
                    .title(getString(R.string.enable_screen_reading_mode))
                    .description(getString(R.string.enable_screen_reading_mode_desc))
                    .positiveBtnText(getString(R.string.enable))
                    .icon(null)
                    .getCallback {
                        if (it) {

                            try {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivityForResult(intent, RequestCode.ACCESSIBILITY)

                            } catch (e: ActivityNotFoundException) {
                                showToastLong(getString(R.string.you_dont_have_activity_to_perform_action))
                            }
                        }
                    }.build()
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtilClass.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        showLog("BASE_ACTIVITY", "onResponseSuccess>>> $apiCode")
        hideProgressBar()
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        hideProgressBar()
        handleOnException(isNetworkAvailable, exception, apiCode)
    }


    override fun onError(error: ToastData, apiCode: String?) {
        hideProgressBar()
        handleOnError(error)
    }


    override fun onLoading(message: String, apiCode: String?) {
        showProgressBar()
    }

    override fun onRetry(apiCode: String, networkError: Boolean, exception: ApiError) {
        showLog("BaseActivity", "onException: ${exception.message}  $apiCode ")

        retryHandling(apiCode, networkError, {
            onApiRetry(it)
        }, exception)

    }

    fun goToHomeActivity() {
        startActivity(
            Intent(
                this,
                HomeActivity::class.java
            )
        )
        finish()
    }

    fun goToModeratorActivity() {
        startActivity(
            Intent(
                this,
                ModeratorActivity::class.java
            )
        )
//        finish()
    }


    fun openRetryDialog(apiCode: String, networkError: Boolean) {
        hideProgressBar()
        NetworkFailDialogue().apply {
            setOnDialogClickListener(this@BaseActivity)
            arguments = bundleOf("apiCode" to apiCode, "networkError" to networkError)
        }.show(supportFragmentManager, "")
    }

    override fun onDialogClick(vararg items: Any) {
        onClickDialog?.onRetryDialogClick(items[0].toString())
    }


    fun onClickOfBaseDialog(onClickDialogue: IBaseDialogClick) {

        this.onClickDialog = onClickDialogue
    }

    interface IBaseDialogClick {
        fun onRetryDialogClick(apiCode: String)
    }

    fun handlePermissionDenied(perms: Array<String>, callBack: (Boolean) -> Unit = {}) {
        var value = true
        perms.forEach {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                value = shouldShowRequestPermissionRationale(it)
                if (!value) {
                    return@forEach
                }
            }
        }
        callBack(value)
        if (value) {

            showToastShort(getString(R.string.no_permission_accepted))
        } else {
            permissionDenied()
        }


    }

    private fun permissionDenied() {
        CommonAlertDialog.builder(this)
            .title(getString(R.string.alert))
            .description(getString(R.string.permission_denied))
            .positiveBtnText(getString(R.string.okay))
            .icon(null)
            .getCallback {
                if (it) {

                    try {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)

                    } catch (e: ActivityNotFoundException) {
                        showToastLong(getString(R.string.you_dont_have_activity_to_perform_action))
                    }
                }
            }.build()
    }


    private fun getRefreshToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("varun", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            token = task.result

        })
    }


    //hide keyboard on click outside the edittext
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        /* if (event.action == MotionEvent.ACTION_MOVE) {
             val imm = (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
             val isKeyboardUp = imm.isAcceptingText
             val v = currentFocus

             if (isKeyboardUp) {
                 imm.hideSoftInputFromWindow(v?.windowToken, 0)
             }
 //            if (v is ScrollView) {
 //                val outRect = Rect()
 //                v.getGlobalVisibleRect(outRect)
 //                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
 //                    v.clearFocus()
 //                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
 //                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
 //                }
 //            }
         }
       else */ if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    Log.d("focus", "touchEvent")
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    fun tokenFromDataStore(): String {
        var token = ""
        lifecycleScope.launch {
            token = PreferenceDataStore.getString(Constants.USER_TOKEN).toString()

        }

        return SelfLearningApplication.token ?: ""
    }

    fun guestUserPopUp() {
        CommonAlertDialog.builder(this)
            .title(getString(R.string.hey_guest))
            .description(getString(R.string.you_need_to_login_first))
            .positiveBtnText(getString(R.string.login))
            .negativeBtnText(getString(R.string.cancel))
            .notCancellable()
            .icon(R.drawable.ic_alert_title)
            .getCallback {
                if (it) {
                    goToInitialActivity()

                }
            }.build()
    }


    fun shareIntent(data: String) {

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, data)
        startActivity(Intent.createChooser(shareIntent, "Share via"))

    }

//    override fun onPaymentSuccess(razorpayPaymentId: String?) {
//        showLog(tag = "RAZORPAY", msg = "Payment onPaymentSuccess $razorpayPaymentId ")
//
//    }

//    override fun onPaymentError(errorCode: Int, response: String?) {
//        showLog(tag = "RAZORPAY", msg = "Payment onPaymentError $errorCode >>> $response")
//    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, p1: PaymentData?) {
        showLog(
            tag = "RAZORPAY",
            msg = "Payment onPaymentSuccess $razorpayPaymentId ... ${Gson().toJson(p1)} ... ${p1?.orderId}"
        )

        if (orderData == null) {
            orderData = OrderData()
        }

        orderData?.orderId = p1?.orderId
        orderData?.transactionId = p1?.paymentId
        onRazorpayCallback(true, orderData)
    }

    override fun onPaymentError(errorCode: Int, response: String?, p2: PaymentData?) {
        showLog(tag = "RAZORPAY", msg = "Payment onPaymentError $errorCode >>> $response ...?? $p2")
        onRazorpayCallback(false, orderData, response)
    }

    override fun onExternalWalletSelected(razorpayPaymentId: String?, p1: PaymentData?) {
        showLog(
            tag = "RAZORPAY",
            msg = "Payment onExternalWalletSelected $razorpayPaymentId ,.. $p1"
        )
    }


    open fun onRazorpayCallback(isSuccess: Boolean, data: OrderData?, response: String? = null) {

    }

    override fun orderDetail(orderData: OrderData?) {
        this.orderData = orderData
    }


    fun startRazorpayPayment(orderData: OrderData?) {
        if (!razorpayUtils.isInitialized())
            razorpayUtils.initRazorPay(this)

        razorpayUtils.startPayment(orderData)
    }
}