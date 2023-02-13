package com.selflearningcoursecreationapp.ui.my_bank

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentMyBankBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.utils.HandleClick
import org.koin.androidx.viewmodel.ext.android.viewModel


class MyBankFragment : BaseFragment<FragmentMyBankBinding>(), HandleClick, BaseDialog.IDialogClick,
    BaseBottomSheetDialog.IDialogClick {

    val viewModel: BankAccountVM by viewModel()
    private lateinit var adapter: MyBankAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callMenu()
        init()

    }

    fun init() {
        binding.cvAddBank.visible()
        binding.myBank = this
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getBankAccountListing()
        viewModel.maxBankAddedCount()
        viewModel.bankAccountListingLiveData.observe(viewLifecycleOwner) {

            if (!it.isNullOrEmpty()) {
                adapter = MyBankAdapter(it) { payload ->
                    val data = AccountOptionsBottomSheet()
                    data.arguments = bundleOf(
                        "id" to payload.id,
                        "isPrimeyChecked" to payload.isPrimaryChecked
                    )
                    data.setOnDialogClickListener(this)
                    data.show(childFragmentManager, "")

                }
                binding.recyclerBankDetail.adapter = adapter
                binding.recyclerBankDetail.visible()
                showLog("BANK", "size >>> " + (it.size < viewModel.maxBankCount.value ?: 0))
                binding.btnAddBank.visibleView(viewModel.bankAccountListingLiveData.value?.size ?: 0 != 0 && it.size < viewModel.maxBankCount.value ?: 0)
                binding.cvAddBank.gone()


            } else {
                binding.cvAddBank.visible()

            }

        }

        viewModel.maxBankCount.observe(viewLifecycleOwner, Observer {
            binding.btnAddBank.visibleView(viewModel.bankAccountListingLiveData.value?.size ?: 0 != 0 && viewModel.bankAccountListingLiveData.value?.size ?: 0 < it ?: 0)

        })

    }


    override fun getLayoutRes() = R.layout.fragment_my_bank
    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.btn_add_bank_acc -> {
                    findNavController().navigateTo(R.id.action_myBankFragment_to_addBankDetailsFragment)
                }

                R.id.btn_add_bank -> {
                    findNavController().navigateTo(R.id.action_myBankFragment_to_addBankDetailsFragment)
                }
            }
        }
    }


    override fun onDialogClick(vararg items: Any) {
        viewModel.getBankAccountListing()
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)

    }


}