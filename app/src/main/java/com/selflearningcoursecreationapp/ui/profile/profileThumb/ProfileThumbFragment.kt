package com.selflearningcoursecreationapp.ui.profile.profileThumb

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.databinding.FragmentProfileThumbBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.ui.moderator.ModeratorActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.MODTYPE
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileThumbFragment : BaseFragment<FragmentProfileThumbBinding>(), HandleClick {

    private val viewModel: ProfileThumbViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity.supportActionBar?.hide()
        init()

    }

    @SuppressLint("ClickableViewAccessibility")
    fun init() {


        binding.testOne.constraintLayout.visibleView(baseActivity is ModeratorActivity)
        binding.constraintLayout.visibleView(baseActivity !is ModeratorActivity)
        binding.profileThumbVM = viewModel
        binding.profileThumb = this
        viewModel.checkedLiveData.value = baseActivity is ModeratorActivity





        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        setContentDescription()

//        binding.svModerator.setOnTouchListener { _, motionEvent ->
//            if (motionEvent?.action == MotionEvent.ACTION_DOWN) {
//                if (binding.svModerator.isChecked) {
//                    if (baseActivity is ModeratorActivity) {
//                        baseActivity.goToHomeActivity()
//                    }
//                } else {
//                    openPopUP()
//                }
//
//            }
//            false
//        }

//        binding.svChangeDash.setOnTouchListener { _, motionEvent ->
//            if (motionEvent?.action == MotionEvent.ACTION_DOWN) {
//                viewModel.saveViMode(binding.svChangeDash.isChecked)
////                baseActivity.setDayNightTheme()
//            }
//            false
//        }

        binding.svChangeDash.setOnClickListener {
//            baseActivity.showProgressBar()
//            viewModel.saveViMode(binding.svChangeDash.isChecked)
//            lifecycleScope.launch {
//                delay(1500)
//                baseActivity?.runOnUiThread {
//                    SelfLearningApplication.isViOn = binding.svChangeDash.isChecked
//                    baseActivity.setDayNightTheme()
//                    baseActivity.hideProgressBar()
//                }
//            }
        }


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity.supportActionBar?.hide()

    }

    private fun setContentDescription() {
        binding.txtUserName.contentDescription = "user name alisi nikolson"
        binding.tvUserMail.contentDescription = "user mail @limadecell"
    }


    override fun getLayoutRes() = R.layout.fragment_profile_thumb


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {

                R.id.txt_profile_details -> {

                    findNavController().navigate(R.id.action_profileThumbFragment_to_profileDetailsFragment)
                }

                R.id.txt_tracker -> {

                    findNavController().navigate(R.id.action_profileThumbFragment_to_requestTrackerDashboardFragment)
                }
                R.id.txt_wishlist -> {

                    findNavController().navigate(R.id.action_profileThumbFragment_to_bookmarkedCoursesFragment)
                }
                R.id.txt_moderator_switch -> {

//                    if ((viewModel.userProfile?.roles?.size ?: 0) > 1) {
                    viewModel.switchMod()
//                    } else {
//                        modPopUp()
//                    }
                }
                R.id.txt_moderator_switch_2 -> {
                    viewModel.switchMod()

                }
                R.id.txt_dashboard -> {
                    if (baseActivity is HomeActivity) {
                        findNavController().navigate(R.id.action_profileThumbFragment_to_dashboardBaseFragment)
                    } else {
                        findNavController().navigate(R.id.action_profileThumbFragment_to_modDashBaseFragment)
                    }
                }
                R.id.txt_courses -> {
                    (baseActivity as HomeActivity).setSelected(R.id.action_course)
                }
                R.id.txt_reward -> {
                    findNavController().navigate(R.id.action_profileThumbFragment_to_rewardFragment)
                }
                R.id.txt_my_qualifications -> {
                    findNavController().navigate(R.id.action_profileThumbFragment_to_fragment_mode_doc)
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
                    findNavController().popBackStack()
                }
                R.id.txt_my_bank -> {
                    findNavController().navigate(R.id.action_profileThumbFragment_to_myBankFragment)
                }
                R.id.txt_delete_account -> {
                    CommonAlertDialog.builder(baseActivity)
                        .title(getString(R.string.are_you_sure))
                        .description(getString(R.string.do_you_really_want_to_delete_your_account))
                        .positiveBtnText(getString(R.string.delete_acc))
                        .icon(R.drawable.ic_fogot_password)
                        .getCallback {
                            if (it) {
                                viewModel.deleteAccount()
                            }
                        }.build()
                }

            }
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_LOGOUT -> {
                baseActivity.goToInitialActivity()
            }
            ApiEndPoints.API_DELETE_ACCOUNT -> {
                baseActivity.goToInitialActivity()
            }
            ApiEndPoints.API_SWITCH_TO_MOD -> {
                (value as BaseResponse<UserProfile>).let {
                    if (viewModel.userProfile?.currentMode == MODTYPE.LEARNER /*&& it.resource?.roles?.get(
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
                if (exception.statusCode == 400) {
                    hideLoading()
                    modPopUp()

                } else if (exception.statusCode == 424) {
                    hideLoading()
                    underReviewPopUP()
                } else {
                    super.onException(isNetworkAvailable, exception, apiCode)
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

        viewModel.getUserData()
        binding.txtUserName.text = viewModel.userProfile?.name
        binding.tvUserMail.text = viewModel.userProfile?.email
        binding.svChangeDash.isChecked = baseActivity.isViOn()
        binding.testOne.txtUserName.text = viewModel.userProfile?.name
        binding.testOne.tvUserMail.text = viewModel.userProfile?.email
        binding.testOne.svChangeDash.isChecked = baseActivity.isViOn()

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
                    findNavController().navigate(R.id.action_profileThumbFragment_to_requestTrackerDashboardFragment)
                }
            }.build()
    }

    private fun underReviewPopUP() {
        CommonAlertDialog.builder(baseActivity)
            .hideNegativeBtn(true)
            .title("Under review")
            .spannedText(
                SpanUtils.with(
                    baseActivity,
                    "We’ve received your request. Your “become a moderator” request is under review we’ll let you know when it’s done."
                ).startPos(34).endPos(54).isBold().getSpanString()
            )
            .getCallback {


            }.notCancellable().icon(R.drawable.ic_under_review)
            .build()
    }
}