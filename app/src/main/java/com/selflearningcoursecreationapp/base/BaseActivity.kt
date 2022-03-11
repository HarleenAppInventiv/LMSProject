package com.selflearningcoursecreationapp.base

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.provider.Settings
import android.text.style.LocaleSpan
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.extensions.setTransparentLightStatusBar
import com.selflearningcoursecreationapp.extensions.showAlertDialog
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.models.AppThemeFile
import com.selflearningcoursecreationapp.ui.dialog.ProgressDialog
import com.selflearningcoursecreationapp.utils.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import java.util.*

open class BaseActivity : AppCompatActivity() {
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setTransparentLightStatusBar()
        changeAppLanguage()
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

        if (accessibilityEnabled == 1) {
            Log.v(TAG.ACCESSIBILITY, "Accessibility Is Enabled")
            return true
        } else {
            return false
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


    fun handleOnException(networkAvailable: Boolean, exception: ApiError, apiCode: String) {
        if (isDestroyed || isFinishing)
            return
        exception.message?.let {
            showToastShort(it)
        }

    }

    fun showToastShort(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showToastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun handleOnError(error: ToastData) {
        error.errorCode?.let {
            showToastShort(getString(it))
        }
        error.errorString?.let {
            showToastShort(it)
        }

    }


    fun setAppTheme() {
        lifecycleScope.launch {
            var themeValue: Int =
                PreferenceDataStore.getInt(Constants.APP_THEME) ?: THEME_CONSTANT.BLUE
            var fontValue: Int =
                PreferenceDataStore.getInt(Constants.FONT_THEME) ?: FONT_CONSTANT.IBM
            when (themeValue) {

                THEME_CONSTANT.SEA -> {
                    val fontArray = listOf(
                        R.style.SeaTheme_Roboto,
                        R.style.SeaTheme,
                        R.style.SeaTheme_WorkSansTheme
                    )
                    fontArray[fontValue - 1]
                }
                THEME_CONSTANT.BLACK -> {
                    val fontArray = listOf(
                        R.style.BlackTheme_Roboto,
                        R.style.BlackTheme,
                        R.style.BlackTheme_WorkSansTheme
                    )
                    fontArray[fontValue - 1]
                }
                THEME_CONSTANT.WINE -> {
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
    ) {
        Log.d("main", "main")
    }

    var localeSpan: LocaleSpan? = null
    var languageCode: String? = null
    fun changeAppLanguage() {
        showProgressBar()
        languageCode = runBlocking {

            PreferenceDataStore.getString(Constants.LANGUAGE_THEME) ?: LANGUAGE_CONSTANT.ENGLISH

        }
//        showToastShort(languageCode)
        val newLocale = Locale(languageCode)
        Locale.setDefault(newLocale)
        val tts: SpeechUtils by inject()
        tts.changeLanguage(newLocale)

        localeSpan = LocaleSpan(newLocale)

        val resources: Resources = resources
        val configuration: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)
            configuration.setLocale(newLocale)

            configuration.setLocales(localeList)
        } else {
            configuration.locale = newLocale
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
            lifecycleScope.async {
                PreferenceDataStore.saveInt(Constants.APP_THEME, themeId)
            }.await()
            delay(1000)
            setAppTheme()
            delay(1000)
        }
    }

    fun checkAccessibilityService() {
        val isActive = getAccessibilityService()

        when {
            isActive ->
                showAlertDialog(
                    title = getString(R.string.disable_screen_reading_mode),
                    description = getString(R.string.disable_screen_reading_mode_desc),
                    buttonText = getString(R.string.disable),
                    negativeButtonText = getString(R.string.cancel),
                    icon = null, onClick = {
                        if (it) {

                            try {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivityForResult(intent, REQUEST_CODE.ACCESSIBILITY)

                            } catch (e: ActivityNotFoundException) {
                                showToastLong("You don't have activity to perform this action")
                            }
                        }

                    })

            else ->

                showAlertDialog(title = getString(R.string.enable_screen_reading_mode),
                    description = getString(R.string.enable_screen_reading_mode_desc),
                    buttonText = getString(R.string.enable),
                    negativeButtonText = getString(R.string.cancel),
                    icon = null, onClick = {
                        if (it) {

                            try {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivityForResult(intent, REQUEST_CODE.ACCESSIBILITY)

                            } catch (e: ActivityNotFoundException) {
                                showToastLong("You don't have activity to perform this action")
                            }
                        }

                    })
//


        }


    }

    fun getThemeFile(colorString: String): AppThemeFile {
        val myTheme = AppThemeFile()
        myTheme.themeColor = colorString
        myTheme.btnTextColor = colorString
        val themeColor = Color.parseColor(colorString)

        var red = (Color.red(themeColor))
        if (red >= 100) {
            red -= 10
        } else {
            red += 10
        }


        var green = (Color.green(themeColor))
        if (green >= 100) {
            green -= 10
        } else {
            green += 10
        }


        var blue = (Color.blue(themeColor))
        if (blue >= 100) {
            blue -= 10
        } else {
            blue += 10
        }




        showLog(
            "COLOR_HEX",
            String.format(
                "#%02x%02x%02x%02x",
                200,
                red,
                green,

                blue
            )
        )
        myTheme.themeTint = String.format(
            "#%02x%02x%02x%02x",
            200,
            red,
            green,
            blue
        )
        return myTheme
    }

    fun saveThemeFile(themeFile: AppThemeFile) {
        lifecycleScope.launch {
            PreferenceDataStore.saveString(Constants.THEME_FILE, Gson().toJson(themeFile))
        }
    }

}