package com.selflearningcoursecreationapp.ui.bottom_more.payments.purchase

import android.Manifest
import android.app.DownloadManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentEarningBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.bottom_more.payments.PaymentsVM
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
import org.koin.androidx.viewmodel.ext.android.viewModel

class PurchaseFragment : BaseFragment<FragmentEarningBinding>(), BaseAdapter.IViewClick,
    BaseAdapter.IListEnd {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_earning
    }

    private val viewModel: PaymentsVM by viewModel()
    private var mAdapter: PurchaseAdapter? = null
    var manager: DownloadManager? = null
    private var downloadID: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        callMenu()
    }

    override fun onResume() {
        super.onResume()
        binding.getRoot().requestLayout();
    }

    private fun initUi() {

        mAdapter?.notifyDataSetChanged()
        mAdapter = null
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        setMyPurchasesObserver()

        viewModel.getMyPurchases()


    }

    private fun setMyPurchasesObserver() {
        viewModel.userPurchasesLiveData.observe(viewLifecycleOwner) {
            setAdapter()
        }
    }

    private fun setAdapter() {
        binding.rvList.visibleView(!viewModel.userPurchasesLiveData.value.isNullOrEmpty())
        binding.noDataTV.visibleView(viewModel.userPurchasesLiveData.value.isNullOrEmpty())


        if (viewModel.userPurchasesLiveData.value.isNullOrEmpty()) {
            mAdapter?.notifyDataSetChanged()
            mAdapter = null
        } else {
            mAdapter?.notifyDataSetChanged() ?: kotlin.run {
                mAdapter =
                    PurchaseAdapter(
                        viewModel.userPurchasesLiveData.value ?: ArrayList()
                    )
                binding.rvList.adapter = mAdapter
                mAdapter?.setOnAdapterItemClickListener(this)
                mAdapter?.setOnPageEndListener(this)
            }
        }
    }

    override fun onItemClick(vararg items: Any) {
        var type = items[0] as Int
        var position = items[1] as Int


        when (type) {
            Constant.CLICK_VIEW -> {
                findNavController().navigateTo(
                    R.id.action_paymentsFragment_to_paymentDetailsFragment,
                    bundleOf(
                        "purchaseData" to viewModel.userPurchasesLiveData.value?.get(position)
                    )
                )
            }
            Constant.CLICK_INVOICE -> {
                if (viewModel.userPurchasesLiveData.value?.get(position)?.invoiceURL?.isNotEmpty() == true) {
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
                                baseActivity.downloadPdf(
                                    viewModel.userPurchasesLiveData.value?.get(
                                        position
                                    )?.invoiceURL, "Skillfy Invoice.pdf"
                                )
//                                downloadCertificate(viewModel.userPurchasesLiveData.value?.get(position)?.invoiceURL)

                            } else {
                                baseActivity.handlePermissionDenied(strings)
                            }
                        }.build()


                }
            }
        }
    }


    override fun onApiRetry(apiCode: String) {

    }

    override fun onPageEnd(vararg items: Any) {
        viewModel.getMyPurchases()

    }

}