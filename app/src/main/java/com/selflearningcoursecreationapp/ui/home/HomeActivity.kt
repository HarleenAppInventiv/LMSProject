package com.selflearningcoursecreationapp.ui.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationBarView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.databinding.ActivityHomeBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseBaseFragment
import com.selflearningcoursecreationapp.ui.preferences.PreferencesFragment
import com.selflearningcoursecreationapp.utils.*
import org.koin.android.ext.android.inject


class HomeActivity : BaseActivity(), NavigationBarView.OnItemSelectedListener {
    private var navController: NavController? = null
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeActivityViewModel by inject()
    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_NOTIFICATION_BROADCAST -> {
                    openInvitePopUp(intent.extras)
                }
            }

        }
    }

    private fun openInvitePopUp(notifyBundle: Bundle?) {
        val courseId = notifyBundle?.getString("courseId").toString()


        CommonAlertDialog.builder(this)
            .description(
                "You have invitation for “${
                    notifyBundle?.getString("courseName").toString()
                }”."
            )
            .title(getString(R.string.co_author_invitation))
            .positiveBtnText(getString(R.string.accept))
            .negativeBtnText(getString(R.string.reject))
            .notCancellable()
            .setThemeIconColor(true)
            .setPositiveButtonTheme(R.color.accent_color_2FBF71, R.color.white)
            .setNegativeButtonTheme(R.color.accent_color_fc6d5b, R.color.white)
            .icon(R.drawable.ic_co_author_icon)
            .getCallback {
                if (it) {
                    viewModel.manageCoAuthorInvitation(courseId, CoAuthorStatus.ACCEPT)

                } else {
                    viewModel.manageCoAuthorInvitation(courseId, CoAuthorStatus.REJECT)

                }
            }.build()
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel.getApiResponse().observe(this, this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initUi()

        intent.let {
            if (it.hasExtra("notificationBundle")) {
                val notifyBundle = it.getBundleExtra("notificationBundle")
                openInvitePopUp(notifyBundle)

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

        binding.fabAdd.setOnClickListener {
            if (tokenFromDataStore() == "") guestUserPopUp() else navController?.navigate(R.id.addCourseBaseFragment)
        }

        observeCourseData()
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
            val hideToolbar = arrayListOf(
                R.id.profileThumbFragment,
                R.id.profileDetailsFragment,
                R.id.courseDetailsFragment,
                R.id.homeFragment,
                R.id.quizBaseFragment,
                R.id.privacyFragment
            )
            val showCrossIcon =
                arrayListOf<Int>()
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

            val hideElevation = arrayListOf<Int>(R.id.addEmailFragment, R.id.paymentDetailsFragment)
            hideTBElevation(hideElevation.contains(destination.id))

            val bottomBarArray =
                arrayListOf(R.id.moreFragment, R.id.homeFragment, R.id.myCourseTabFragment)
            bottomBarVisibility(bottomBarArray.contains(destination.id))
            if (bottomBarArray.contains(destination.id)) {


                setToolbar(
                    title = destination.label.toString(),
                    showToolbar = true,
                    showBackIcon = (destination.id == R.id.myCourseTabFragment)
                )
            }
        }
    }

    private fun hideTBElevation(hideElevation: Boolean) {
        binding.toolbar.elevation = if (hideElevation) 0f else 2f
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
                    getAttrColor(
                        toolbarColor ?: R.attr.toolbarColor
                    )
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
        val destArrayList = listOf(R.id.homeFragment)
        val bottomArray =
            listOf(R.id.myCourseTabFragment, R.id.moreFragment, R.id.paymentDetailsFragment)
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
                if (tokenFromDataStore() == "") {
                    guestUserPopUp()
                } else {
                    navController?.navigate(R.id.moreFragment)
                    return true
                }
            }
            R.id.action_course -> {
                if (tokenFromDataStore() == "") {
                    guestUserPopUp()
                } else {
                    navController?.navigate(R.id.myCourseTabFragment)
                    return true
                }
            }
//            R.id.action_home -> {
//                navController?.navigate(R.id.profileThumbFragment)
//                return true
//            }
            R.id.action_home -> {
                if (tokenFromDataStore() == "") {
                } else {
                    navController?.navigate(R.id.homeFragment)
                }
            }
//            R.id.action_add -> {
//                navController?.navigate(R.id.addCourseBaseFragment)
//
//            }
            else -> {
//                if (tokenFromDataStore() == "") {
//                    guestUserPopUp()
//                } else {
//                    navController?.navigate(R.id.moderatorBaseFragment)
//                }
                return false
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

    override fun onRazorpayCallback(isSuccess: Boolean, data: OrderData?, response: String?) {
        super.onRazorpayCallback(isSuccess, data, response)
        if (isSuccess) viewModel.purchaseCourse(data?.courseId, CourseType.PAID)

    }

    private fun observeCourseData() {
        viewModel.purchaseCourseLiveData.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                hideProgressBar()
                navController?.navigate(R.id.paymentDetailsFragment, bundleOf("orderData" to it))
            }

        })
    }
}
