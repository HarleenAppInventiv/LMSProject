package com.selflearningcoursecreationapp.ui.bottom_more.payments.details

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
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
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class PaymentDetailsFragment : BaseFragment<FragmentPaymentDetailsBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_payment_details
    }

    private val viewModel: PaymentDetailVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setHasOptionsMenu(true)
    }

    private fun initUi() {
        baseActivity.setToolbar(
            showToolbar = true,
            title = "",
            toolbarColor = R.attr.secondaryScreenBgColor
        )
        arguments?.let {
            viewModel.orderData = it.getParcelable("orderData")

        }

        viewModel.orderData?.let { data ->
            baseActivity.hideProgressBar()
            setPaymentState(data)
            setOrderData(data)
            setCourseData(data.course)

        }
        binding.llCourse.priceG.gone()
        binding.llCourse.bookmarkTimeG.gone()
        binding.llCourse.btBuy.gone()
        binding.llCourse.tvDuration.gone()


        binding.llCourse.tvCoin.gone()
//        binding.llCourse.tvDuration.visible()
        binding.llCourse.view.gone()
        binding.llCourse.ivBookmark.gone()
        binding.llCourse.ivPreview.setImageResource(R.drawable.ic_course_dummy)

    }

    private fun setCourseData(data: CourseData?) {
        binding.llCourse.apply {
            tvName.text = data?.courseTitle
            tvAuthor.text = data?.getCreatedName()
            ivCertification.text = data?.categoryName
            ivPreview.loadImage(data?.courseBannerUrl, R.drawable.ic_home_default_banner, 0)
            ivLang.text = data?.languageName
            cvOngoing.setOnClickListener {
                findNavController().navigate(
                    R.id.action_paymentDetailsFragment_to_courseDetailsFragment,
                    bundleOf("courseId" to data?.courseId)
                )
            }
            btBuy.setOnClickListener {
                findNavController().navigate(
                    R.id.action_paymentDetailsFragment_to_courseDetailsFragment,
                    bundleOf("courseId" to data?.courseId)
                )
            }

            val msg =
                SpanUtils.with(baseActivity, "10m left in lesson").endPos(8).themeColor()
                    .getSpanString()
//
            binding.llCourse.tvDuration.setSpanString(msg)
            binding.llCourse.tvRating.text = data?.averageRating
            binding.llCourse.tvProgress.text = "0% COMPLETED"
            binding.llCourse.pbProgress.progress = 0

        }
    }

    private fun setOrderData(data: OrderData) {
        binding.tvRewards.text =
            String.format("-%s %s", data?.currencySymbol, data?.course?.courseFee)
        binding.tvTransId.text = data?.transactionId
        binding.tvTransId.visibleView(!data.transactionId.isNullOrEmpty())
        binding.tvTransTitle.visibleView(!data.transactionId.isNullOrEmpty())
        binding.tvOrderId.text = data?.orderId
        binding.tvTime.text = Calendar.getInstance().time.getStringDate("MMM dd, yyyy HH:mm:ss")
    }

    private fun setPaymentState(data: OrderData) {
        when (data.status) {
            PaymentStatus.SUCCESS -> {
                binding.ivHeader.setImageResource(R.drawable.ic_payment_done)
//                binding.ivHeader.loadGif(R.raw.in_progress)

                binding.tvStatus.text = baseActivity.getString(R.string.success)
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        baseActivity,
                        R.color.accent_color_2FBF71
                    )
                )
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        baseActivity,
                        R.color.price_bg_color
                    )
                )

//                binding.llCourse.btBuy.visible()
//                binding.llCourse.btBuy.text = baseActivity.getString(R.string.resume)
//                binding.llCourse.btBuy.icon =
//                    ContextCompat.getDrawable(baseActivity, R.drawable.ic_resume)
            }
            PaymentStatus.FAILED -> {
                binding.ivHeader.setImageResource(R.drawable.ic_payment_failed)
                binding.tvStatus.text = baseActivity.getString(R.string.failed)
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        baseActivity,
                        R.color.accent_color_fc6d5b
                    )
                )
                binding.tvStatus.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(baseActivity, R.color.red_light))
//                binding.llCourse.btBuy.visible()
//                binding.llCourse.btBuy.text = baseActivity.getString(R.string.buy_now)
//                binding.llCourse.btBuy.icon = null
            }
            PaymentStatus.IN_PROGRESS -> {
                binding.ivHeader.loadGif(R.raw.in_progress)
//                binding.ivHeader.setImageResource(R.drawable.ic_alert_title)
                binding.tvStatus.text = baseActivity.getString(R.string.in_progress)
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        baseActivity,
                        R.color.accent_color_FFB800
                    )
                )
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        baseActivity,
                        R.color.yellow_light
                    )
                )
//                binding.llCourse.btBuy.visible()
//                binding.llCourse.btBuy.text = baseActivity.getString(R.string.resume)
//                binding.llCourse.btBuy.setBtnEnabled(false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }


    override fun onApiRetry(apiCode: String) {

    }


}