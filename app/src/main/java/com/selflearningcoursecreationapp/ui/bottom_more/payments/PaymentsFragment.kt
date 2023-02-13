package com.selflearningcoursecreationapp.ui.bottom_more.payments

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPaymentsBinding
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.ui.bottom_more.payments.earnings.EarningFragment
import com.selflearningcoursecreationapp.ui.bottom_more.payments.new_request.NewRequestBottomSheet
import com.selflearningcoursecreationapp.ui.bottom_more.payments.purchase.PurchaseFragment
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.convertPaiseToRs
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class PaymentsFragment : BaseFragment<FragmentPaymentsBinding>(),
    BaseDialog.IDialogClick {
    private var currencySymbol: String? = ""
    private val viewModel: PaymentsVM by viewModel()
    var remainingWallet = 0f

    override fun getLayoutRes(): Int {
        return R.layout.fragment_payments
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }


    private fun initUi() {
        binding.viewmodel = viewModel
        binding.btHistory.setOnClickListener {
//            showCommingSoonDialog(getString(R.string.coming_soon))
            findNavController().navigateTo(R.id.action_paymentsFragment_to_walletFragment)
        }
        binding.btRequest.setOnClickListener {
//            showCommingSoonDialog(getString(R.string.coming_soon))
            if (remainingWallet.toDouble().isNullOrZero()) {
                showToastShort(getString(R.string.you_dont_have_any_earning))
            } else {
                NewRequestBottomSheet().apply {
                    arguments = bundleOf(
                        "remainingWallet" to this@PaymentsFragment.remainingWallet,
                        "currencySymbol" to this@PaymentsFragment.currencySymbol,
                    )
                    setOnDialogClickListener(this@PaymentsFragment)

                }.show(childFragmentManager, "")
            }
        }

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        setRemainingWalletObserver()

        viewModel.remainingWalletBalance()

        val list = ArrayList<Fragment>()

        list.add(EarningFragment())
        list.add(PurchaseFragment())


        val nameArray = arrayListOf(getString(R.string.my_earning), getString(R.string.my_purchase))
        binding.viewpager.adapter =
            ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)

        TabLayoutMediator(binding.tlHeader, binding.viewpager) { tab, position ->
            tab.text = nameArray[position]


        }.attach()
        binding.tlHeader.setSelectedTabIndicatorColor(ThemeUtils.getAppColor(baseActivity))

        binding.tlHeader.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.customView as TextView?)?.typeface = Typeface.DEFAULT_BOLD
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.customView as TextView?)?.typeface = Typeface.DEFAULT

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("main", "")

            }
        })
    }

    private fun setRemainingWalletObserver() {
        viewModel.remainingWalletBalanceLiveData.observe(viewLifecycleOwner) {

            remainingWallet = (it.remainingWalletBalance?.convertPaiseToRs())?.toFloat() ?: 0f
            currencySymbol = it.currencySymbol

            if ((it.remainingWalletBalance ?: 0f) < 0f)
                binding.tvRewards.text = String.format(
                    "- %s%s",
                    it.currencySymbol,
                    Math.abs((it.remainingWalletBalance ?: 0f))?.convertPaiseToRs()
                )
            else
                binding.tvRewards.text = String.format(
                    "%s %s",
                    it.currencySymbol,
                    it.remainingWalletBalance?.convertPaiseToRs()
                )

            binding.tvEarned.text =
                String.format("%s %s", it.currencySymbol, it.totalEarning?.convertPaiseToRs())
            binding.tvExpense.text =
                String.format("%s %s", it.currencySymbol, it.totalExpenses?.convertPaiseToRs())
        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {

            }
        }
    }

}