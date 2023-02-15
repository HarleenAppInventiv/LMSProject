package com.selflearningcoursecreationapp.ui.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.messaging.FirebaseMessaging
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseActivity
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.databinding.ActivityHomeBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.ui.bottom_course.MyCourseTabFragment
import com.selflearningcoursecreationapp.ui.bottom_home.CreateCourseAcceptTermsDialog
import com.selflearningcoursecreationapp.ui.bottom_home.HomeFragment
import com.selflearningcoursecreationapp.ui.bottom_home.HomeVM
import com.selflearningcoursecreationapp.ui.bottom_home.downloaded.DownloadedLectureViewFragment
import com.selflearningcoursecreationapp.ui.content_creator.course_detail.ContentCourseDetailFragment
import com.selflearningcoursecreationapp.ui.course_details.doc.PdfViewerFragment
import com.selflearningcoursecreationapp.ui.course_details.quiz.QuizBaseFragment
import com.selflearningcoursecreationapp.ui.course_details.quiz.QuizReportFragment
import com.selflearningcoursecreationapp.ui.course_details.video.VideoBaseFragment
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseBaseNewFragment
import com.selflearningcoursecreationapp.ui.create_course.quiz.AddQuizFragment
import com.selflearningcoursecreationapp.ui.create_course.upload_content.audio_as_lesson.AudioLectureFragment
import com.selflearningcoursecreationapp.ui.create_course.upload_content.audio_as_lesson.RecordAudioFragment
import com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_as_lecture.DocLessonFragment
import com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_text.LessonTextFragment
import com.selflearningcoursecreationapp.ui.create_course.upload_content.editVideo.EditAudioFragment
import com.selflearningcoursecreationapp.ui.create_course.upload_content.video_as_lecture.VideoLectureFragment
import com.selflearningcoursecreationapp.ui.preferences.PreferencesFragment
import com.selflearningcoursecreationapp.ui.profile.profileThumb.ProfileThumbFragment
import com.selflearningcoursecreationapp.ui.profile.requestTracker.RequestTrackerDashboardFragment
import com.selflearningcoursecreationapp.ui.profile.requestTracker.coAuthorRequest.CoAuthorRequestFragment
import com.selflearningcoursecreationapp.ui.profile.requestTracker.moderatorComments.ModeratorsCommentFragment
import com.selflearningcoursecreationapp.ui.profile.requestTracker.sentRequests.SentRequestFragment
import com.selflearningcoursecreationapp.ui.profile.reward.RewardFragment
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeActivity : BaseActivity(), NavigationBarView.OnItemSelectedListener {
    private var navController: NavController? = null
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeActivityViewModel by viewModel()
    private var bottomBundle: Bundle? = null
    private val sharedHomeVM: HomeVM by viewModel()
    private var doubleBackToExitPressedOnce = false


    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_NOTIFICATION_BROADCAST -> {
                    handleBroadcastNotification(intent.extras)
                }
            }
        }
    }

    private fun handleBroadcastNotification(extras: Bundle?) {
//        showToastShort(extras.toString())
        val type = extras?.getString("type").toString()
        when (type) {

            NotificationType.COAUTHOR_REQUEST -> {
                if (getCurrentFragment() is CoAuthorRequestFragment) {
                    (getCurrentFragment() as CoAuthorRequestFragment?)?.refreshData()
                } else {
                    openInvitePopUp(extras)
                }
            }
            NotificationType.COAUTHOR_COURSE_SUBMIT -> {
                if (getCurrentFragment() is AddCourseBaseNewFragment) {
                    (getCurrentFragment() as AddCourseBaseNewFragment?)?.refreshData()
                } else if (getCurrentFragment() is SentRequestFragment) {
                    (getCurrentFragment() as SentRequestFragment?)?.refreshData()
                } else if (getCurrentFragment() is RequestTrackerDashboardFragment) {
                    (getCurrentFragment() as RequestTrackerDashboardFragment?)?.refreshData()

                }
            }
            NotificationType.COAUTHOR_ACCEPT_REQUEST, NotificationType.COAUTHOR_REJECT_REQUEST -> {
                if (getCurrentFragment() is SentRequestFragment) {
                    (getCurrentFragment() as SentRequestFragment?)?.refreshData()
                } else if (getCurrentFragment() is RequestTrackerDashboardFragment) {
                    (getCurrentFragment() as RequestTrackerDashboardFragment?)?.refreshData()

                }
            }
            NotificationType.COURSE_PUBLISHED -> {
                FirebaseMessaging.getInstance().subscribeToTopic("uat_all_loggedin") //for UAT
//              FirebaseMessaging.getInstance().subscribeToTopic("production_all_loggedin") //for Production
                viewModel.getUserData().apply {
                    viewModel.userProfile?.roles?.forEach {
                        Log.d("varun", "onReceive: ${it.topicName}")
                        FirebaseMessaging.getInstance().subscribeToTopic(it.topicName.toString())
                    }
                }
                if (getCurrentFragment() is ContentCourseDetailFragment) {
                    (getCurrentFragment() as ContentCourseDetailFragment?)?.refreshData(false)
                } /*else if (getCurrentFragment() is MyCourseTabFragment) {
                    (getCurrentFragment() as MyCourseTabFragment?)?.refreshData()
                }*/

            }

            NotificationType.COURSE_SUBMITTED -> {
                if (getCurrentFragment() is ContentCourseDetailFragment) {
                    (getCurrentFragment() as ContentCourseDetailFragment?)?.refreshData(false)
                } else if (getCurrentFragment() is MyCourseTabFragment) {
                    (getCurrentFragment() as MyCourseTabFragment?)?.refreshData()
                }


            }
            NotificationType.COURSE_REJECTED -> {
                if (getCurrentFragment() is ContentCourseDetailFragment) {
                    (getCurrentFragment() as ContentCourseDetailFragment?)?.refreshData(false)
                } else if (getCurrentFragment() is ModeratorsCommentFragment) {
                    (getCurrentFragment() as ModeratorsCommentFragment?)?.refreshData()
                } else if (getCurrentFragment() is MyCourseTabFragment) {
                    (getCurrentFragment() as MyCourseTabFragment?)?.refreshData()
                }


            }
            NotificationType.MODERATOR_REQUEST_APPROVED, NotificationType.MODERATOR_REQUEST_REJECTED, NotificationType.AS_MODERATOR_BLOCKED -> {
                if (getCurrentFragment() is RequestTrackerDashboardFragment) {
                    (getCurrentFragment() as RequestTrackerDashboardFragment?)?.refreshData()

                }
            }
            NotificationType.REVIEW_ADDED -> {
                if (getCurrentFragment() is ContentCourseDetailFragment) {
                    (getCurrentFragment() as ContentCourseDetailFragment?)?.refreshData(true)
                }

            }
            NotificationType.REWARDS_EARNED -> {
                if (getCurrentFragment() is RewardFragment) {
                    (getCurrentFragment() as RewardFragment?)?.refreshData()
                }


            }


        }
    }

    private fun openInvitePopUp(notifyBundle: Bundle?) {
        val courseId = notifyBundle?.getString("courseId").toString()

        CommonAlertDialog.builder(this)
            .description(
                notifyBundle?.getString("body").toString()
            )
            .title(getString(R.string.co_author_invitation))
            .positiveBtnText(getString(R.string.accept))
            .negativeBtnText(getString(R.string.reject))
            .notCancellable(false)
            .getCloseButtonCallback(true)
            .setThemeIconColor(true)


            .setPositiveButtonTheme(
                if (ThemeUtils.isViOn()) R.color.ViSecondaryColor else R.color.accent_color_2FBF71,
                if (ThemeUtils.isViOn()) R.color.ViMainColor else R.color.white
            )
            .setNegativeButtonTheme(
                if (ThemeUtils.isViOn()) R.color.white else R.color.accent_color_fc6d5b,
                if (ThemeUtils.isViOn()) R.color.ViMainColor else R.color.white,
                strokeColor = if (isViOn()) R.color.ViMainColor else R.color.accent_color_fc6d5b
            )
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
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        changeAppLanguage()
        super.onCreate(savedInstanceState)

        setAppTheme()


        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        viewModel.getApiResponse().observe(this, this)
        initUi()

        intent.let {
            if (it.hasExtra("notificationBundle")) {
                val notifyBundle = it.getBundleExtra("notificationBundle")
                viewModel.bundle = notifyBundle
                viewModel.notificationId = notifyBundle?.getString("NotificationId")?.toInt() ?: 0
                viewModel.patchNotification()
                handleNotification(notifyBundle)


            }
        }

    }

    fun handleNotification(bundle: Bundle?, fromList: Boolean = false) {
        showLog("HANDLE_NOTIFICATION", "fromList >> $fromList")
        when (bundle?.getString("type").toString()) {

            NotificationType.COAUTHOR_REQUEST -> {
                navController?.navigateTo(
                    R.id.coAuthorRequestFragment,
                    bundleOf("coAuthorRequestId" to 1)
                )
            }
            NotificationType.COAUTHOR_COURSE_SUBMIT -> {
                setSelected(R.id.action_course, bundleOf("tabPosition" to 2))

            }

            NotificationType.COURSE_PUBLISHED, NotificationType.COURSE_REJECTED -> {
                val courseId = bundle?.get("courseId")?.toString() ?: "0"
//                showToastShort("yes")
                navController?.navigateTo(
                    R.id.contentCourseDetailFragment,
                    bundleOf("courseId" to courseId.toInt(), "status" to "creator")
                )

            }
            NotificationType.REVIEW_ADDED -> {
                val courseId = bundle?.get("courseId")?.toString() ?: "0"

                navController?.navigateTo(
                    R.id.contentCourseDetailFragment,
                    bundleOf(
                        "courseId" to courseId.toInt(),
                        "status" to "creator",
                        "goToReview" to true
                    )
                )


            }
            NotificationType.REWARDS_EARNED -> {

                navController?.navigateTo(R.id.rewardFragment)

            }
            NotificationType.ENROLLED_COURSE -> {
                setSelected(R.id.action_course)
            }
            NotificationType.COURSE_SUBMITTED -> {
                setSelected(R.id.action_course, bundleOf("tabPosition" to 2))
            }

            NotificationType.UPLOAD_SIGN, NotificationType.DELETE_SIGN -> {
                val courseId = bundle?.get("courseId")?.toString() ?: "0"
                navController?.navigateTo(
                    R.id.contentCourseDetailFragment,
                    bundleOf("courseId" to courseId.toInt(), "status" to "creator")
                )
            }
            else -> {
                if (getCurrentFragment() is HomeFragment) {
                    (getCurrentFragment() as HomeFragment?)?.refreshData()
                }
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
            if (tokenFromDataStore() == "") guestUserPopUp()
            else {
                if (viewModel.userProfile?.coursePolicy == true) {
                    navController?.navigateTo(R.id.addCourseBaseFragment)
                } else {
                    if (viewModel.userProfile?.coursePolicy == true) {
                        navController?.navigateTo(R.id.addCourseBaseFragment)
                    } else {
                        CreateCourseAcceptTermsDialog().apply {
                            setOnDialogClickListener(object : BaseDialog.IDialogClick {
                                override fun onDialogClick(vararg items: Any) {
                                    var view = items[0] as Int
                                    when (view) {
                                        Constant.CLICK_VIEW -> {

                                            findNavController().navigateTo(
                                                R.id.action_global_privacyFragment, bundleOf(
                                                    "type" to STATIC_PAGES_TYPE.TERMS
                                                )
                                            )
                                        }
                                        Constant.CLICK_ADD -> {
                                            viewModel.coursePolicyStatus(true)
//                                        sharedHomeVM.reportComment(
//                                            true,
//                                            wishListItem.reviewId,
//                                            wishListItem.courseId
//                                        )
                                        }
                                    }
                                }

                            })
                        }.show(supportFragmentManager, "")
                    }
                }
            }

        }
        observeCourseData()

        FirebaseMessaging.getInstance().subscribeToTopic("uat_all_loggedin") //for UAT
//                    FirebaseMessaging.getInstance().subscribeToTopic("production_all_loggedin") //for Production

        viewModel.getUserData().apply {
            viewModel.userProfile?.roles?.forEach {
                Log.d("varun", "onReceive: ${it.topicName}")
                FirebaseMessaging.getInstance().subscribeToTopic(it.topicName.toString())
            }
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
        supportActionBar?.setHomeActionContentDescription(R.string.back_button)
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
                R.id.privacyFragment,
                R.id.authorDetailsFragment
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
                arrayListOf(
                    R.id.moreFragment,
                    R.id.homeFragment,
                    R.id.myCourseTabFragment,
                    R.id.downloadedCourseFragment
                )
            bottomBarVisibility(bottomBarArray.contains(destination.id))
            if (bottomBarArray.contains(destination.id)) {


                setToolbar(
                    title = destination.label.toString(),
                    showToolbar = !hideToolbar.contains(destination.id),
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
        showLog("LAGGING_ISSE", "onBackPressed")
        hideKeyboard()
        val destArrayList = listOf(R.id.homeFragment)
        val bottomArray =
            listOf(
                R.id.myCourseTabFragment,
                R.id.moreFragment,
                R.id.paymentDetailsFragment,
                R.id.downloadedCourseFragment
            )
        when {
            destArrayList.contains(navController?.currentDestination?.id) -> {
                if (doubleBackToExitPressedOnce) {
                    finishAffinity()
                    return
                }

                this.doubleBackToExitPressedOnce = true
                showToastShort("Press again to exit")
                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    doubleBackToExitPressedOnce = false
                }, 2000)

            }
//            navController?.currentDestination?.id == R.id.myCourseTabFragment -> {
//                if ((getCurrentFragment() as MyCourseTabFragment?)?.onClickBack() == false) {
//                    showLog("LAGGING_ISSE", "onBackPressed")
//                    setSelected(R.id.action_home)
//                }
//            }
            getCurrentFragment() is MyCourseTabFragment? -> {
                if ((getCurrentFragment() as MyCourseTabFragment?)?.onClickBack() == false) {
                    showLog("LAGGING_ISSE", "onBackPressed")
                    setSelected(R.id.action_home)
                }
            }

            bottomArray.contains(navController?.currentDestination?.id) -> {
                showLog("LAGGING_ISSE", "onBackPressed")
                setSelected(R.id.action_home)
            }
            getCurrentFragment() is ProfileThumbFragment -> {
                setSelected(R.id.action_home)

            }
            getCurrentFragment() is PreferencesFragment? -> {
                (getCurrentFragment() as PreferencesFragment?)?.onClickBack()
            }
            getCurrentFragment() is LessonTextFragment? -> {
                (getCurrentFragment() as LessonTextFragment?)?.onClickBack()
            }
            getCurrentFragment() is AddQuizFragment? -> {
                (getCurrentFragment() as AddQuizFragment?)?.onClickBack()
            }
            getCurrentFragment() is RecordAudioFragment? -> {
                (getCurrentFragment() as RecordAudioFragment?)?.onClickBack()
            }
            getCurrentFragment() is AudioLectureFragment? -> {
                (getCurrentFragment() as AudioLectureFragment?)?.onClickBack()
            }
            getCurrentFragment() is DocLessonFragment? -> {
                (getCurrentFragment() as DocLessonFragment?)?.onClickBack()
            }
            getCurrentFragment() is VideoLectureFragment? -> {
                (getCurrentFragment() as VideoLectureFragment?)?.onClickBack()
            }
            getCurrentFragment() is AddCourseBaseNewFragment? -> {
                (getCurrentFragment() as AddCourseBaseNewFragment?)?.onClickBack()
            }

            getCurrentFragment() is VideoBaseFragment? -> {
                (getCurrentFragment() as VideoBaseFragment?)?.onClickBack()
            }

            getCurrentFragment() is DownloadedLectureViewFragment? -> {
                (getCurrentFragment() as DownloadedLectureViewFragment?)?.onClickBack()
            }

            getCurrentFragment() is DownloadedLectureViewFragment -> {
                (getCurrentFragment() as DownloadedLectureViewFragment).onClickBack()
            }
            getCurrentFragment() is EditAudioFragment? -> {
                (getCurrentFragment() as EditAudioFragment?)?.onClickBack()
            }
            getCurrentFragment() is QuizBaseFragment? -> {
                (getCurrentFragment() as QuizBaseFragment?)?.onClickBack()
            }
            getCurrentFragment() is QuizReportFragment? -> {
                (getCurrentFragment() as QuizReportFragment?)?.onClickBack()
            }
            getCurrentFragment() is PdfViewerFragment? -> {
                (getCurrentFragment() as PdfViewerFragment?)?.onClickBack()
            }
            else -> {
                showLog("LAGGING_ISSE", "onBackPressed")
                navController?.popBackStack()
            }
        }
    }

    fun setSelected(itemId: Int, bundle: Bundle? = null) {
        this.bottomBundle = bundle
        binding.bottomNavigationView.selectedItemId = itemId
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_more -> {
                if (tokenFromDataStore() == "") {
                    guestUserPopUp()
                } else {
                    navController?.navigateTo(R.id.moreFragment)
                    return true
                }
            }
            R.id.action_download -> {

                if (tokenFromDataStore() == "") {
                    guestUserPopUp()

                } else {
                    navController?.navigateTo(R.id.downloadedCourseFragment)

                }
                return true

            }
            R.id.action_course -> {
                if (tokenFromDataStore() == "") {
                    guestUserPopUp()

                } else {
                    navController?.navigateTo(R.id.myCourseTabFragment, bottomBundle)
                    return true
                }
            }

            R.id.action_home -> {
                if (tokenFromDataStore() == "") {
                } else {
                    navController?.navigateTo(R.id.homeFragment)
                }
            }
            else -> {
                return false
            }
        }
        return true
    }

    private fun getCurrentFragment(): Fragment? {
        val navFrag = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        return navFrag?.childFragmentManager?.fragments?.get(0)
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_COAUTHOR_INVITATION + "/home" -> {
                val type = value as Pair<String, Int>
                when (type.second) {
                    CoAuthorStatus.ACCEPT -> {
                        navController?.navigateTo(
                            R.id.addCourseBaseFragment,
                            bundleOf("courseId" to type.first.toInt())
                        )
                    }
                }
            }
            ApiEndPoints.API_COURSE_POLICY_STATUS -> {
                viewModel.userProfile?.coursePolicy = true
                navController?.navigateTo(R.id.addCourseBaseFragment)
            }
//            ApiEndPoints.API_PATCH_NOTIFICATION -> {
//                handleNotification(viewModel.bundle)
//            }
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
                sharedHomeVM.updateCourse(it.course?.courseId)
                navController?.navigateTo(
                    R.id.paymentDetailsFragment,
                    bundleOf("orderData" to it)
                )
            }

        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setBottomBar()


    }

    override fun callFragment(id: Int, isBottomFrag: Boolean) {
        super.callFragment(id, isBottomFrag)
        if (isBottomFrag) {
            setSelected(id)
        }
    }

    override fun updateTheme() {
        super.updateTheme()
        navController?.navigateTo(R.id.nav_profile_graph)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        showLog("intent_action", "onNewIntent >> ${intent?.extras}")
        intent?.let {
            if (it.hasExtra("notificationBundle")) {
                val notifyBundle = it.getBundleExtra("notificationBundle")
                viewModel.bundle = notifyBundle
                viewModel.notificationId = notifyBundle?.getString("NotificationId")?.toInt() ?: 0
                Log.d("test", "onNewIntent: ${notifyBundle?.get("userType").toString()}")
                if (notifyBundle?.get("userType").toString().toInt() != 0) {
                    viewModel.patchNotification()
                }
                handleNotification(notifyBundle)


            }
        }
    }


}
