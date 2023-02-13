package com.selflearningcoursecreationapp.ui.profile.profileThumb

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentProfileThumbBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.CategoryResponse
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.ui.moderator.ModeratorActivity
import com.selflearningcoursecreationapp.ui.splash.MessageListener
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.MODSTATUS
import com.selflearningcoursecreationapp.utils.MODTYPE
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileThumbFragment : BaseFragment<FragmentProfileThumbBinding>(), HandleClick,
    MessageListener {
    private val viewModel: ProfileThumbViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity.supportActionBar?.hide()
        init()

    }

    @SuppressLint("ClickableViewAccessibility")
    fun init() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.testOne.constraintLayout.visibleView(baseActivity is ModeratorActivity)
        binding.constraintLayout.visibleView(baseActivity !is ModeratorActivity)
        binding.profileThumbVM = viewModel
        binding.profileThumb = this
        viewModel.checkedLiveData.value = baseActivity is ModeratorActivity



        binding.txtUserName.contentDescription = "User name is " + viewModel.userProfile?.name

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity.supportActionBar?.hide()

    }


    override fun getLayoutRes() = R.layout.fragment_profile_thumb


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {

                R.id.txt_profile_details -> {
                    findNavController().navigateTo(R.id.action_profileThumbFragment_to_profileDetailsFragment)
                }

                R.id.txt_tracker -> {
                    findNavController().navigateTo(R.id.action_profileThumbFragment_to_requestTrackerDashboardFragment)
                }
                R.id.img_talk -> {
                    baseActivity.checkAccessibilityService()
                }
                R.id.txt_wishlist -> {
                    findNavController().navigateTo(R.id.action_profileThumbFragment_to_bookmarkedCoursesFragment)
                }
                R.id.sv_change_dash -> {

                    viewLifecycleOwner.lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
                            viewModel.changeViMode = binding.svChangeDash.isChecked

                            viewModel.changeViModeStatus()
                        }
                    }
                }
                R.id.sv_change_dash1 -> {

                    viewLifecycleOwner.lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
                            viewModel.changeViMode = binding.testOne.svChangeDash1.isChecked
                            viewModel.changeViModeStatus()
                        }
                    }
                }
                R.id.txt_moderator_switch -> {
                    viewModel.switchMod()
                }
                R.id.txt_moderator_switch_2 -> {
                    viewModel.switchMod()

                }
                R.id.txt_dashboard -> {
                    if (baseActivity is HomeActivity) {
                        findNavController().navigateTo(R.id.action_profileThumbFragment_to_dashboardBaseFragment)
                    } else {
                        findNavController().navigateTo(R.id.action_profileThumbFragment_to_modDashBaseFragment)
                    }
                }
                R.id.txt_courses -> {
//                    (baseActivity as HomeActivity).setSelected(
//                        R.id.action_course,
//                        bundleOf("fromProfile" to true)
//                    )
                    findNavController().navigateTo(
                        R.id.myCourseTabFragment,
                        bundleOf("fromProfile" to true)
                    )
                }
                R.id.txt_reward -> {
                    findNavController().navigateTo(R.id.action_profileThumbFragment_to_rewardFragment)
                }
                R.id.txt_my_qualifications -> {
                    findNavController().navigateTo(R.id.action_profileThumbFragment_to_fragment_mode_doc)
                }
                R.id.txt_my_categories -> {
                    findNavController().navigateTo(R.id.action_profileThumbFragment_to_fragment_mode_my_categories)
                }
                R.id.txt_logout -> {
                    CommonAlertDialog.builder(baseActivity)
                        .title(getString(R.string.come_back_soon))
                        .description(getString(R.string.are_you_sure_you_want_to_logout))
                        .positiveBtnText(getString(R.string.log_out))
                        .icon(R.drawable.ic_fogot_password)
                        .getCallback {
                            if (it) {
                                viewModel.callLogout()
                            }
                        }.build()
                }
                R.id.img_back -> {
                    baseActivity.onBackPressed()
                }
                R.id.txt_my_bank -> {
//                    showCommingSoonDialog(getString(R.string.coming_soon))
                    findNavController().navigateTo(R.id.action_profileThumbFragment_to_myBankFragment)
                }
