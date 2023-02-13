package com.selflearningcoursecreationapp.ui.bottom_more

import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.selflearningcoursecreationapp.BuildConfig
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.models.CategoryResponse
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.MODSTATUS
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
        callMenu()
    }

    private fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.handleClick = this

        binding.tvVersion.text =
            baseActivity.getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME
        /*  val spannable: SpannableString = SpannableString(baseActivity.getString(R.string.settings))
          spannable.setSpan(baseActivity.localeSpan?: LocaleSpan(Locale.getDefault()), 0, spannable.length, 0)
          binding.tvSettings.text=spannable */
    }


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.tv_settings -> {
                    findNavController().navigateTo(R.id.action_moreFragment_to_settingsFragment)
                }
                R.id.tv_payments -> {
                    findNavController().navigateTo(R.id.action_moreFragment_to_paymentsFragment)
                }
                R.id.tv_cards -> {
                    showCommingSoonDialog(getString(R.string.coming_soon))
                }
                R.id.tv_moderator -> {
                    viewModel.switchMod()

                }
                R.id.tv_practice_accent -> {
                    findNavController().navigateTo(R.id.action_moreFragment_to_practiceAccentFragment)

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
                } else if (exception.statusCode == HTTPCode.DATA_MISSING_VALIDATION) {
                    hideLoading()
                    val gson = Gson()
                    var jsonObject = gson.toJsonTree(exception.data).asJsonObject
                    val category = Gson().fromJson(
                        jsonObject, CategoryResponse::class.java
                    )
                    var categoryNames = ""
                    var filteredList = category.list?.filter { it.status == MODSTATUS.PENDING }
                    if (filteredList?.size ?: 0 > 0) {
                        filteredList?.forEach { categoryNames = categoryNames + ", " + it.name }
                        underReviewPopUP(categoryNames.substring(2))
                    }

                } else if (exception.statusCode == 403) {
                    hideLoading()
                    accountBlockedPopup()
                } else {
                    super.onException(isNetworkAvailable, exception, apiCode)
                }
            }
        }
    }

    private fun accountBlockedPopup() {

        CommonAlertDialog.builder(baseActivity)
            .hideNegativeBtn(true)
            .title(baseActivity.getString(R.string.moderator_access_removed))
            .description(getString(R.string.account_blocked_desc_text))
            .getCallback {


            }.notCancellable(false).icon(R.drawable.ic_help_desk)
            .build()
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

    private fun underReviewPopUP(category: String) {
        var firstHalf = SpanUtils.with(
            baseActivity,
            baseActivity.getString(R.string.moderator_under_review_desc) + " "

        ).startPos(34).endPos(54).isBold().getSpanString()

        var secondHalf = SpanUtils.with(
            baseActivity,
            String.format(
                baseActivity.getString(R.string.moderator_under_review_desc_2),
                category
            )

        ).startPos(0).endPos((category?.length ?: 0).plus(2)).isBold().getSpanString()

        var finslSpannedString = SpannableString(TextUtils.concat(firstHalf, secondHalf))
        CommonAlertDialog.builder(baseActivity)
            .hideNegativeBtn(true)
            .title(getString(R.string.under_review))
            .spannedText(
                finslSpannedString
            )
            .getCallback {


            }.notCancellable(false).icon(R.drawable.ic_under_review)
            .build()
    }

}