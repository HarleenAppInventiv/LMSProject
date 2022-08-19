package com.selflearningcoursecreationapp.ui.moderator

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentBecomeModertorBinding
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.user.PreferenceData
import com.selflearningcoursecreationapp.ui.profile.profileDetails.ProfileDetailViewModel
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.ImagePickUtils
import com.selflearningcoursecreationapp.utils.PREFERENCES
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class BecomeModeratorFragment : BaseFragment<FragmentBecomeModertorBinding>(), (Int, Int) -> Unit,
        (String?) -> Unit,
    HandleClick {
    private val imagePickUtils: ImagePickUtils by inject()
    private var position = -1
    private val viewModel: ProfileDetailViewModel by viewModel()


    private val uploadDocumentsAdapter by lazy {
        AdapterBecomeMode(viewModel.certificateFiles, this)

    }


    override fun getLayoutRes(): Int {

        return R.layout.fragment_become_modertor

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.rvQualificationDoc.adapter = uploadDocumentsAdapter
        binding.handleClick = this
        if (viewModel.certificateFiles.isEmpty()) {
            viewModel.certificateFiles.add(ModeCertificate("", File("")))

        }

        activityResultListener()
    }

    override fun onApiRetry(apiCode: String) {


    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_UPLOAD_MODERATOR_DOC -> {
                uploadDocumentsAdapter.addPayload(viewModel.certificateFiles)
            }
            ApiEndPoints.API_ADD_MODERATOR -> {
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
//                        baseActivity.goToModeratorActivity()

                        findNavController().navigate(R.id.action_global_homeFragment)

                    }.notCancellable().icon(R.drawable.ic_under_review)
                    .build()

            }

        }
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.ev_choose_category -> {

                    val data = PreferenceData(
                        categorySingleSelection = true,
                        screenType = PREFERENCES.SCREEN_SELECT,
                        title = baseActivity.getString(R.string.category),
                        type = PREFERENCES.TYPE_CATEGORY,
                        selectedValues = viewModel.categoryData.map { it.id }
                    )

                    findNavController().navigate(
                        R.id.action_global_preferencesFragment,
                        bundleOf(
                            "preferenceData" to data
                        )
                    )


                }
                R.id.ev_choose_course_language -> {
                    val data = PreferenceData(
                        languageSingleSelection = false,
                        screenType = PREFERENCES.SCREEN_SELECT,
                        title = baseActivity.getString(R.string.language),
                        type = PREFERENCES.TYPE_LANGUAGE,
                        selectedValues = viewModel.laguageData.map { it.id }
                    )

                    findNavController().navigate(
                        R.id.action_global_preferencesFragment,
                        bundleOf(
                            "preferenceData" to data
                        )
                    )

                }
                R.id.bt_enroll -> {

                    if (checkValidations()) {
                        viewModel.addModerator()
                    }


                }
            }
        }
    }

    private fun checkValidations(): Boolean {


        if (viewModel.categoryData.isEmpty()) {
            showToastLong(getString(R.string.please_select_category))
            return false
        }
        if (viewModel.laguageData.isEmpty()) {
            showToastLong(getString(R.string.please_select_language))
            return false
        } else if (viewModel.certificateFiles.size == 1) {
            showToastLong(getString(R.string.please_add_documents))

            return false
        }
        return true

    }

    override fun invoke(p1: String?) {
        viewModel.position = position
        val file = File(Uri.parse(p1).path ?: "")
        viewModel.uploadModeDoc(file, position)


    }

    override fun invoke(p1: Int, type: Int) {

        position = p1
        if (type == 1) {
            imagePickUtils.openDocs(
                baseActivity,
                this,
                registry = baseActivity.activityResultRegistry
            )
        } else {

            viewModel.certificateFiles.removeAt(position)
            uploadDocumentsAdapter.addPayload(viewModel.certificateFiles)


        }
    }

    private fun activityResultListener() {
        setFragmentResultListener(
            "preferenceData"
        ) { _, bundle ->
            val value = bundle.getParcelableArrayList<CategoryData>("data")
            val type = bundle.getInt("type")

            when (type) {
                PREFERENCES.TYPE_LANGUAGE -> {
                    viewModel.laguageData.clear()
                    binding.evChooseCourseLanguage.setText(value?.map { it.name }
                        ?.joinToString(","))

                    if (value != null) {
                        viewModel.laguageData.addAll(value)
                    }

                }
                PREFERENCES.TYPE_CATEGORY -> {
                    viewModel.categoryData.clear()
                    binding.evChooseCategory.setText(value?.map { it.name }?.joinToString(","))
                    if (value != null) {
                        viewModel.categoryData.addAll(value)
                    }
                }
            }

        }
    }


}

