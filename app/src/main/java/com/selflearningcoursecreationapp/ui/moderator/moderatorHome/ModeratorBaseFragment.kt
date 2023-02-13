package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentModeratorBaseBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.NotificationData
import com.selflearningcoursecreationapp.ui.course_details.ratings.model.GetReviewsRequestModel
import com.selflearningcoursecreationapp.ui.moderator.dialog.filter.ModerateFilterDialogue
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.accepted.ApprovedFragment
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.pending.PendingModFragment
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.rejected.RejectedFragment
import com.selflearningcoursecreationapp.ui.moderator.moderatorHome.request.RequestFragment
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.ui.splash.MessageListener
import com.selflearningcoursecreationapp.ui.splash.WebSocketManager
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.customViews.LMSTextView
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.concurrent.thread


class ModeratorBaseFragment : BaseFragment<FragmentModeratorBaseBinding>(), HandleClick,
    BaseBottomSheetDialog.IDialogClick, MessageListener {

    override fun getLayoutRes() = R.layout.fragment_moderator_base
    private val viewModel: ModHomeVM by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity.supportActionBar?.hide()
        initUI()
    }

    override fun onResume() {
        super.onResume()
        activity?.setTransparentStatusBar()

    }

    private fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getNotificationCount()

        setUserData()

        binding.handleClick = this
        initViewPager()
