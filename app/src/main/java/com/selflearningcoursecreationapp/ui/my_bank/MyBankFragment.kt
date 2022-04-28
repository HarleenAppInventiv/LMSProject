package com.selflearningcoursecreationapp.ui.my_bank

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentMyBankBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.ui.dialog.CheckMailDialog
import com.selflearningcoursecreationapp.utils.HandleClick


class MyBankFragment : BaseFragment<FragmentMyBankBinding>(), HandleClick, BaseDialog.IDialogClick {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        init()

    }

    fun init() {
        binding.cvAddBank.visible()
        binding.recyclerBankDetail.adapter = MyBankAdapter()
        binding.myBank = this
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_read -> {
//                baseActivity.checkAccessibilityService()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun getLayoutRes() = R.layout.fragment_my_bank
    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.btn_add_bank_acc -> {
                    val bundle = bundleOf(
                        "description" to baseActivity.getString(
                            R.string.send_razorpay_link
                        )
                    )
                    CheckMailDialog().apply {
                        arguments = bundle

                        setOnDialogClickListener(this@MyBankFragment)

                    }.show(childFragmentManager, "")
                }
            }
        }
    }


    override fun onDialogClick(vararg items: Any) {
        binding.cvAddBank.gone()
        binding.recyclerBankDetail.visible()
    }


}