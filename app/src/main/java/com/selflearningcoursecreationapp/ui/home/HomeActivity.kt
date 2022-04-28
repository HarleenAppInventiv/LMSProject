package com.selflearningcoursecreationapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationBarView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.databinding.ActivityHomeBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.preferences.PreferencesFragment

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
        navController?.addOnDestinationChangedListener { _, destination, args ->
            hideKeyboard()
            val hideToolbar = arrayListOf<Int>(
                R.id.profileThumbFragment,
                R.id.profileDetailsFragment,
                R.id.homeFragment,
                R.id.privacyFragment
            )
            val showCrossIcon = arrayListOf<Int>(R.id.homeCategoriesFragment)
            val subTitleArray = arrayListOf<Int>(R.id.popularFragment)
            val secondaryBgColor = arrayListOf(R.id.paymentDetailsFragment)
            if (hideToolbar.contains(destination.id)) {
                setToolbar(showToolbar = false)
            } else if (secondaryBgColor.contains(destination.id)) {
                setToolbar(
                    title = destination.label.toString(),
                    toolbarColor = R.attr.secondaryScreenBgColor,
                    showToolbar = true
                )
            } else if (showCrossIcon.contains(destination.id)) {
                setToolbar(
                    title = destination.label.toString(),
                    backIcon = R.drawable.ic_cross_grey,
                    showToolbar = true
                )
            } else if (subTitleArray.contains(destination.id)) {
                val subtitle =
                    if (args?.containsKey("subTitle") == true) args?.getString("subTitle") else ""
                setToolbar(
                    title = destination.label.toString(),
                    showToolbar = true,
                    subTitle = subtitle
                )
            } else {
                setToolbar(title = destination.label.toString(), showToolbar = true)
            }

            val bottomBarArray =
                arrayListOf<Int>(R.id.moreFragment, R.id.homeFragment, R.id.myCourseTabFragment)
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
        subTitle: String?
    ) {
        super.setToolbar(title, toolbarColor, showToolbar, backIcon, showBackIcon, subTitle)

        supportActionBar?.title = if (title.isNullOrEmpty()) " " else title
        if (showToolbar) {
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }

        if (!subTitle.isNullOrEmpty())
            binding.toolbar.subtitle = subTitle
        else binding.toolbar.subtitle = ""
        try {


            binding.toolbar.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    getAttrColor(toolbarColor ?: R.attr.toolbarColor)
                )
            )
        } catch (e: UninitializedPropertyAccessException) {
            showException(e)
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
        val destArrayList = listOf<Int>(R.id.homeFragment)
        val bottomArray = listOf<Int>(R.id.myCourseTabFragment, R.id.moreFragment)
        if (destArrayList.contains(navController?.currentDestination?.id)) {
            finishAffinity()
        } else if (bottomArray.contains(navController?.currentDestination?.id)) {
            setSelected(R.id.action_home)
        } else if (navController?.currentDestination?.id == R.id.preferencesFragment) {
            (getCurrentFragment() as PreferencesFragment).onClickBack()
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

    fun getCurrentFragment(): Fragment {
        val navFrag = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        return navFrag!!.childFragmentManager.fragments[0]
    }

}