package com.selflearningcoursecreationapp.ui.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationBarView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.databinding.ActivityHomeBinding

import com.selflearningcoursecreationapp.extensions.hideKeyboard
import com.selflearningcoursecreationapp.extensions.setNavTint
import com.selflearningcoursecreationapp.extensions.setThemeTint
import com.selflearningcoursecreationapp.extensions.visibleView

class HomeActivity : BaseActivity(), NavigationBarView.OnItemSelectedListener {
    private var navController: NavController? = null
    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        changeAppLanguage()
        super.onCreate(savedInstanceState)
        setAppTheme()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        //showToastShort(PrefrenceDataStore)
        initUi()
    }


    private fun initUi() {
        initToolbar()
        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment?
        navController = navHostFrag?.navController
        setDestinationChangeListener()
        setBottomBar()
        setSelected(R.id.action_home)

        binding.fabAdd.setOnClickListener {
            navController?.navigate(R.id.addCourseBaseFragment)
        }
    }

    private fun setBottomBar() {
        binding.bottomNavigationView.setNavTint()
        binding.fabAdd.setThemeTint()
        binding.bottomNavigationView.setOnItemSelectedListener(this)
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setDestinationChangeListener() {
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            hideKeyboard()
            val hideToolbar =
                arrayListOf<Int>(R.id.profileThumbFragment,
                    R.id.profileDetailsFragment,
                    R.id.homeFragment)
            if (hideToolbar.contains(destination.id)) {
                setToolbar(showToolbar = false)
            } else {
                setToolbar(title = destination.label.toString(), showToolbar = true)
            }

            val bottomBarArray =
                arrayListOf<Int>(R.id.moreFragment, R.id.homeFragment,R.id.myCourseTabFragment)
            bottomBarVisibility(bottomBarArray.contains(destination.id))
            if (bottomBarArray.contains(destination.id)) {


                setToolbar(
                    title = destination.label.toString(),
                    showToolbar = true,
                    showBackIcon = false
                )
            }
        }
    }

    @SuppressLint("ResourceType")
    fun bottomBarVisibility(isShow: Boolean) {
        binding.bottomAppBar.visibleView(isShow)
        binding.fabAdd.visibleView(isShow)
        (binding.parentCL.layoutParams as CoordinatorLayout.LayoutParams).apply {
            if (isShow)
                setMargins(0, 0, 0, resources.getDimensionPixelOffset(R.dimen._40sdp))
            else {
                setMargins(0, 0, 0, 0)

            }
        }

    }

    override fun setToolbar(
        title: String?,
        toolbarColor: Int?,
        showToolbar: Boolean,
        backIcon: Int,
        showBackIcon: Boolean,
    ) {
        super.setToolbar(title, toolbarColor, showToolbar, backIcon, showBackIcon)

        supportActionBar?.title = if (title.isNullOrEmpty()) " " else title
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
        val destArrayList = listOf<Int>(R.id.sliderFragment, R.id.homeFragment)
        val bottomArray = listOf<Int>(R.id.profileThumbFragment)
        if (destArrayList.contains(navController?.currentDestination?.id)) {
            finishAffinity()
        } else if (bottomArray.contains(navController?.currentDestination?.id)) {
            setSelected(R.id.action_more)
        } else {
            navController?.popBackStack()
        }
    }

    fun setSelected(itemId: Int) {
        binding.bottomNavigationView.selectedItemId = itemId
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_more -> {
                navController?.navigate(R.id.moreFragment)
                return true
            }
            R.id.action_course -> {
                navController?.navigate(R.id.myCourseTabFragment)
                return true
            }
//            R.id.action_home -> {
//                navController?.navigate(R.id.profileThumbFragment)
//                return true
//            }
            R.id.action_home -> {
                navController?.navigate(R.id.homeFragment)
            }
//            R.id.action_add -> {
//                navController?.navigate(R.id.addCourseBaseFragment)
//
//            }
            else -> {
                showToastShort("not implemented")
                return false
            }
        }
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }


}