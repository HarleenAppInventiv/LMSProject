package com.selflearningcoursecreationapp.ui.course_details.certificate

import android.R.attr.mimeType
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCertificateBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.course_details.CourseDetailVM
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.CourseStatus
import java.io.File
import java.util.*


class CertificateFragment : BaseFragment<FragmentCertificateBinding>() {
    private val viewModel: CourseDetailVM by viewModels({ if (parentFragment !is NavHostFragment) requireParentFragment() else this })
    var manager: DownloadManager? = null
    private val root = Environment.getExternalStorageDirectory().toString()
    private val app_folder = "$root/Self/"
    var date: Date? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_certificate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCertificateData()
        observeAssessmentDetail()
        initUi()
        observeCourseData()

    }

    private fun observeCourseData() {
        viewModel.purchaseCourseLiveData.observe(viewLifecycleOwner) {
            viewsHandling(CourseStatus.ENROLLED)
        }
    }

    private fun observeCertificateData() {
        viewModel.certificateLiveData.observe(viewLifecycleOwner) {
            if (it.contentURL != null) {
                if (it.contentURL?.isNotEmpty() == true) {
                    manager =
                        requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
                    val uri: Uri = Uri.parse(it.contentURL)
                    val request = DownloadManager.Request(uri)
                    request.setMimeType("application/pdf")
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        it.contentName
                    )
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                    manager?.enqueue(request)
                }

            }


        }
    }

    private fun observeAssessmentDetail() {
        viewModel.assessmentDetailLiveData.observe(viewLifecycleOwner) { assessmentData ->
            viewsHandling(viewModel.courseData.value?.userCourseStatus)
            assessmentData?.let {
                it.contentAssessmentQuestionAndDuration?.let { assessmentData ->
                    binding.tvQuesCount.text = baseActivity.getQuantityString(
                        R.plurals.question_quantity,
                        it.contentAssessmentQuestionAndDuration?.noOfQuesToBeDisplayed
                    )
                    binding.cvTest.visible()
                    binding.noDataTV.gone()
                    assessmentData.totalDuration?.toLong()?.let { duration ->
                        binding.tvQuesTime.text = duration.getTime(baseActivity)
                    }
                } ?: kotlin.run {
                    binding.cvTest.gone()
                }

                if (it.contentAssessmentQuestionAndDuration?.AssessmentMandatory != true) {
                    binding.tvTest.text =
                        getString(R.string.test_your_knowledge) + " " + getString(R.string.not_mandatory)
                } else {
                    binding.tvTest.text =
                        getString(R.string.test_your_knowledge) + " " + getString(R.string.mandatory)

                }


                if (it.contentAssessmentQuestionAndDuration?.attemptLeft == 0) {
                    binding.btStart.isEnabled = false
                    binding.tvAttempt.setTextColor(Color.RED)

                }
                binding.tvAttempt.text =
                    "${it.contentAssessmentQuestionAndDuration?.attemptLeft}${getString(R.string.attempts_left)}"

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
    }


    private fun initUi() {
        viewsHandling(viewModel.courseData.value?.userCourseStatus)

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.assessmentDetails()
        binding.ivDownload.setOnClickListener {
            viewModel.downloadCertificate()


        }
        binding.btStart.setOnClickListener {
            if (viewModel.courseData.value?.userCourseStatus == CourseStatus.ENROLLED) {
                findNavController().navigate(
                    R.id.action_courseDetailsFragment_to_quizBaseFragment,
                    bundleOf(
                        "courseId" to viewModel.courseData.value?.courseId,
                        "type" to Constant.CLICK_ASSESSMENT,
                        "title" to "Test Your Knowledge",
                    )
                )
            } else {
                showToastShort("This feature is accessible after you'll enroll the course")
            }

        }
        binding.cvAssessment.setOnClickListener {
            findNavController().navigate(
                R.id.action_courseDetailsFragment_to_quizReportFragment,
                bundleOf(
                    "attemptId" to viewModel.assessmentDetailLiveData.value?.contentUserAssessmentLatestReport?.attemptedId,
                    "courseId" to viewModel.courseData.value?.courseId
                )
            )

        }


    }

    private fun viewsHandling(userCourseStatus: Int?) {

        when {
            baseActivity.tokenFromDataStore().isNullOrEmpty() -> {
                binding.cvCertificate.gone()
                binding.cvCertificate.gone()

            }
            userCourseStatus == CourseStatus.ENROLLED -> {
                binding.noDataTV.gone()
                binding.cvCertificate.gone()
            }
            else -> {
                binding.noDataTV.visibleView(binding.cvTest.visibility == View.GONE)
                binding.cvCertificate.gone()

            }
        }

        binding.btStart.isEnabled = userCourseStatus == CourseStatus.ENROLLED
        //        binding.btStart.isEnabled = true
        binding.btStart.setBtnEnabled(userCourseStatus == CourseStatus.ENROLLED)
        //        binding.btStart.setBtnEnabled(true)


    }

    fun saveFileLocally(

    ): Uri {

        val uri: Uri

        val filePrefix = "Self_"
        val fileExtn = ".pdf"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues()
            contentValues.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                filePrefix + System.currentTimeMillis() + fileExtn
            )
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS + "/Self"
            )
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())



            uri = requireContext().contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            )!!
        } else {

            var dest = File(File(app_folder), filePrefix + fileExtn)
            var fileNo = 0
            while (dest.exists()) {
                fileNo++
                dest = File(File(app_folder), filePrefix + fileNo + fileExtn)

            }
            uri = FileProvider.getUriForFile(
                requireContext(),
                "${BuildConfig.APPLICATION_ID}.provider",
                dest
            )


        }

        return uri
    }

    override fun onApiRetry(apiCode: String) {

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (value) {
            ApiEndPoints.API_PURCHASE_COURSE -> {
                viewsHandling(viewModel.courseData.value?.userCourseStatus)

            }
        }
    }

}


