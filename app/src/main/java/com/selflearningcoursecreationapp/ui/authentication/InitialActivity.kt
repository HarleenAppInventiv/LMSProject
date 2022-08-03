package com.selflearningcoursecreationapp.ui.authentication

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.databinding.ActivityOnBoadingBinding
import com.selflearningcoursecreationapp.extensions.hideKeyboard
import com.selflearningcoursecreationapp.extensions.setTransparentLightStatusBar
import com.selflearningcoursecreationapp.ui.authentication.login_signup.LoginSignUpFragment
import com.selflearningcoursecreationapp.ui.preferences.PreferencesFragment

class InitialActivity : BaseActivity() {
    private var navController: NavController? = null
    private lateinit var binding: ActivityOnBoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransparentLightStatusBar()
        setAppTheme()
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_on_boading
        )
        initUi()

    }

    private fun initUi() {
        Log.e("onDestroy", "Init Called")
        initToolbar()
        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment?
        navController = navHostFrag?.navController
        setDestinationChangeListener()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setDestinationChangeListener() {
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            hideKeyboard()
            val hideToolbar = arrayListOf(
                R.id.sliderFragment,
                R.id.privacyFragment,
                R.id.loginSignUpFragment
            )
            if (hideToolbar.contains(destination.id)) {
                setToolbar(showToolbar = false)
            } else {
                setToolbar(title = destination.label?.toString(), showToolbar = true)
            }
        }
    }

    override fun setToolbar(
        title: String?,
        toolbarColor: Int?,
        showToolbar: Boolean,
        backIcon: Int,
        showBackIcon: Boolean,
        subTitle: String?
    ) {
        super.setToolbar(title, toolbarColor, showToolbar, backIcon, showBackIcon, subTitle)

        supportActionBar?.title = if (title.isNullOrEmpty()) " " else title
        if (showToolbar) {
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }
        supportActionBar?.setHomeAsUpIndicator(backIcon)
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackIcon)
        if (showBackIcon) {
            binding.toolbar.setContentInsetsRelative(
                0,
                resources.getDimensionPixelOffset(R.dimen._15sdp)
            )

        } else {
            binding.toolbar.setContentInsetsRelative(
                resources.getDimensionPixelOffset(R.dimen._15sdp),
                resources.getDimensionPixelOffset(R.dimen._15sdp)
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        hideKeyboard()
        val destArrayList = listOf(R.id.sliderFragment)
        when {
            destArrayList.contains(navController?.currentDestination?.id) -> {
                finishAffinity()
            }
            navController?.currentDestination?.id == R.id.loginSignUpFragment -> {
                (getCurrentFragment() as LoginSignUpFragment).onClickBack()
            }
            navController?.currentDestination?.id == R.id.preferencesFragment -> {
                (getCurrentFragment() as PreferencesFragment).onClickBack()
            }
            else -> {
                navController?.popBackStack()
            }
        }
    }

    private fun getCurrentFragment(): Fragment {
        val navFrag = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        return navFrag!!.childFragmentManager.fragments[0]
    }
}