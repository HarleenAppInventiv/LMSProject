package com.selflearningcoursecreationapp.ui.moderator

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationBarView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.databinding.ActivityModeratorBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseBaseFragment
import com.selflearningcoursecreationapp.ui.home.HomeActivityViewModel
import com.selflearningcoursecreationapp.ui.preferences.PreferencesFragment
import com.selflearningcoursecreationapp.utils.ACTION_NOTIFICATION_BROADCAST
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CoAuthorStatus
import org.koin.androidx.viewmodel.ext.android.viewModel


class ModeratorActivity : BaseActivity(), NavigationBarView.OnItemSelectedListener {
    private var navController: NavController? = null
    private lateinit var binding: ActivityModeratorBinding
    private val viewModel: HomeActivityViewModel by viewModel()
    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_NOTIFICATION_BROADCAST -> {
                }
            }

        }
    }


    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(ACTION_NOTIFICATION_BROADCAST)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        changeAppLanguage()
        super.onCreate(savedInstanceState)
        setAppTheme()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_moderator)
        viewModel.getApiResponse().observe(this, this)
        initUi()

        intent.let {
            if (it.hasExtra("notificationBundle")) {
                val notifyBundle = it.getBundleExtra("notificationBundle")

            }
        }


    }


    private fun initUi() {
        initToolbar()
        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment?
        navController = navHostFrag?.navController
        setDestinationChangeListener()
        setBottomBar()
        setSelected(R.id.action_home)
    }

    private fun setBottomBar() {
        binding.bottomNavigationView.setNavTint()
        binding.bottomNavigationView.setOnItemSelectedListener(this)
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setDestinationChangeListener() {
        navController?.addOnDestinationChangedListener { _, destination, args ->
            hideKeyboard()
            val hideToolbar = arrayListOf(
                R.id.profileThumbFragment,
                R.id.moderatorBaseFragment,
                R.id.profileDetailsFragment,
                R.id.courseDetailsFragment,
                R.id.homeFragment,
                R.id.modCourseDetailsFragment,
                R.id.quizBaseFragment,
                R.id.privacyFragment
            )
            val showCrossIcon =
                arrayListOf(R.id.homeCategoriesFragment)
            val subTitleArray = arrayListOf(R.id.popularFragment)
            val secondaryBgColor = arrayListOf(R.id.paymentDetailsFragment)
            when {
                hideToolbar.contains(destination.id) -> {
                    setToolbar(showToolbar = false)
                }
                secondaryBgColor.contains(destination.id) -> {
                    setToolbar(
                        title = destination.label.toString(),
                        toolbarColor = R.attr.secondaryScreenBgColor,
                        showToolbar = true
                    )
                }
                showCrossIcon.contains(destination.id) -> {
                    setToolbar(
                        title = destination.label.toString(),
                        backIcon = R.drawable.ic_cross_grey,
                        showToolbar = true
                    )
                }
                subTitleArray.contains(destination.id) -> {
                    val subtitle =
                        if (args?.containsKey("subTitle") == true) args.getString("subTitle") else ""
                    val title =
                        if (args?.containsKey("title") == true) args.getString("title") else destination.label.toString()
                    setToolbar(
                        title = title,
                        showToolbar = true,
                        subTitle = subtitle
                    )
                }
                else -> {
                    setToolbar(title = destination.label.toString(), showToolbar = true)
                }
            }

            val bottomBarArray =
                arrayListOf(
                    R.id.settingsFragment,
                    R.id.moderatorBaseFragment,
                    R.id.myCourseTabFragment
                )
            bottomBarVisibility(bottomBarArray.contains(destination.id))
            if (bottomBarArray.contains(destination.id)) {


                setToolbar(
                    title = destination.label.toString(),
                    showToolbar = (destination.id != R.id.modCourseDetailsFragment),
                    showBackIcon = (destination.id == R.id.myCourseTabFragment)
                )
            }
        }
    }

    @SuppressLint("ResourceType")
    fun bottomBarVisibility(isShow: Boolean) {
        binding.bottomNavigationView.visibleView(isShow)
//        (binding.parentCL.layoutParams as CoordinatorLayout.LayoutParams).apply {
//            if (isShow)
//                setMargins(0, 0, 0, resources.getDimensionPixelOffset(R.dimen._40sdp))
//            else {
//                setMargins(0, 0, 0, 0)
//
//            }
//        }

    }

    override fun setToolbar(
        title: String?,
        toolbarColor: Int?,
        showToolbar: Boolean,

        backIcon: Int,
        showBackIcon: Boolean,
        subTitle: String?,
    ) {
        super.setToolbar(title, toolbarColor, showToolbar, backIcon, showBackIcon, subTitle)

        supportActionBar?.title = if (title.isNullOrEmpty()) " " else title
        if (showToolbar) {
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }

        if (!subTitle.isNullOrEmpty()) {
            binding.toolbar.subtitle = subTitle
            binding.toolbar.layoutParams?.apply {
                height = resources.getDimensionPixelOffset(R.dimen._50sdp)
            }
        } else {
            binding.toolbar.subtitle = ""
            binding.toolbar.layoutParams?.apply {
                height = resources.getDimensionPixelOffset(R.dimen._40sdp)
            }
        }
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
        val destArrayList = listOf(R.id.moderatorBaseFragment)
        val bottomArray = listOf(R.id.settingsFragment)
        when {
            destArrayList.contains(navController?.currentDestination?.id) -> {
                finishAffinity()
            }
            bottomArray.contains(navController?.currentDestination?.id) -> {
                setSelected(R.id.action_home)
            }
            navController?.currentDestination?.id == R.id.preferencesFragment -> {
                (getCurrentFragment() as PreferencesFragment).onClickBack()
            }
            navController?.currentDestination?.id == R.id.addCourseBaseFragment -> {
                (getCurrentFragment() as AddCourseBaseFragment).onClickBack()
            }
            else -> {
                navController?.popBackStack()
            }
        }
    }

    fun setSelected(itemId: Int) {
        binding.bottomNavigationView.selectedItemId = itemId
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_more -> {

                navController?.navigate(R.id.setting_graph)
                return true

            }

            R.id.action_home -> {

                navController?.navigate(R.id.moderatorBaseFragment)

            }

        }
        return true
    }

    private fun getCurrentFragment(): Fragment {
        val navFrag = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        return navFrag!!.childFragmentManager.fragments[0]
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_COAUTHOR_INVITATION + "/home" -> {
                val type = value as Pair<String, Int>
                when (type.second) {
                    CoAuthorStatus.ACCEPT -> {
                        navController?.navigate(
                            R.id.addCourseBaseFragment,
                            bundleOf("courseId" to type.first.toInt())
                        )
                    }
                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        super.onApiRetry(apiCode)
        viewModel.onApiRetry(apiCode)
    }
}
