package com.selflearningcoursecreationapp.ui.bottom_more.settings.changePassword

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentChangePasswordBinding
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.showHidePassword
import com.selflearningcoursecreationapp.ui.authentication.InitialActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding>() {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_change_password
    }

    private val viewModel: ChangePasswordVM by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding.viewModel = viewModel
        setHasOptionsMenu(true)

        binding.etConfirm.showHidePassword()
        binding.etNew.showHidePassword()
        binding.etOld.showHidePassword()

        binding.tvOld.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.old_password)).endPos(3)
                .isBold().getSpanString()
        )
        binding.tvNew.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.new_password)).endPos(3)
                .isBold().getSpanString()
        )
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
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


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_CHANGE_PASS -> {
                if (viewModel.isLogout.value == true) {
                    lifecycleScope.launch {
                        viewModel.saveUserToken("")
                        viewModel.saveUser(null)
                    }
                    CommonAlertDialog.builder(baseActivity).notCancellable().hideNegativeBtn(true)
                        .description(baseActivity.getString(R.string.password_changed_successfully))
                        .getCallback {
                            baseActivity.startActivity(
                                Intent(
                                    baseActivity,
                                    InitialActivity::class.java
                                )
                            )
                            baseActivity.finish()
                        }.build()


                } else {
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

}