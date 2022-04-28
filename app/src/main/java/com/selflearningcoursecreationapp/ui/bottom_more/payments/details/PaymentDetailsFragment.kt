package com.selflearningcoursecreationapp.ui.bottom_more.payments.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPaymentDetailsBinding
import com.selflearningcoursecreationapp.extensions.gone


class PaymentDetailsFragment : BaseFragment<FragmentPaymentDetailsBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_payment_details
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setHasOptionsMenu(true)
    }

    private fun initUi() {
        binding.llCourse.priceG.gone()
        binding.llCourse.bookmarkTimeG.gone()
        binding.llCourse.btBuy.gone()
        binding.llCourse.tvCoin.gone()
        binding.llCourse.tvDuration.gone()
        binding.llCourse.view.gone()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }


}