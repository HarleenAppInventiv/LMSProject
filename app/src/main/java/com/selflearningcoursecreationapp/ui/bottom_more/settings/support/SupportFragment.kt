package com.selflearningcoursecreationapp.ui.bottom_more.settings.support

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentSupportBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.bottom_more.MoreFragmentVM
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


class SupportFragment : BaseFragment<FragmentSupportBinding>() {
    private val viewModel: MoreFragmentVM by viewModel()
    override fun getLayoutRes() = R.layout.fragment_support

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        callMenu()
    }

    private fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        binding.btEnroll.setOnClickListener {
            if (binding.desc.content().isNullOrEmpty()) {
                showToastShort(getString(R.string.please_enter_reason_for_support))
            } else {
                viewModel.support(binding.desc.content())
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_SUPPORT -> {
                (value as BaseResponse<UserProfile>).let {
                    CommonAlertDialog.builder(baseActivity)
                        .hideNegativeBtn(true)
                        .description(it.message)
                        .notCancellable(false)
                        .getCallback {
                            if (findNavController().currentDestination?.id == R.id.supportFragment2) {
                                (baseActivity as HomeActivity).setSelected(R.id.action_home)
                            } else {
                                findNavController().popBackStack()

                            }

                        }
                        .build()
                }


//                showToastShort(getString(R.string.submit_succesfully))
//                findNavController().popBackStack()
            }
        }
    }


}