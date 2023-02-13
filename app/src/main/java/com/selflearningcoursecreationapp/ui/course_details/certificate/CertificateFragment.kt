package com.selflearningcoursecreationapp.ui.course_details.certificate

import android.app.DownloadManager
import android.graphics.Color
import android.os.Environment
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCertificateBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.ContentAssessmentQuestionAndDuration
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.ui.dialog.DownloadSampleOptionDialog
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseStatus
import com.selflearningcoursecreationapp.utils.CreatedCourseStatus
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import me.daemon.view.common.isNull
import java.util.*


class CertificateFragment : BaseFragment<FragmentCertificateBinding>(), View.OnTouchListener,
    View.OnClickListener {
    private val viewModel: CourseDetailVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    var manager: DownloadManager? = null
    private val root = Environment.getExternalStorageDirectory().toString()
    private val app_folder = "$root/Self/"
    var date: Date? = null
    private var downloadID: Long = 0

    override fun getLayoutRes(): Int {
        return R.layout.fragment_certificate
    }

    override fun onResume() {
        super.onResume()
        binding.getRoot().requestLayout();
        initUi()
    }

    private fun observeCourseData() {
        viewModel.purchaseCourseLiveData.observe(viewLifecycleOwner) {
            viewsHandling()
        }
    }


    private fun observeCertificateData() {
        viewModel.certificateLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {

                if (!it.contentURL.isNullOrEmpty()) {
                    if (viewModel.share == 1) {
                        if (it.contentURL != null) {
                            baseActivity.shareIntent(
                                "${getString(R.string.below_is_the_link_for_certificate_detail)}  \n" + it.contentURL?.replace(
                                    " ",
                                    "%20"
                                ) + "\n"
                            )
                        }
                    } else if (viewModel.share == 2) {
                        baseActivity.downloadPdf(
                            it.contentURL,
                            "Skillfy Achievement Certificate.pdf"
                        )

                    } else {
                        baseActivity.downloadPdf(
                            it.contentURL,
                            "Skillfy Completion Certificate.pdf"
                        )

                    }
                }
            }
        }
    }

    private fun observeAssessmentDetail() {
        viewModel.assessmentDetailLiveData.observe(viewLifecycleOwner) { assessmentMainData ->
            viewsHandling()
            assessmentMainData?.let {
                it.contentAssessmentQuestionAndDuration?.let { assessmentData ->
                    setAssessmentData(assessmentData)
                } ?: kotlin.run {
                    binding.cvTest.gone()
                }

                it.contentUserAssessmentLatestReport?.let {
                    binding.cvAssessment.visible()
                    binding.tvAttempt.visible()
                    binding.ivDot3.visible()
                    binding.btStart.text = getString(R.string.retake_assessment)
                } ?: kotlin.run {
                    binding.cvAssessment.gone()
                    binding.tvAttempt.gone()
                    binding.ivDot3.gone()
                }


            } ?: kotlin.run {
                binding.cvAssessment.gone()
                binding.cvTest.gone()
            }
        }
        viewModel.courseData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewsHandling()
        })
    }

    private fun setAssessmentData(
        assessmentData: ContentAssessmentQuestionAndDuration
    ) {
        if (viewModel.courseContentType == "creator" || viewModel.courseContentType == "moderatorComments") {
            binding.btStart.apply {
                isEnabled = true
                text = getString(R.string.review)
            }
        }

        binding.cvAppreciationCertificate.visibleView(assessmentData.assessmentPassed == true)
        binding.tvQuesCount.text = baseActivity.getQuantityString(
            R.plurals.question_quantity,
            if (viewModel.courseContentType != "creator") assessmentData.noOfQuesToBeDisplayed else assessmentData.totalQues
        )
        binding.cvTest.visible()
        assessmentData.totalDuration?.toLong()?.let { duration ->
            binding.tvQuesTime.text = duration.getTime(baseActivity)
        }


        if (assessmentData.AssessmentMandatory != true) {
            binding.tvTest.text = baseActivity.getString(R.string.test_your_knowledge)
        } else {
            binding.tvTest.text = baseActivity.getString(R.string.test_your_knowledge)
            binding.tvMandatory.visible()
        }
        binding.tvAttempt.text =
            "${assessmentData.attemptLeft} ${baseActivity.getString(R.string.attempts_left)}"

        if (assessmentData.attemptLeft == 0) {
            binding.btStart.isEnabled = false
            binding.tvAttempt.setTextColor(Color.RED)

            binding.tvRetakeTestDesc.visible()
            binding.tvRetakeTestDesc.text =
                baseActivity.getString(R.string.you_can_retake_assessment_tomorrow)

        }
    }


    private fun initUi() {
        observeCertificateData()
        observeAssessmentDetail()
        observeCourseData()


        if (viewModel.courseData.value?.createdById == viewModel.userProfile?.id) {
            binding.noDataTV.text = baseActivity.getString(R.string.no_certificate_added)
        } else {
//            if (viewModel.courseData.value?.userCourseStatus == CourseStatus.ENROLLED) {
            binding.noDataTV.text = baseActivity.getString(R.string.no_certificate_to_download)
//            } else {
//                binding.noDataTV.text = baseActivity.getString(R.string.no_certificate_error_Text)
//            }
        }


        viewsHandling()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.assessmentDetails()

        binding.ivInfo.setOnClickListener(this)
        binding.ivShare.setOnClickListener(this)
        binding.ivAppShare.setOnClickListener(this)
        binding.ivAppDownload.setOnClickListener(this)
        binding.ivDownload.setOnClickListener(this)
        binding.ivDownloadPre.setOnClickListener(this)
        binding.btStart.setOnClickListener(this)
        binding.cvAssessment.setOnClickListener(this)


    }

    private fun viewsHandling() {

//        binding.btStart.isEnabled =
//            (viewModel.courseData.value?.totalSectionsCompleted
//                ?: 0) == (viewModel.courseData.value?.totalSections
//                ?: 0) && (viewModel.assessmentDetailLiveData.value?.contentAssessmentQuestionAndDuration?.attemptLeft
//                ?: 0) > 0
        binding.cvCertificatePre.gone()
        binding.noDataTV.gone()
        binding.cvCertificate.gone()


        viewModel.courseData.value?.courseComments?.forEach { commentData ->

            when (commentData.commentType) {
                Constant.COMMENT_ASSESSMENT -> {
                    binding.tvAssessmentComment.setSpanString(
                        context?.let {
                            SpanUtils.with(
                                it,
                                "${getString(R.string.mod_comment)} ${commentData.comment}"
                            ).endPos(21).isBold().getSpanString()
                        }
                    )
                    binding.tvAssessmentComment.visibleView(
                        (commentData.comment?.isNotEmpty() == true) &&
                                (viewModel.courseData.value?.status != CreatedCourseStatus.SUBMIT || viewModel.courseContentType.equals(
                                    "moderatorComments"
                                ))
                    )

                }

            }
        }


        when (viewModel.courseData.value?.userCourseStatus) {
            CourseStatus.NOT_ENROLLED -> {
                binding.cvCertificatePre.visible()
            }
            CourseStatus.COMPELETD -> {
                binding.cvCertificate.visible()
            }
            else -> {
                binding.noDataTV.visibleView(viewModel.assessmentDetailLiveData.value?.contentAssessmentQuestionAndDuration.isNull())
            }

        }


    }


    override fun onApiRetry(apiCode: String) {

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (value) {
            ApiEndPoints.API_PURCHASE_COURSE -> {
                viewsHandling()

            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_info -> {
                CommonAlertDialog.builder(baseActivity)
                    .title(baseActivity.getString(R.string.how_to_get_certificate))
                    .description(baseActivity.getString(R.string.how_to_get_certificate_inf))
                    .positiveBtnText(baseActivity.getString(R.string.ok))
                    .hideNegativeBtn(true)
                    .icon(R.drawable.ic_info)
                    .build()
            }
            R.id.iv_share -> {
                viewModel.share = 1
                viewModel.downloadCertificate()
            }
            R.id.iv_app_share -> {
                viewModel.share = 1
                viewModel.downloadAppCertificate()
            }
            R.id.iv_app_download -> {
                viewModel.share = 2
                viewModel.downloadAppCertificate()
            }
            R.id.iv_download -> {
                viewModel.share = 0
                viewModel.downloadCertificate()
            }
            R.id.iv_download_pre -> {
//                showToastShort("yes")
//                baseActivity.downloadPdf(
//                    ApiEndPoints.PRE_CERTIFICATE,
//                    "Skillfy Certificate Sample.Pdf"
//                )
                val dialogue = DownloadSampleOptionDialog()
                dialogue.apply {
                    setOnDialogClickListener(object : BaseBottomSheetDialog.IDialogClick {
                        override fun onDialogClick(vararg items: Any) {
                            when (items[0]) {
                                Constant.DOWNLOAD_COMPLETIION -> {
                                    baseActivity.downloadPdf(
                                        ApiEndPoints.COM_CERTIFICATE,
                                        "Skillfy Certificate Sample.Pdf"
                                    )
                                }
                                Constant.DOWNLOAD_APPRECIATION -> {
                                    baseActivity.downloadPdf(
                                        ApiEndPoints.APP_CERTIFICATE,
                                        "Skillfy Achievement Sample.Pdf"
                                    )
                                }
                            }
                        }

                    })
                }.show(childFragmentManager, "")

            }
            R.id.bt_start -> {

                //       binding.btStart.isEnabled =
//            (viewModel.courseData.value?.totalSectionsCompleted
//                ?: 0) == (viewModel.courseData.value?.totalSections
//                ?: 0) && (viewModel.assessmentDetailLiveData.value?.contentAssessmentQuestionAndDuration?.attemptLeft
//                ?: 0) > 0
                if ((viewModel.courseData.value?.totalSectionsCompleted
                        ?: 0) != (viewModel.courseData.value?.totalSections
                        ?: 0) && (viewModel.courseData.value?.userCourseStatus == CourseStatus.ENROLLED || viewModel.courseData.value?.userCourseStatus == CourseStatus.IN_PROGRESS)
                /*&& (viewModel.assessmentDetailLiveData.value?.contentAssessmentQuestionAndDuration?.attemptLeft
                ?: 0) > 0*/) {
                    CommonAlertDialog.builder(baseActivity)
                        .title("")
                        .notCancellable(false)
                        .description(
                            getString(R.string.complete_all_lesson_for_assessment)
                        )
                        .positiveBtnText(baseActivity.getString(R.string.okay))
                        .hideNegativeBtn(true)
                        .icon(R.drawable.ic_info_large)
                        .setThemeIconColor(true)

                        .build()
//                    showToastShort(getString(R.string.complete_all_lesson_for_assessment))
                } else

                    if (viewModel.courseContentType == "creator" || viewModel.courseContentType == "moderatorComments") {
                        findNavController().navigateTo(
                            R.id.action_contentCourseDetailFragment_to_contentCreatorAssessmentFragment,
                            bundleOf(
                                "assessmentId" to viewModel.assessmentDetailLiveData.value?.contentAssessmentQuestionAndDuration?.assessmentId,
                            )
                        )
                    } else {
//                    if (viewModel.courseData.value?.totalSections == viewModel.courseData.value?.totalSectionsCompleted) {
                        if ((viewModel.courseData.value?.totalSectionsCompleted
                                ?: 0) == (viewModel.courseData.value?.totalSections
                                ?: 0) && (viewModel.assessmentDetailLiveData.value?.contentAssessmentQuestionAndDuration?.attemptLeft
                                ?: 0) > 0
                        ) {
                            findNavController().navigateTo(
                                R.id.action_courseDetailsFragment_to_quizBaseFragment,
                                bundleOf(
                                    "courseId" to viewModel.courseData.value?.courseId,
                                    "type" to Constant.CLICK_ASSESSMENT,
                                    "title" to getString(R.string.test_your_knowledge),
                                    "enableTimer" to true,


                                    )
                            )
                        } else {
                            showToastShort(getString(R.string.this_feature_is_accessible_after_you_enroll_the_course))
                        }
                    }
            }

            R.id.cv_assessment -> {
                findNavController().navigateTo(
                    R.id.action_courseDetailsFragment_to_quizReportFragment,
                    bundleOf(
                        "attemptId" to viewModel.assessmentDetailLiveData.value?.contentUserAssessmentLatestReport?.attemptedId,
                        "courseId" to viewModel.courseData.value?.courseId

                    )
                )
            }
        }

    }
}



