package com.selflearningcoursecreationapp.ui.profile.requestTracker

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.MODSTATUS
import org.koin.androidx.viewmodel.ext.android.viewModel


class RequestTrackerDashboardFragment :
    BaseFragment<com.selflearningcoursecreationapp.databinding.FragmentRequestTrackerDashboardBinding>() {

    private val viewModel: RequestrackerVM by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_request_tracker_dashboard
    }

    private fun init() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observeModStatus()
        observerRequestCountLiveData()
        viewModel.modStatus()
        binding.viewModelXML = viewModel
        viewModel.requestCount()

        binding.buttonBecomeModerator.setOnClickListener {
            findNavController().navigate(R.id.action_requestTrackerDashboardFragment_to_becomeModeratorFragment)
        }
        binding.cardViewAcceptedRequest.setOnClickListener {
            findNavController().navigate(
                R.id.action_requestTrackerDashboardFragment_to_AcceptedRequestFragment,
                bundleOf(
                    "isRejected" to false,
                    "acceptedRequestId" to viewModel.requestCountLiveData.value?.acceptedRequestId
                )
            )
        }
        binding.cardViewRejectedReq.setOnClickListener {
            findNavController().navigate(
                R.id.action_requestTrackerDashboardFragment_to_AcceptedRequestFragment,
                bundleOf(
                    "isRejected" to true,
                    "rejectedRequestId" to viewModel.requestCountLiveData.value?.rejectedRequestId
                )
            )
        }

        binding.cardViewCoAuthor.setOnClickListener {
            findNavController().navigate(
                R.id.action_requestTrackerDashboardFragment_to_coAuthorRequestFragment,
                bundleOf("coAuthorRequestId" to viewModel.requestCountLiveData.value?.coAuthorRequestId)
            )
        }

        binding.cardViewModeratorComments.setOnClickListener {
            findNavController().navigate(
                R.id.action_requestTrackerDashboardFragment_to_moderatorsCommentFragment,
                bundleOf(
                    "modComment" to true,
                    "coAuthorRequestId" to viewModel.requestCountLiveData.value?.moderatorCommentsId
                )
            )
        }

        binding.cardViewRejectedCourses.setOnClickListener {
            findNavController().navigate(
                R.id.action_requestTrackerDashboardFragment_to_moderatorsCommentFragment,
                bundleOf(
                    "modComment" to false,
                    "coAuthorRequestId" to viewModel.requestCountLiveData.value?.rejectedCoursesId
                )
            )
        }

        binding.cardViewSendRequest.setOnClickListener {
            findNavController().navigate(
                R.id.action_requestTrackerDashboardFragment_to_sentRequestFragment,
                bundleOf("sentRequestId" to viewModel.requestCountLiveData.value?.sentRequestId)
            )
        }
    }

    private fun observerRequestCountLiveData() {
        viewModel.requestCountLiveData.observe(viewLifecycleOwner) {
            binding.textCoAuthorRequestCount.text = it.coAuthorRequestCount.toString()
            binding.textAcceptedRequestCount.text = it.acceptedRequestCount.toString()
            binding.textRejectedRequestCount.text = it.rejectedRequestCount.toString()
            binding.textModCommentsCount.text = it.moderatorCommentsCount.toString()
            binding.textRejectedCoursesCount.text = it.rejectedCoursesCount.toString()
            binding.textSendRequestCount.text = it.sentRequestCount.toString()
        }
    }

    private fun observeModStatus() {
        viewModel.modStatusLiveData.observe(viewLifecycleOwner) {
            binding.tvVerify.text = baseActivity.getString(R.string.verification)
            when (it) {
                MODSTATUS.BLOCKED -> {
                    binding.buttonBecomeModerator.visible()
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
                    binding.buttonBecomeModerator.gone()
                }
                MODSTATUS.REJECTED -> {
                    binding.ivRequestForm.loadImage(R.drawable.ic_correct_with_whitecircle)
                    binding.ivRequestSent.loadImage(R.drawable.ic_correct_with_whitecircle)
                    binding.ivVerify.loadImage(R.drawable.ic_rejected_request)
                    binding.tvVerify.text = baseActivity.getString(R.string.rejected)
                    binding.buttonBecomeModerator.visible()
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
                if (exception.statusCode == 400) {
                    hideLoading()
                } else {
                    super.onException(isNetworkAvailable, exception, apiCode)
                }
            }


        }
    }

    override fun onApiRetry(apiCode: String) {

    }

}