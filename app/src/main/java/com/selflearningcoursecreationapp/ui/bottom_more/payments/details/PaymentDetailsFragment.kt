package com.selflearningcoursecreationapp.ui.bottom_more.payments.details

import android.Manifest
import android.app.DownloadManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPaymentDetailsBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.OrderData
import com.selflearningcoursecreationapp.utils.PaymentStatus
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import com.selflearningcoursecreationapp.utils.convertPaiseToRs
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class PaymentDetailsFragment : BaseFragment<FragmentPaymentDetailsBinding>() {
    var manager: DownloadManager? = null
    private var downloadID: Long = 0
    private var invoiceUrl: String? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_payment_details
    }

    private val viewModel: PaymentDetailVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        callMenu()
    }

    private fun initUi() {
        baseActivity.setToolbar(
            showToolbar = true, title = "", toolbarColor = R.attr.secondaryScreenBgColor
        )
        arguments?.let {
            if (it.containsKey("purchaseData")) {
                viewModel.purchaseData = it.getParcelable("purchaseData")
                viewModel.getApiResponse().observe(viewLifecycleOwner, this)
                setPurchasedCourseDetailObserver()

                viewModel.purchaseData?.let { it1 ->
                    viewModel.getPurchasedCourseDetails(
                        it1.courseId ?: 0
                    )
                }
                setPurchaseData()
            } else {
                viewModel.orderData = it.getParcelable("orderData")

                binding.parentSV.visible()
                viewModel.orderData?.let { data ->
                    baseActivity.hideProgressBar()
                    invoiceUrl = data.invoiceURL
                    data.status?.let { it1 -> setPaymentState(it1) }
                    setOrderData(data)
                    setCourseData(data.course)
                    viewModel.courseid = data.course?.courseId ?: 0
                }
            }


        }

        observeInvoiceLiveData()
        binding.llCourse.priceG.gone()
        binding.llCourse.bookmarkTimeG.gone()
        binding.llCourse.btBuy.gone()
        binding.llCourse.tvDuration.gone()


        binding.llCourse.tvCoin.gone()
//        binding.llCourse.tvDuration.visible()
        binding.llCourse.view.gone()
        binding.llCourse.ivBookmark.gone()
        binding.llCourse.ivPreview.setImageResource(R.drawable.ic_home_default_banner)

        binding.btInvoice.setOnClickListener {
//            showCommingSoonDialog(getString(R.string.coming_soon))
//            showToastShort(getString(R.string.coming_soon))

            if (!invoiceUrl.isNullOrEmpty()) {
                downloadInvoice()

            } else {

                viewModel.getPurchasedCourseDetails(viewModel.courseid, false)

            }
        }

    }

    private fun downloadInvoice() {
        PermissionUtilClass.builder(baseActivity)
            .requestPermissions(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.READ_MEDIA_VIDEO,
                    )
                } else {
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            )
            .getCallBack { b, strings, _ ->
                if (b) {
                    baseActivity.downloadPdf(invoiceUrl, "Skillfy Invoice.pdf")
//                    downloadCertificate(invoiceUrl)

                } else {
                    baseActivity.handlePermissionDenied(strings)
                }
            }.build()
    }

    private fun observeInvoiceLiveData() {
        viewModel.invoiceLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it.getContentIfNotHandled()?.let { url ->
                if (url.isNotEmpty()) {
                    invoiceUrl = url
                    downloadInvoice()
                }

            }
        })
    }

    private fun setPurchasedCourseDetailObserver() {
        viewModel.purchasedCourseDetailLiveData.observe(viewLifecycleOwner) {
            it?.let { data ->
                binding.parentSV.visible()
                invoiceUrl = data.invoiceURL
                setCourseData(data.course)

            }
        }
    }


    private fun setPurchaseData() {
        binding.tvRewards.text = String.format(
            "- %s %s",
            viewModel.purchaseData?.currencySymbol,
            viewModel.purchaseData?.amount?.convertPaiseToRs()
        )
        binding.tvTransId.text = viewModel.purchaseData?.transactionId
        binding.tvTransId.visibleView(!viewModel.purchaseData?.transactionId.isNullOrEmpty())
        binding.tvTransTitle.visibleView(!viewModel.purchaseData?.transactionId.isNullOrEmpty())
        binding.tvOrderId.text = viewModel.purchaseData?.orderId
        binding.tvTime.text = viewModel.purchaseData?.createdDate.changeDateFormat(
            "yyyy-MM-dd'T'hh:mm:ss",
            "MMM dd, yyyy HH:mm:ss"
        )

        viewModel.purchaseData?.let { setPaymentState(it.paymentStatus ?: PaymentStatus.SUCCESS) }

    }

    private fun setCourseData(data: CourseData?) {
        binding.llCourse.apply {
            tvName.text = data?.courseTitle
            tvAuthor.text = data?.getCreatedName()
            ivCertification.text = data?.categoryName
            ivPreview.loadImage(data?.courseBannerUrl, R.drawable.ic_home_default_banner, 0)
            ivLang.text = data?.languageName
            cvOngoing.setOnClickListener {
                findNavController().navigateTo(
                    R.id.action_paymentDetailsFragment_to_courseDetailsFragment,
                    bundleOf("courseId" to data?.courseId)
                )
            }
            btBuy.setOnClickListener {
                findNavController().navigateTo(
                    R.id.paymentDetailsFragment, bundleOf("courseId" to data?.courseId)
                )
            }

            binding.llCourse.tvRating.text = data?.averageRating
            binding.llCourse.progressG.visible()


            binding.llCourse.tvProgress.text =
                data?.percentageCompleted.toString() + "% " + context?.getString(R.string.completed)
            binding.llCourse.progressG.visible()
            binding.llCourse.tvDuration.apply {
                visibleView(
                    (data?.percentageCompleted?.toInt()
                        ?: 0) > 0 && (data?.percentageCompleted?.toInt() ?: 0) < 100
                )
                val duration = data?.totalDurationLeft.milliSecToMin().toString()
                val msg = SpanUtils.with(
                    context, "${duration}${context.getString(R.string.m_left_in_course)}"
                ).endPos(duration.length + 9).themeColor().getSpanString()
                binding.llCourse.tvDuration.setSpanString(msg)
//                        text = (list[position].courseDuration?.toInt()
//                            ?: 0.minus(list[position].totalDurationLeft ?: 0)).toString()
            }

//
//            binding.llCourse.pbProgress.apply {
//                max = 100
//                progress = data?.percentageCompleted?.toInt() ?: 0
//            }

            binding.llCourse.pbProgress.apply {

                max = 100
                progress = if ((data?.percentageCompleted ?: 0.0) > 0 && (data?.percentageCompleted
                        ?: 0.0) < 1
                ) 1
                else data?.percentageCompleted?.toInt() ?: 0

            }
            binding.llCourse.pbProgress.setOnTouchListener { _, _ ->
                return@setOnTouchListener true
            }


        }
    }

    private fun setOrderData(data: OrderData) {
        binding.tvRewards.text =
            String.format("-%s %s", data.currencySymbol, data.course?.courseFee)
        binding.tvTransId.text = data.transactionId
        binding.tvTransId.visibleView(!data.transactionId.isNullOrEmpty())
        binding.tvTransTitle.visibleView(!data.transactionId.isNullOrEmpty())
        binding.tvOrderId.text = data.orderId
        binding.tvTime.text = Calendar.getInstance().time.getStringDate("MMM dd, yyyy HH:mm:ss")
    }

    private fun setPaymentState(status: Int) {
        when (status) {
            PaymentStatus.SUCCESS -> {
                binding.ivHeader.setImageResource(R.drawable.ic_payment_done)
//                binding.ivHeader.loadGif(R.raw.in_progress)

                binding.tvStatus.text = baseActivity.getString(R.string.success)
                binding.tvStatus.setTextColor(
                    baseActivity.getAttrResource(R.attr.accentColor_Green)
                )
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(
                    baseActivity.getAttrResource(R.attr.accentColor_GreenTintAlpha)

                )
                binding.btInvoice.visible()

//                binding.llCourse.btBuy.visible()
//                binding.llCourse.btBuy.text = baseActivity.getString(R.string.resume)
//                binding.llCourse.btBuy.icon =
//                    ContextCompat.getDrawable(baseActivity, R.drawable.ic_resume)
            }
            PaymentStatus.FAILED -> {
                binding.ivHeader.setImageResource(R.drawable.ic_payment_failed)
                binding.tvStatus.text = baseActivity.getString(R.string.failed)
                binding.tvStatus.setTextColor(
                    baseActivity.getAttrResource(R.attr.accentColor_Red)

                )
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        baseActivity, baseActivity.getAttrResource(R.attr.accentColor_RedTintAlpha)
                    )
                )
                binding.btInvoice.gone()

//                binding.llCourse.btBuy.visible()
//                binding.llCourse.btBuy.text = baseActivity.getString(R.string.buy_now)
//                binding.llCourse.btBuy.icon = null
            }
            PaymentStatus.IN_PROGRESS -> {
                binding.ivHeader.loadGif(R.raw.in_progress)
//                binding.ivHeader.setImageResource(R.drawable.ic_alert_title)
                binding.tvStatus.text = baseActivity.getString(R.string.in_progress)
                binding.tvStatus.setTextColor(
                    baseActivity.getAttrResource(R.attr.accentColor_Yellow)

                )
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(
                    baseActivity.getAttrResource(R.attr.accentColor_YellowTintAlpha)

                )
                binding.btInvoice.gone()

//                binding.llCourse.btBuy.visible()
//                binding.llCourse.btBuy.text = baseActivity.getString(R.string.resume)
//                binding.llCourse.btBuy.setBtnEnabled(false)
            }
        }
    }


    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

}