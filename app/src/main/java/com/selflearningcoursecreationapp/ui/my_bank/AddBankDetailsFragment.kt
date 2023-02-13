package com.selflearningcoursecreationapp.ui.my_bank

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.databinding.FragmentAddBankDetailsBinding
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddBankDetailsFragment : BaseFragment<FragmentAddBankDetailsBinding>(),
    BaseDialog.IDialogClick {

    val viewModel: BankAccountVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callMenu()
        init()

    }

    fun init() {
        binding.bankAccount = viewModel
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_ADD_BANK_ACCOUNT -> {
                showToastShort(getString(R.string.account_saved_successfully))
                findNavController().popBackStack()
            }
        }
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        super.onException(isNetworkAvailable, exception, apiCode)
//        showToastShort(baseActivity.getString(R.string.entered_details_are_not_correct))
    }

    override fun getLayoutRes() = R.layout.fragment_add_bank_details


    override fun onDialogClick(vararg items: Any) {

    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


}