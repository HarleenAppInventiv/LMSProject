package com.selflearningcoursecreationapp.ui.moderator

import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.databinding.FragmentBecomeModertorBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.setBtnEnabled
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.CategoryResponse
import com.selflearningcoursecreationapp.models.user.PreferenceData
import com.selflearningcoursecreationapp.ui.profile.profileDetails.ProfileDetailViewModel
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.PermissionUtilClass
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.lang.Runnable
import java.util.regex.Pattern


class BecomeModeratorFragment : BaseFragment<FragmentBecomeModertorBinding>(), (Int, Int) -> Unit,
        (String?) -> Unit,
    HandleClick {
    private val imagePickUtils: ImagePickUtils by inject()
    private var position = -1
    private val viewModel: ProfileDetailViewModel by viewModel()
    var regex = Pattern.compile("[$&+,:;=\\\\?@#|'<>^*()%!]")


    private val uploadDocumentsAdapter by lazy {
        AdapterBecomeMode(viewModel.certificateFiles, this)

    }


    override fun getLayoutRes(): Int {

        return R.layout.fragment_become_modertor

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callMenu()
        init()
    }


    fun init() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.rvQualificationDoc.adapter = uploadDocumentsAdapter
        binding.handleClick = this
        binding.viewModel = viewModel
        if (viewModel.certificateFiles.isEmpty()) {
            viewModel.certificateFiles.add(ModeCertificate("", File("")))

        }
        viewModel.isAllDataSelected.observe(viewLifecycleOwner) {
//            if (it == true) {
            binding.btEnroll.setBtnEnabled(it)
            binding.btEnroll.isEnabled = it
//            }

        }


        activityResultListener()
    }

    override fun onApiRetry(apiCode: String) {


    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_UPLOAD_MODERATOR_DOC -> {
                showToastLong(getString(R.string.document_added_succesfully))

//                uploadDocumentsAdapter.addPayload(viewModel.certificateFiles)


                uploadDocumentsAdapter?.notifyDataSetChanged()

                binding.nestedScroll.postDelayed(Runnable {
//                                            binding.parentNSV.scroll
                    binding.nestedScroll.fullScroll(
                        View.FOCUS_DOWN
                    )
                }, 10)

            }
            ApiEndPoints.API_ADD_MODERATOR -> {

                var categoryNames = ""

                (value as BaseResponse<CategoryResponse>).resource?.list?.forEach {
                    categoryNames = categoryNames + ", " + it.name
                }



                categoryNames.substring(2).let { category ->

                    var firstHalf = SpanUtils.with(
                        baseActivity,
                        baseActivity.getString(R.string.moderator_under_review_desc) + " "

                    ).startPos(34).endPos(54).isBold().getSpanString()

                    var secondHalf = SpanUtils.with(
                        baseActivity,
                        category?.let {
                            String.format(
                                baseActivity.getString(R.string.moderator_under_review_desc_2),
                                it
                            )
                        }

                    ).startPos(0).endPos((category?.length ?: 0).plus(2)).isBold()
                        .getSpanString()

                    var finslSpannedString =
                        SpannableString(TextUtils.concat(firstHalf, secondHalf))


                    CommonAlertDialog.builder(baseActivity)
                        .hideNegativeBtn(true)
                        .title(baseActivity.getString(R.string.under_review))
                        .spannedText(
                            finslSpannedString
                        )
                        .notCancellable(false).icon(R.drawable.ic_under_review)
                        .getCallback {

                            baseActivity.onBackPressed()


                        }.build()

                } ?: kotlin.run {
                    CommonAlertDialog.builder(baseActivity)
                        .hideNegativeBtn(true)
                        .title(getString(R.string.under_review))
                        .spannedText(
                            SpanUtils.with(
                                baseActivity,
                                getString(R.string.we_have_recieved_your_request_your_become_a_moderator_request_is_under_review_we_will_let_you_know_when_its_done)
                            ).startPos(34).endPos(54).isBold().getSpanString()
                        )
                        .getCallback {

                            baseActivity.onBackPressed()


                        }.notCancellable(false).icon(R.drawable.ic_under_review)
                        .build()
                }


            }

        }
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.ev_choose_category -> {

                    val data = PreferenceData(
                        categorySingleSelection = false,
                        screenType = PREFERENCES.SCREEN_SELECT,
                        title = baseActivity.getString(R.string.category),
                        type = PREFERENCES.TYPE_CATEGORY,
                        selectedValues = viewModel.categoryData.map { it.id }
                    )

                    findNavController().navigateTo(
                        R.id.action_global_preferencesFragment,
                        bundleOf(
                            "preferenceData" to data
                        )
                    )


                }
                R.id.ev_choose_course_language -> {
                    val data = PreferenceData(
                        languageSingleSelection = false,
                        isSignIncluded = true,
                        screenType = PREFERENCES.SCREEN_SELECT,
                        title = baseActivity.getString(R.string.language),
                        type = PREFERENCES.TYPE_LANGUAGE,
                        from = true,
                        selectedValues = viewModel.laguageData.map { it.id }
                    )

                    findNavController().navigateTo(
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
        showLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val uri = async { call(p1.toString()) }.await()
            showLog("URLL", msg = uri)
            updateUI(uri)
        }


    }

    private suspend fun call(p1: String): String {
        return try {

            FileUtils.getPath(
                SelfLearningApplication.applicationContext(),
                Uri.parse(p1.toString())
            ) ?: p1
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    private suspend fun updateUI(uri: String) {
        withContext(Main) {
            viewModel.position = position
            val file = File(uri)
            if (isFileLessThan5MB(file)) {
                showToastShort(baseActivity.getString(R.string.file_limit_alert_text_five_mb))
            } else if (regex.matcher(file.name).find()) {
                showToastShort(baseActivity.getString(R.string.plz_remove_special))
            } else {
                viewModel.uploadModeDoc(file, position)
            }
            hideLoading()
        }
    }


    private fun isFileLessThan5MB(file: File): Boolean {
        val maxFileSize = 5 * 1024 * 1024
        val l = file.length()
        val fileSize = l.toString()
        val finalFileSize = fileSize.toInt()
        return finalFileSize >= maxFileSize
    }


    override fun invoke(p1: Int, type: Int) {

        position = p1
        if (type == 1) {
            PermissionUtilClass.builder(baseActivity).requestExternalStorage()
                .requestCode(Permission.DOC)
                .getCallBack { b, perms, i ->
                    if (b) {
                        imagePickUtils.openDocs(

                            baseActivity,
                            this,
                            true
                        )
                    } else {
                        baseActivity.handlePermissionDenied(perms)
                    }

                }
                .build()


        } else {
            showToastShort(getString(R.string.doc_removed))
            viewModel.certificateFiles.removeAt(position)
            viewModel.observeData()
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
            viewModel.observeData()

        }
    }

    fun checkSpecialCharacter(fileName: String): Boolean {

        return fileName.contains("~") ||
                fileName.contains("@") ||
                fileName.contains("#") ||
                fileName.contains("|") ||
                fileName.contains("^") ||
                fileName.contains("$") ||
                fileName.contains("%") ||
                fileName.contains("&") ||
                fileName.contains("!") ||
                fileName.contains("*")
    }
}

