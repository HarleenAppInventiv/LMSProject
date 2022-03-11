package com.selflearningcoursecreationapp.ui.authentication

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.databinding.ActivityOnBoadingBinding
import com.selflearningcoursecreationapp.extensions.hideKeyboard
import com.selflearningcoursecreationapp.extensions.setTransparentLightStatusBar

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
        navController?.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {

                hideKeyboard()
                val hideToolbar = arrayListOf<Int>(R.id.sliderFragment,R.id.loginSignUpFragment)
                if (hideToolbar.contains(destination.id)) {
                    setToolbar(showToolbar = false)
                } else {
                    setToolbar(title = destination.label?.toString(), showToolbar = true)
                }

            }
        })
    }

    override fun setToolbar(
        title: String?,
        toolbarColor: Int?,
        showToolbar: Boolean,
        backIcon: Int,
        showBackIcon: Boolean
    ) {
        super.setToolbar(title, toolbarColor, showToolbar, backIcon, showBackIcon)

        supportActionBar?.title=if (title.isNullOrEmpty()) " " else title
        if (showToolbar) {
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }
        supportActionBar?.setHomeAsUpIndicator(backIcon)
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackIcon)
           
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
        val destArrayList = listOf<Int>(R.id.sliderFragment, R.id.loginSignUpFragment)
        if (destArrayList.contains(navController?.currentDestination?.id)) {
            finishAffinity()
        } else {
            navController?.popBackStack()
        }
    }
}