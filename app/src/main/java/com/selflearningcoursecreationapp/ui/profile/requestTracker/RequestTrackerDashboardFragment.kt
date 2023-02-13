package com.selflearningcoursecreationapp.ui.profile.requestTracker

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.MODSTATUS
import com.selflearningcoursecreationapp.utils.isLessThan9
import org.koin.androidx.viewmodel.ext.android.viewModel


class RequestTrackerDashboardFragment :
    BaseFragment<com.selflearningcoursecreationapp.databinding.FragmentRequestTrackerDashboardBinding>() {

    private val viewModel: RequestrackerVM by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callMenu()
        init()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_request_tracker_dashboard
    }


    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observeModStatus()
        observerRequestCountLiveData()
        viewModel.modStatus()
        binding.viewModelXML = viewModel
        viewModel.requestCount()

        binding.buttonBecomeModerator.setOnClickListener {
            findNavController().navigateTo(R.id.action_requestTrackerDashboardFragment_to_becomeModeratorFragment)
        }
        binding.cardViewAcceptedRequest.setOnClickListener {
            findNavController().navigateTo(
                R.id.action_requestTrackerDashboardFragment_to_AcceptedRequestFragment,
                bundleOf(
                    "isRejected" to false,
                    "acceptedRequestId" to viewModel.requestCountLiveData.value?.acceptedRequestId
                )
            )
        }
        binding.cardViewRejectedReq.setOnClickListener {
            findNavController().navigateTo(
                R.id.action_requestTrackerDashboardFragment_to_AcceptedRequestFragment,
                bundleOf(
                    "isRejected" to true,
                    "rejectedRequestId" to viewModel.requestCountLiveData.value?.rejectedRequestId
                )
            )
        }

        binding.cardViewCoAuthor.setOnClickListener {
            findNavController().navigateTo(
                R.id.action_requestTrackerDashboardFragment_to_coAuthorRequestFragment,
                bundleOf("coAuthorRequestId" to viewModel.requestCountLiveData.value?.coAuthorRequestId)
            )
        }

        binding.cardViewModeratorComments.setOnClickListener {
            findNavController().navigateTo(
                R.id.action_requestTrackerDashboardFragment_to_moderatorsCommentFragment,
                bundleOf(
                    "modComment" to true,
                    "coAuthorRequestId" to viewModel.requestCountLiveData.value?.moderatorCommentsId
                )
            )
        }

        binding.cardViewRejectedCourses.setOnClickListener {
            findNavController().navigateTo(
                R.id.action_requestTrackerDashboardFragment_to_moderatorsCommentFragment,
                bundleOf(
                    "modComment" to false,
                    "coAuthorRequestId" to viewModel.requestCountLiveData.value?.rejectedCoursesId
                )
            )
        }

        binding.cardViewSendRequest.setOnClickListener {
            findNavController().navigateTo(
                R.id.action_requestTrackerDashboardFragment_to_sentRequestFragment,
                bundleOf("sentRequestId" to viewModel.requestCountLiveData.value?.sentRequestId)
            )
        }

        binding.cardViewPaymentWithdrawls.setOnClickListener {
            findNavController().navigateTo(
                R.id.action_requestTrackerDashboardFragment_to_paymentWithdrawlsFragment
            )
        }
    }

    private fun observerRequestCountLiveData() {
        viewModel.requestCountLiveData.observe(viewLifecycleOwner) {
            binding.textCoAuthorRequestCount.text = isLessThan9(it.coAuthorRequestCount ?: 0)
            binding.textAcceptedRequestCount.text = isLessThan9(it.acceptedRequestCount ?: 0)
            binding.textRejectedRequestCount.text = isLessThan9(it.rejectedRequestCount ?: 0)
            binding.textModCommentsCount.text = isLessThan9(it.moderatorCommentsCount)
            binding.textRejectedCoursesCount.text = isLessThan9(it.rejectedCoursesCount ?: 0)
            binding.textSendRequestCount.text = isLessThan9(it.sentRequestCount ?: 0)

            binding.cardViewCoAuthor.contentDescription =
                "${it.coAuthorRequestCount} Co-Author requests"
            binding.cardViewSendRequest.contentDescription = "${it.sentRequestCount} Send requests"
            binding.cardViewAcceptedRequest.contentDescription =
                "${it.acceptedRequestCount} Accepted Request"
            binding.cardViewRejectedReq.contentDescription =
                "${it.rejectedRequestCount} rejected requests"
            binding.cardViewModeratorComments.contentDescription =
                "${it.moderatorCommentsCount} moderator comments"
            binding.cardViewRejectedCourses.contentDescription =
                "${it.rejectedCoursesCount} rejected courses"
            binding.textPaymentWithdrawlsCount.text =
                isLessThan9(it.paymentWithdrawRequestCount ?: 0)
        }
    }

    private fun observeModStatus() {
        viewModel.modStatusLiveData.observe(viewLifecycleOwner) {
            binding.tvVerify.text = baseActivity.getString(R.string.verification)
            when (it) {
                MODSTATUS.BLOCKED -> {
                    binding.ivRequestForm.loadImage(R.drawable.ic_correct_with_whitecircle)
                    binding.ivRequestSent.loadImage(R.drawable.ic_correct_with_whitecircle)
                    binding.ivVerify.loadImage(R.drawable.ic_rejected_account)
                    binding.tvVerify.text = baseActivity.getString(R.string.account_blocked)
                    binding.buttonBecomeModerator.gone()


                }
                MODSTATUS.ACCEPTED -> {
                    binding.ivRequestForm.loadImage(R.drawable.ic_correct_with_whitecircle)
                    binding.ivRequestSent.loadImage(R.drawable.ic_correct_with_whitecircle)
                    binding.ivVerify.loadImage(R.drawable.ic_correct_with_whitecircle)
                    binding.tvVerify.text = baseActivity.getString(R.string.verified)
                    binding.buttonBecomeModerator.gone()
                }
                MODSTATUS.PENDING -> {
                    binding.ivRequestForm.loadImage(R.drawable.ic_correct_with_whitecircle)
                    binding.ivRequestSent.loadImage(R.drawable.ic_correct_with_whitecircle)
                    binding.tvVerify.text = baseActivity.getString(R.string.under_verification)
                    binding.ivVerify.loadImage(R.drawable.ic_verification_icon)
                    binding.buttonBecomeModerator.gone()
                }
                MODSTATUS.REJECTED -> {
                    binding.ivRequestForm.loadImage(R.drawable.ic_correct_with_whitecircle)
                    binding.ivRequestSent.loadImage(R.drawable.ic_correct_with_whitecircle)
                    binding.ivVerify.loadImage(R.drawable.ic_rejected_request)
                    binding.tvVerify.text = baseActivity.getString(R.string.rejected)
                    binding.buttonBecomeModerator.visible()
                    binding.buttonBecomeModerator.text =
                        baseActivity.getString(R.string.request_again)
                }
                MODSTATUS.CANCELLED -> {
                    binding.ivRequestForm.loadImage(R.drawable.ic_request_form_icon)
                    binding.ivRequestSent.loadImage(R.drawable.ic_request_sent_icon)
                    binding.ivVerify.loadImage(R.drawable.ic_verification_icon)
                    binding.buttonBecomeModerator.visible()
                }
            }
        }
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_MOD_STATUS -> {
//                (value as BaseResponse<UserProfile>).let {
//
//
//                }
            }
        }
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_MOD_STATUS -> {

                when (exception.statusCode) {
                    HTTPCode.UN_SUCESS -> {
                        hideLoading()
                        binding.ivRequestForm.loadImage(R.drawable.ic_request_form_icon)
                        binding.ivRequestSent.loadImage(R.drawable.ic_request_sent_icon)
                        binding.ivVerify.loadImage(R.drawable.ic_verification_icon)
                        binding.buttonBecomeModerator.visible()
                        binding.tvVerify.text = baseActivity.getString(R.string.verification)
                    }
                    HTTPCode.FORBIDDEN -> {
                        hideLoading()
                    }
                    else -> {
                        super.onException(isNetworkAvailable, exception, apiCode)
                    }
                }
            }


        }
    }

    override fun onApiRetry(apiCode: String) {

    }

    fun refreshData() {
        viewModel.requestCount()
        viewModel.modStatus()

    }

}