//                R.id.txt_delete_account -> {
//                    CommonAlertDialog.builder(baseActivity)
//                        .title(getString(R.string.are_you_sure))
//                        .description(getString(R.string.do_you_really_want_to_delete_your_account))
//                        .positiveBtnText(getString(R.string.delete_acc))
//                        .icon(R.drawable.ic_fogot_password)
//                        .getCallback {
//                            if (it) {
//                                viewModel.deleteAccount()
//                            }
//                        }.build()
//                }

            }
        }
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_LOGOUT -> {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("uat_all_loggedin") //for UAT
//                   FirebaseMessaging.getInstance().unsubscribeFromTopic("production_all_loggedin") //for Production

                viewModel.getUserData().apply {
                    viewModel.userProfile?.roles?.forEach {
                        Log.d("varun", "onReceive: ${it.topicName}")
                        FirebaseMessaging.getInstance()
                            .unsubscribeFromTopic(it.topicName.toString())
                    }
                }
                baseActivity.goToInitialActivity()
            }
//            ApiEndPoints.API_DELETE_ACCOUNT -> {
//                baseActivity.goToInitialActivity()
//            }
            ApiEndPoints.API_CHANGE_VI_MODE -> {
                baseActivity.showProgressBar()
                viewModel.saveViMode(viewModel.changeViMode)
                lifecycleScope.launch {
//                    delay(500)
                    showLoading()
                    baseActivity.runOnUiThread {
                        SelfLearningApplication.isViOn = viewModel.changeViMode
                        baseActivity.setDayNightTheme()
                        baseActivity.updateTheme()
                    }
//            }
                }
            }
            ApiEndPoints.API_SWITCH_TO_MOD -> {
                (value as BaseResponse<UserProfile>).let {
                    if (viewModel.userProfile?.currentMode == MODTYPE.LEARNER


                    /*&& it.resource?.roles?.get(
                            1)?.id == 3*/
                    ) {
                        baseActivity.goToModeratorActivity()
                    } else if (viewModel.userProfile?.currentMode == MODTYPE.MODERATOR) {
                        baseActivity.goToHomeActivity()
                    }
                }

            }

        }
    }


    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_SWITCH_TO_MOD -> {
                if (exception.statusCode == HTTPCode.UN_SUCESS) {
                    hideLoading()
                    modPopUp()

                } else if (exception.statusCode == HTTPCode.DATA_MISSING_VALIDATION) {
                    hideLoading()
                    val gson = Gson()
                    var jsonObject = gson.toJsonTree(exception.data).asJsonObject
                    val category = Gson().fromJson(
                        jsonObject, CategoryResponse::class.java
                    )

                    var categoryNames = ""
                    var filteredList = category.list?.filter { it.status == MODSTATUS.PENDING }
                    if (filteredList?.size ?: 0 > 0) {
                        filteredList?.forEach { categoryNames = categoryNames + ", " + it.name }
                        underReviewPopUP(categoryNames.substring(2))
                    }


//                    category.list?.let { underReviewPopUP(
//                        it.get(0)) }
                } else if (exception.statusCode == HTTPCode.FORBIDDEN) {
                    hideLoading()
                    accountBlockedPopup()
                } else {
                    super.onException(isNetworkAvailable, exception, apiCode)
                }
            }
            ApiEndPoints.API_DELETE_ACCOUNT -> {
                hideLoading()
                when (exception.statusCode) {
                    HTTPCode.COURSE_HAS_ENROLLED_USERS -> {
                        CommonAlertDialog.builder(baseActivity)
                            .title(getString(R.string.are_you_sure))
                            .description(exception.message ?: "")
                            .positiveBtnText(getString(R.string.delete_acc))
                            .icon(R.drawable.ic_fogot_password)
                            .getCallback {
                                if (it) {
                                    viewModel.deleteAccount(true)
                                }
                            }.build()
                    }
                    HTTPCode.CREATOR_HAS_PENDING_BALANCE -> {
                        CommonAlertDialog.builder(baseActivity)
                            .title(getString(R.string.are_you_sure))
                            .description(exception.message ?: "")
                            .positiveBtnText(getString(R.string.delete_acc))
                            .icon(R.drawable.ic_fogot_password)
                            .getCallback {
                                if (it) {
                                    viewModel.deleteAccount(true, true)
                                }
                            }.build()
                    }
                    else -> {
                        super.onException(isNetworkAvailable, exception, apiCode)

                    }
                }
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)

            }
        }
    }


    override fun onResume() {
        super.onResume()

        activity?.setTransparentStatusBar()
        viewModel.getUserData()
        binding.txtUserName.text = viewModel.userProfile?.name
        binding.tvUserMail.text = viewModel.userProfile?.email
        binding.svChangeDash.isChecked = baseActivity.isViOn()
        binding.testOne.txtUserName.text = viewModel.userProfile?.name
        binding.testOne.tvUserMail.text = viewModel.userProfile?.email
        binding.testOne.svChangeDash1.isChecked = baseActivity.isViOn()

        binding.circle.loadImage(
            viewModel.userProfile?.profileUrl,
            R.drawable.ic_default_user_grey,
            viewModel.userProfile?.profileBlurHash
        )
        binding.testOne.circle.loadImage(
            viewModel.userProfile?.profileUrl,
            R.drawable.ic_default_user_grey,
            viewModel.userProfile?.profileBlurHash
        )
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    private fun modPopUp() {
        CommonAlertDialog.builder(baseActivity)
            .title(baseActivity.getString(R.string.moderator_mode))
            .spannedText(
                SpanUtils.with(
                    baseActivity,
                    baseActivity.getString(R.string.become_moderator_desc_text)
                ).startPos(60).isBold().getSpanString()
            )
            .positiveBtnText(baseActivity.getString(R.string.okay))
            .hideNegativeBtn(true)
            .icon(R.drawable.ic_become_moderator_alert)
            .getCallback {
                if (it) {
                    findNavController().navigateTo(R.id.action_profileThumbFragment_to_requestTrackerDashboardFragment)
                }
            }.build()
    }

    private fun underReviewPopUP(category: String) {

        var firstHalf = SpanUtils.with(
            baseActivity,
            baseActivity.getString(R.string.moderator_under_review_desc) + " "

        ).startPos(34).endPos(54).isBold().getSpanString()

        var secondHalf = SpanUtils.with(
            baseActivity,
            String.format(
                baseActivity.getString(R.string.moderator_under_review_desc_2),
                category
            )

        ).startPos(0).endPos((category?.length ?: 0).plus(2)).isBold().getSpanString()

        var finslSpannedString = SpannableString(TextUtils.concat(firstHalf, secondHalf))


        CommonAlertDialog.builder(baseActivity)
            .hideNegativeBtn(true)
            .title(baseActivity.getString(R.string.under_review))
            .spannedText(
                finslSpannedString
            )
            .notCancellable(false).icon(R.drawable.ic_under_review)
            .build()
    }

    private fun accountBlockedPopup() {

        CommonAlertDialog.builder(baseActivity)
            .hideNegativeBtn(true)
            .title(baseActivity.getString(R.string.moderator_access_removed))
            .description(getString(R.string.account_blocked_desc_text))
            .getCallback {


            }.notCancellable(false).icon(R.drawable.ic_help_desk)
            .build()
    }

    override fun onConnectSuccess() {
        Log.e("websocket", "onConnectSuccess")

    }

    override fun onConnectFailed() {
        Log.e("websocket", "onConnectFailed")

    }

    override fun onClose() {
        Log.e("websocket", "onClose")

    }

    override fun onMessage(text: String?) {
        Log.e("websocket", "$text")


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.setTransparentLightStatusBar()
    }


}