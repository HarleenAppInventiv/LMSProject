package com.selflearningcoursecreationapp.base

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.LocaleList
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
import com.selflearningcoursecreationapp.ui.splash.intro_slider.ActivityMessageListener
import com.selflearningcoursecreationapp.ui.splash.intro_slider.ActivityTrackSocketManager
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.concurrent.schedule


open class BaseActivity : AppCompatActivity(), LiveDataObserver, BaseDialog.IDialogClick,
    /*PaymentResultListener,*/ PaymentResultWithDataListener,
    ExternalWalletListener, RazorpayUtils.RazorPayCallback, ActivityMessageListener {


    private var failDialog: NetworkFailDialogue? = null
    private var progressDialog: ProgressDialog? = null
    private var onClickDialog: IBaseDialogClick? = null
    private var orderData: OrderData? = null
    val razorpayUtils = RazorpayUtils()
    private var timer: Timer? = null

    var token = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        razorpayUtils.initRazorPay(this)
        setTransparentLightStatusBar()
        changeAppLanguage()
        getRefreshToken()

//        val exceptionHandler by inject<ExceptionHandler>()
//        exceptionHandler.initialize(this)
//        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)

        setDayNightTheme()

//        transferObject.apply {
//            crashText = "D'oh! Its Crash.." // your error message "oops its crash" or something.
//            destinationActivity = HomeActivity::class.java //MUST BE UR STARTING ACTIVITY
//            detailsButonText =
//                "Details" // showing stacktrace. change your button's text what you want
//            restartAppButtonText =
//                "Contiune" // restart your app. change your button's text what you want
//            imagePath =
//                R.drawable.ic_logo_default // ur error image change what you want. MUST BE "R.drawable.example"
//            backgorundHex = "#ffffff" // ur crash activity's backgorund color.change what you want.
//            crashTextColor = "#000000" // CrashText's color. MUST BE HEX CODE
//
//        }

//        Thread.setDefaultUncaughtExceptionHandler(CosmosException(this,transferObject))


    }

    private fun sendDataToSocket() {


        ActivityTrackSocketManager.init(
            "${ApiEndPoints.WEB_SOCKET_USER_TRACKING}?Token=${tokenFromDataStore()}&LanguageId=${1}&ChannelId=2",
            this
        )

        ActivityTrackSocketManager.connect()
        timer?.cancel()
        timer = null
        timer = Timer()


        timer?.schedule(20000, 20000) {
            runOnUiThread {
                if (ActivityTrackSocketManager.isConnect() && isInternetAvailable()) {
                    ActivityTrackSocketManager.sendMessage("xzcvb")
                    showLog("websocket", "sendcalled")
                }
            }
        }


    }


    fun getAccessibilityService(): Boolean {
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


    fun showProgressBar(message: String? = null) {
        if (!isDestroyed) {
            try {


                if (progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
                progressDialog = null
                if (progressDialog == null) {
                    progressDialog = ProgressDialog(this, message)

                    progressDialog?.let { dialog ->
                        dialog.setCancelable(false)
                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    }
                }
                progressDialog?.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
        SelfLearningApplication.fontId = FontConstant.IBM
        lifecycleScope.launch {
            withContext(lifecycleScope.coroutineContext) {
                PreferenceDataStore.saveString(Constants.USER_TOKEN, "")
                PreferenceDataStore.saveInt(Constants.FONT_THEME, FontConstant.IBM)
                PreferenceDataStore.saveString(Constants.USER_RESPONSE, null)
            }
        }
        startActivity(Intent(this@BaseActivity, InitialActivity::class.java))
        finish()
    }

    fun showToastShort(message: String) {
        showLog("SHOW_TOAST", message)
        var toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        val view: View = LayoutInflater.from(this)
            .inflate(R.layout.custom_toast_layout, null)

        val tvMessage: LMSTextView = view.findViewById(R.id.tvMessage)
        tvMessage.text = message

        toast.view = view
        toast.show()
    }

    fun showToastLong(message: String) {
        showLog("SHOW_TOAST", message)
        var toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        val view: View = LayoutInflater.from(this)
            .inflate(R.layout.custom_toast_layout, null)

        val tvMessage: LMSTextView = view.findViewById(R.id.tvMessage)
        tvMessage.text = message

        toast.view = view
        toast.show()
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
        failDialog?.dismiss()
        failDialog = NetworkFailDialogue().apply {
            setOnDialogClickListener(object : BaseDialog.IDialogClick {
                override fun onDialogClick(vararg items: Any) {
                    var view = items[0] as Int
                    when (view) {
                        Constant.CLICK_VIEW -> {
                            callFragment(R.id.action_download, true)
                        }
                        else -> {
                            if (networkError && isInternetAvailable()) {
                                sendDataToSocket()
                            }
                            callBack(apiCode)
                        }
                    }
                }

            })
            arguments = bundleOf(
                "apiCode" to apiCode,
                "networkError" to networkError,
                "exception" to exception, "fromHome" to (this@BaseActivity is HomeActivity)
            )
        }
        failDialog?.show(supportFragmentManager, "")
    }

    open fun onApiRetry(apiCode: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imagePickUtils: ImagePickUtils by inject()
        imagePickUtils.onActivityResult(requestCode, resultCode, data)
    }

    fun setAppTheme() {
        lifecycleScope.launch {
            val fontValue: Int =
                SelfLearningApplication.fontId

            val fontArray = listOf(
                R.style.AppTheme_Roboto,
                R.style.AppTheme,
                R.style.AppTheme_WorkSansTheme
            )
            val theme = fontArray[fontValue - 1]

            if (Build.VERSION.SDK_INT >= 23) {
                onApplyThemeResource(getTheme(), theme, false)
            } else {
                setTheme(theme)
            }
        }
    }

    open fun setToolbar(
        title: String? = "",
        toolbarColor: Int? = R.attr.toolbarColor,
        showToolbar: Boolean = true,
        backIcon: Int = R.drawable.ic_arrow_left,
        showBackIcon: Boolean = true,
        subTitle: String? = ""
    ) {

    }

    fun changeAppLanguage() {
        showProgressBar()
        val languageCode = runBlocking {

            PreferenceDataStore.getString(Constants.LANGUAGE_THEME) ?: LanguageConstant.ENGLISH

        }
        val newLocale = Locale(languageCode)
        Locale.setDefault(newLocale)


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


    fun checkAccessibilityService() {
//        val isActive = getAccessibilityService()

        CommonAlertDialog.builder(this)
            .title(getString(R.string.screen_reading_mode))
            .spannedText(
                SpanUtils.with(this, getString(R.string.enable_screen_reading_mode_desc))
                    .startPos(80)
                    .textColor().isBold().getSpanString()
            ).positiveBtnText(getString(R.string.close_cap))
//            .negativeBtnText(getString(R.string.close))
            .setThemeIconColor(true)
            .hideNegativeBtn(true)
            .notCancellable()
            .icon(R.drawable.ic_visually_impaired)
            .getCallback {
//                        if (it) {
//
//                            try {
//                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                startActivityForResult(intent, RequestCode.ACCESSIBILITY)
//
//                            } catch (e: ActivityNotFoundException) {
//                                showToastLong(getString(R.string.you_dont_have_activity_to_perform_action))
//                            }
//                        }
            }.build()
//        when {
//            isActive -> {
//                CommonAlertDialog.builder(this)
//                    .title(getString(R.string.screen_reading_mode))
//                    .description(getString(R.string.disable_screen_reading_mode_desc))
//                    .positiveBtnText(getString(R.string.disable))
//                    .notCancellable()
//                    .setThemeIconColor(true)
//                    .hideNegativeBtn(true)
//                    .icon(R.drawable.ic_visually_impaired)
//                    .getCallback {
////                        if (it) {
////
////                            try {
////                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
////                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
////                                startActivityForResult(intent, RequestCode.ACCESSIBILITY)
////
////                            } catch (e: ActivityNotFoundException) {
////                                showToastLong(getString(R.string.you_dont_have_activity_to_perform_action))
////                            }
////                        }
//                    }.build()
//
//
//            }
//            else -> {
//
//
//                CommonAlertDialog.builder(this)
//                    .title(getString(R.string.screen_reading_mode))
//                    .spannedText(
//                        SpanUtils.with(this, getString(R.string.enable_screen_reading_mode_desc))
//                            .startPos(80)
//                            .textColor().isBold().getSpanString()
//                    ).positiveBtnText(getString(R.string.okay))
////                    .negativeBtnText(getString(R.string.close))
//                    .setThemeIconColor(true)
//                    .hideNegativeBtn(true)
//                    .notCancellable()
//                    .icon(R.drawable.ic_visually_impaired)
//                    .getCallback {
////                        if (it) {
////
////                            try {
////                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
////                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
////                                startActivityForResult(intent, RequestCode.ACCESSIBILITY)
////
////                            } catch (e: ActivityNotFoundException) {
////                                showToastLong(getString(R.string.you_dont_have_activity_to_perform_action))
////                            }
////                        }
//                    }.build()
//            }
//        }


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

    override fun onResume() {
        super.onResume()
        sendDataToSocket()


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
        val intent = Intent(
            this,
            HomeActivity::class.java
        )
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//        overridePendingTransition(0, 0)
        startActivity(
            intent
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
            arguments = bundleOf(
                "apiCode" to apiCode,
                "networkError" to networkError,
                "fromHome" to (this@BaseActivity is HomeActivity)
            )
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
                Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            token = task.result

        })
    }


    //hide keyboard on click outside the edittext
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
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
        return SelfLearningApplication.token ?: ""
    }


    fun guestUserPopUp() {
        CommonAlertDialog.builder(this)
            .title(getString(R.string.hey_guest))
            .description(getString(R.string.you_need_to_login_first))
            .positiveBtnText(getString(R.string.login))
            .negativeBtnText(getString(R.string.cancel))
            .notCancellable(false)
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
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))

    }

    override fun onPause() {
        super.onPause()
        timer?.cancel()
    }


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

    open fun callFragment(id: Int, isBottomFrag: Boolean) {

    }

    override fun orderDetail(orderData: OrderData?) {
        this.orderData = orderData
    }


    fun startRazorpayPayment(orderData: OrderData?) {
        if (!razorpayUtils.isInitialized())
            razorpayUtils.initRazorPay(this)

        razorpayUtils.startPayment(orderData)
    }

    fun setDayNightTheme() {

        hideProgressBar()

        if (SelfLearningApplication.isViOn == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }

    fun isViOn(): Boolean {
        return SelfLearningApplication.isViOn ?: false
    }

    open fun updateTheme() {
        progressDialog = null

    }

    fun showDefaultDialog(message: String) {


        CommonAlertDialog.builder(this)
            .hideNegativeBtn(true)
            .title(this.getString(R.string.coming_soon))
            .getCallback {

            }.icon(null)
            .build()
    }

    override fun onConnectSuccess() {

        showLog("websocket", "onConnectSuccess")
    }

    override fun onConnectFailed() {
        showLog("websocket", "onConnectFailed")
        if (!isDestroyed && !(tokenFromDataStore().isNullOrEmpty()) && isInternetAvailable()) {
            sendDataToSocket()
        }
    }

    override fun onClose() {
        showLog("websocket", "onClose")
    }

    override fun onMessage(text: String?) {
        showLog("websocket", "onMessage")
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityTrackSocketManager.close()
    }


    @SuppressLint("Range")
    fun downloadPdf(contentURL: String?, fileName: String) {
        PermissionUtilClass.builder(this).requestPermissions(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,

                    )
            } else {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        ).getCallBack { b, strings, _ ->
            if (b) {
                try {


                    val manager =
                        getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
                    val uri: Uri = Uri.parse(contentURL)
                    val request = DownloadManager.Request(uri)
                    request.setMimeType("application/pdf")
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        fileName
                    )
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                    val downloadID = manager?.enqueue(request) ?: 0
                    showToastShort(getString(R.string.download_start))

                    var finishDownload = false
                    var progress: Int
                    while (!finishDownload) {
                        val cursor: Cursor? =
                            manager?.query(DownloadManager.Query().setFilterById(downloadID))
                        if (cursor?.moveToFirst() == true) {
                            val status =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            when (status) {
                                DownloadManager.STATUS_FAILED -> {
                                    finishDownload = true
                                    showToastShort(getString(R.string.download_failed))

                                }
                                DownloadManager.STATUS_PAUSED -> {}
                                DownloadManager.STATUS_PENDING -> {}
                                DownloadManager.STATUS_RUNNING -> {
//                                val total =
//                                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
//                                if (total > 0) {
//                                    val downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
//                                    progress = (downloaded * 100L / total).toInt()
                                    // if you use downloadmanger in async task, here you can use like this to display progress.
                                    // Don't forget to do the division in long to get more digits rather than double.
                                    //  publishProgress((int) ((downloaded * 100L) / total));
//                                }
                                }
                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    progress = 100
                                    // if you use aysnc task
                                    // publishProgress(100);
                                    finishDownload = true

                                    showToastShort(getString(R.string.download_completed))
                                }
                            }
                        }
                        cursor?.close()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                handlePermissionDenied(strings)
            }


        }.build()

    }

    override fun onStop() {
        super.onStop()

        ActivityTrackSocketManager.close()
    }

}