//        binding.swipeRefresh.setOnRefreshListener {
//            binding.swipeRefresh.isRefreshing = false
//        }
        val color = ThemeUtils.getAppColor(baseActivity)
        binding.toolbarLayout.setContentScrimColor(color)
        binding.toolbarLayout.setBackgroundColor(color)
        binding.toolbarLayout.setStatusBarScrimColor(color)
        binding.appBar.setBackgroundColor(color)

        binding.filter.setOnClickListener {
            ModerateFilterDialogue().apply {
                arguments = bundleOf(
                    "filterData" to viewModel.filterData,
                    "position" to binding.tabLayout.selectedTabPosition
                )
                setOnDialogClickListener(this@ModeratorBaseFragment)
            }.show(childFragmentManager, "")
        }
        binding.ivNotification.setOnClickListener {
            findNavController().navigateTo(R.id.action_moderatorBaseFragment_to_notificationFragment)
        }
        binding.ivTalkback.setOnClickListener {
            baseActivity.checkAccessibilityService()
        }

        binding.imgMic.setOnClickListener {
            displaySpeechRecognizer(this)
        }

        searchFunctionality()

        thread {
            kotlin.run {
                WebSocketManager.init(
                    "${ApiEndPoints.WEB_SOCKET_USER_NOTIFICATION_COUNT}?Token=${baseActivity.tokenFromDataStore()}&LanguageId=${1}&ChannelId=2",
                    this,
                )
                WebSocketManager.connect()


            }
        }

        viewModel.switchTab.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.vpModeratorHome.currentItem = 1

            }
        }


        initFragmentResultListener()
    }

    private fun initFragmentResultListener() {
        setFragmentResultListener("modRequestData", listener = { _, bundle ->
            val requestType = bundle.getInt("requestType")

            showLog("MOD_HOME", " listener call")
            val status = bundle.getInt("status")
            when (requestType) {
                ModHomeConst.REQUEST -> {
                    if (status == CoAuthorStatus.ACCEPT) {
                        showLog("MOD_HOME", " listener accepted")
                        lifecycleScope.launch {
                            delay(500)
                            baseActivity.runOnUiThread {
                                showLog("MOD_HOME", " listener refresh")
                                binding.vpModeratorHome.currentItem = 1
                                viewModel.refreshData.value = true

                            }
                        }
                    }

                }
            }


        })
    }

    private fun searchFunctionality() {

        spokenTextLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { value ->
                binding.etSearch.setText(value)
                viewModel.isTextSearch = true
                viewModel.filterData.generalSearchField = binding.etSearch.content()
                viewModel.refreshData.value = true
            }
        }

        binding.imgCross.setOnClickListener {

            binding.etSearch.text?.clear()
            binding.imgCross.gone()
            binding.imgMic.visible()

        }

        binding.etSearch.doOnTextChanged { text, start, before, count ->
            if (text.toString().isEmpty() && viewModel.isTextSearch) {
                viewModel.isTextSearch = false
                binding.imgCross.gone()
                binding.imgMic.visible()
                viewModel.filterData.generalSearchField = binding.etSearch.content()
                viewModel.refreshData.value = true
            } else if (text.toString().isEmpty()) {
                binding.imgCross.gone()
                binding.imgMic.visible()
            } else {
                binding.imgCross.visible()
                binding.imgMic.gone()
            }
        }

        binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.isTextSearch = true
                viewModel.filterData.generalSearchField = binding.etSearch.content()
                viewModel.refreshData.value = true
            }

            return@setOnEditorActionListener true
        }
    }

    override fun onStart() {
        super.onStart()
        baseActivity.supportActionBar?.hide()

    }

    private fun setUserData() {
        viewModel.getUserData()

        binding.tvUserName.apply {

            val value = viewModel.userProfile?.name ?: "Guest"
            if (value.length > 12) {
                val str = value.substring(0, 12)
                text = "${str}..."
            } else {
                text = value
            }

        }
        binding.ivUserImage.loadImage(
            viewModel.userProfile?.profileUrl,
            R.drawable.ic_default_user_grey
        )

        binding.tvUserName.contentDescription = "User name is" + viewModel.userProfile?.name
    }

    private fun initViewPager() {
        val list = ArrayList<Fragment>()
        list.add(RequestFragment())
        list.add(PendingModFragment())
        list.add(ApprovedFragment())
        list.add(RejectedFragment())

        val nameArray = ArrayList<String>()
        nameArray.add(getString(R.string.requeste))
        nameArray.add(getString(R.string.pending))
        nameArray.add(getString(R.string.approved))
        nameArray.add(getString(R.string.rejected))
        binding.vpModeratorHome.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)

        TabLayoutMediator(binding.tabLayout, binding.vpModeratorHome) { tab, position ->
            tab.text = nameArray[position]
        }.attach()
        showLog("MOD_HOME", " init Pager")

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.selectedTabPosition = tab?.position ?: 0

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }


        })
        binding.tabLayout.setCustomTabsFixed(nameArray)
        binding.tabLayout.getTabAt(0)?.customView?.isSelected = true
        binding.vpModeratorHome.isUserInputEnabled = false

        binding.tabLayout.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))
        binding.tabLayout.setTabTextColors(
            ContextCompat.getColor(
                baseActivity,
                R.color.hint_color_929292
            ), ThemeUtils.getPrimaryTextColor(baseActivity)
        )
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.customView as LMSTextView?)?.changeFontType(ThemeConstants.FONT_SEMI_BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.customView as LMSTextView?)?.changeFontType(ThemeConstants.FONT_MEDIUM)

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("main", "")

            }
        })
    }

    override fun onApiRetry(apiCode: String) {
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.iv_user_image -> {
                    if (baseActivity.tokenFromDataStore() == "") {
                        baseActivity.guestUserPopUp()
                    } else {
                        findNavController().navigateTo(R.id.action_moderatorBaseFragment_to_nav_profile_graph)
                    }

                }
                R.id.tv_user_name -> {
                    if (baseActivity.tokenFromDataStore() == "") {
                        baseActivity.guestUserPopUp()
                    } else {
                        findNavController().navigateTo(R.id.action_moderatorBaseFragment_to_nav_profile_graph)
                    }

                }

            }

        }
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
                DialogType.HOME_FILTER -> {

                    viewModel.filterData = items[1] as GetReviewsRequestModel
                    viewModel.filterData.generalSearchField = binding.etSearch.content()
                    viewModel.refreshData.value = true
                    binding.ivRed.visibleView(!viewModel.filterData.searchFields.isNullOrEmpty())

                }
            }
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_NOTIFICATION_COUNT -> {
                (value as BaseResponse<NotificationData>).let { notificationData ->
                    binding.tvNotificationCount.apply {
                        text = notificationData.resource?.totalunreadnotificationcount.toString()
                        visibleView(notificationData.resource?.totalunreadnotificationcount != 0)
                    }
                }
            }
        }
    }

    fun refreshData() {
        if (binding.vpModeratorHome.currentItem == 0) {
            showLog("MOD_HOME", " home refresh")
            viewModel.refreshData.value = true
        }
        showLog("REFRESH_DATA", "home refresh")
        if (baseActivity.tokenFromDataStore().isNotEmpty() && isVisible && isAdded) {
            viewModel.getNotificationCount()
        }
    }

    override fun onConnectSuccess() {

    }

    override fun onConnectFailed() {
    }

    override fun onClose() {
    }

    override fun onMessage(text: String?) {
        showLog("web", text ?: "")
        try {
            val jsObj = Gson().fromJson(text?.toString() ?: "", NotificationData::class.java)
            baseActivity.runOnUiThread {

                binding.tvNotificationCount.text =
                    jsObj?.totalunreadnotificationcount?.toString() ?: ""
                binding.tvNotificationCount.visibleView(
                    (jsObj?.totalunreadnotificationcount ?: 0) != 0
                )

            }
        } catch (e: JSONException) {
            showLog("WEB_SOCKET", "parsing error")
            showException(e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.setTransparentLightStatusBar()
        WebSocketManager.close()
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (exception.statusCode) {
            HTTPCode.USER_NOT_FOUND -> {
                hideLoading()
                CommonAlertDialog.builder(baseActivity)
                    .description(exception.message ?: "")
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .icon(R.drawable.ic_alert)
                    .title("")
                    .notCancellable(true)
                    .hideNegativeBtn(true)
                    .getCallback {
                        if (it) {
                            viewModel.refreshData.value = true
                        }
                    }.build()
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)
            }
        }
    }

}