package com.selflearningcoursecreationapp.ui.bottom_more

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class MoreFragment :
    BaseFragment<com.selflearningcoursecreationapp.databinding.FragmentMoreBinding>(),
    HandleClick {
    private val viewModel: MoreFragmentVM by viewModel()
    override fun getLayoutRes(): Int {
        return R.layout.fragment_more
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setHasOptionsMenu(true)
    }

    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.handleClick = this
        /*  val spannable: SpannableString = SpannableString(baseActivity.getString(R.string.settings))
          spannable.setSpan(baseActivity.localeSpan?: LocaleSpan(Locale.getDefault()), 0, spannable.length, 0)
          binding.tvSettings.text=spannable */
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.tv_settings -> {
                    findNavController().navigate(R.id.action_moreFragment_to_settingsFragment)
                }
                R.id.tv_payments -> {
                    findNavController().navigate(R.id.action_moreFragment_to_paymentsFragment)
                }
                R.id.tv_cards -> {
                    findNavController().navigate(R.id.action_moreFragment_to_cardsFragment)
                }
                R.id.tv_moderator -> {
//                    findNavController().navigate(R.id.action_moreFragment_to_becomeModeratorFragment)
                    viewModel.switchMod()

                }
                R.id.tv_practice_accent -> {

//                    findNavController().navigate(R.id.action_moreFragment_to_assessmentFragment)

                }

            }
        }

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_SWITCH_TO_MOD -> {
                alreadyModPopUp()
//                baseActivity.goToModeratorActivity()
            }
        }
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (apiCode) {
            ApiEndPoints.API_SWITCH_TO_MOD -> {
                if (exception.statusCode == 400) {
                    hideLoading()
                    findNavController().navigateTo(R.id.action_moreFragment_to_becomeModeratorFragment)
                } else if (exception.statusCode == 424) {
                    hideLoading()
                    underReviewPopUP()
                } else {
                    super.onException(isNetworkAvailable, exception, apiCode)
                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }

    private fun alreadyModPopUp() {
        CommonAlertDialog.builder(baseActivity)
            .title(baseActivity.getString(R.string.already_a_moderator))
            .description(baseActivity.getString(R.string.already_moderator_desc_text))
//            spannedText(
//                SpanUtils.with(
//                    baseActivity,
//                    baseActivity.getString(R.string.already_moderator_desc_text)
//                ).startPos(60).isBold().getSpanString()
//            )
            .positiveBtnText(baseActivity.getString(R.string.okay))
            .hideNegativeBtn(true)
            .icon(R.drawable.ic_become_moderator_alert)
            .getCallback {
                if (it) {
                }
            }.build()
    }

    private fun underReviewPopUP() {
        CommonAlertDialog.builder(baseActivity)
            .hideNegativeBtn(true)
            .title("Under review")
            .spannedText(
                SpanUtils.with(
                    baseActivity,
                    "We’ve received your request. Your “become a moderator” request is under review we’ll let you know when it’s done."
                ).startPos(34).endPos(54).isBold().getSpanString()
            )
            .getCallback {


            }.notCancellable().icon(R.drawable.ic_under_review)
            .build()
    }

}