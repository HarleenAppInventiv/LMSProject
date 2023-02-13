package com.selflearningcoursecreationapp.ui.create_course.upload_content.docs_text

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentLessonTextBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class LessonTextFragment : BaseFragment<FragmentLessonTextBinding>(), View.OnClickListener {

    //    private var childPosition: Int? = -1
    private var isFirstTime: Boolean = true

    //    private var type: Int? = null
    private val viewModel: TextViewModel by viewModel()
    var textFile = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        callMenu()
    }



    private fun initUI() {


        binding.wvEnterText.setOnClickListener(this)
        binding.evEnterTextEmpty.setOnClickListener(this)
        binding.constEnterText.setOnClickListener(this)


        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.textLesson = viewModel
//        binding.edtDocTitle.setOnTouchListener(this)

        arguments?.let {
            val lessonData = LessonTextFragmentArgs.fromBundle(it)

            viewModel.lessonArgs = lessonData.lessonArgs


        }
        activityResultListener()

        if (viewModel.lessonArgs?.type == Constant.CLICK_EDIT && isFirstTime) {
            binding.btAdd.text = baseActivity.getString(R.string.update_lesson)
            viewModel.getLectureDetail()
            isFirstTime = false
        }


        binding.wvEnterText.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val actionNew =
                    LessonTextFragmentDirections.actionLessonTextFragmentToTextEditorFragment(
                        Constant.DESC,
                        viewModel.textLiveData.value?.textFileText ?: "",
                        0,


                        )

                findNavController().navigateTo(actionNew)
            }
            return@setOnTouchListener true
        }

        binding.wvEnterText.getSettings().apply {
            setLoadWithOverviewMode(true)
            setRenderPriority(WebSettings.RenderPriority.HIGH);
            setEnableSmoothTransition(true);
            setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING)
        }

        binding.wvEnterText.setWebViewClient(object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!viewModel.courseData.value?.keyTakeaways.isNullOrEmpty()) {
                    binding.constEnterText.setPadding(20, 20, 20, 20)
                }
            }
        })

        binding.btAdd.setOnClickListener {


            viewModel.textValidations()

        }

//        if (childPosition != null && childPosition != -1) {
//            binding.btAdd.text = baseActivity.getString(R.string.update_lesson)
////            binding.edtDocTitle.setText(model?.lessonList?.get(childPosition!!)?.lectureContentName)
//        }


    }


    override fun getLayoutRes() = R.layout.fragment_lesson_text


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_ADD_LECTURE_PATCH + "/patch" -> {
                (value as BaseResponse<ChildModel>).resource?.let {
                    it.lectureContentName = binding.edtDocTitle.content()

//                    if (viewModel.model?.lessonList == null) {
//                        viewModel.model?.lessonList = ArrayList()
//                    }

                    if (viewModel.lessonArgs?.type == Constant.CLICK_EDIT/*!childPosition.isNullOrNegative() && viewModel.model?.lessonList?.isNotEmpty() == true*/) {
//                        viewModel.model?.lessonList?.set(childPosition ?: 0, it)

                        showToastLong(baseActivity.getString(R.string.lesson_updated_successfully))
                    } else {
//                        viewModel.model?.lessonList?.add(it)
                        showToastLong(baseActivity.getString(R.string.lesson_saved_successfully))
                    }
                }
//                viewModel.model?.notifyPropertyChanged(BR.uploadLesson)

                findNavController().popBackStack()
            }
            ApiEndPoints.API_CONTENT_UPLOAD -> {
//                viewModel.textLiveData.value?.notifyChange()

            }
            ApiEndPoints.API_GET_LECTURE_DETAIL -> {


                (value as BaseResponse<ChildModel>).resource?.let {
//                    binding.edtText.setLimitedText(Html.fromHtml(it.textFileText).toString(), 9)

                    viewModel?.textLiveData?.value?.textFileText = it.textFileText
                    binding.edtDocTitle.setText(it.lectureTitle)
                    val min = it.lectureContentDuration?.div(60000) ?: 0
                    binding.edtTime.setText(min.toString())
                    viewModel.lectureContentId = it.lectureContentId
                    viewModel.textLiveData.value?.notifyChange()
                    textFile = it.textFileText.toString()

                    binding.evEnterTextEmpty.visibleView(it.textFileText?.length ?: 0 <= 0)
                    binding.wvEnterText.visibleView(it.textFileText?.length ?: 0 > 0)



                    binding.wvEnterText.setWebViewClient(object : WebViewClient() {

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            if (!viewModel.courseData.value?.keyTakeaways.isNullOrEmpty()) {
                                binding.constEnterText.setPadding(20, 20, 20, 20)
                            }
                        }
                    })


                }
            }
        }

    }

    private fun activityResultListener() {
        setFragmentResultListener(
            "valueHTML"
        ) { _, bundle ->
            val value = bundle.getString("value")
            val type = bundle.getInt("type")
            if (type == Constant.DESC) {
                viewModel.textLiveData.value?.textFileText = value ?: ""
                lifecycleScope.launch {
                    delay(200)
                    baseActivity.runOnUiThread {

                        viewModel.textLiveData.value?.notifyChange()
                    }
                }

//                uploadServer()

            }
        }
    }

    private fun uploadServer() {
        viewModel.uploadContent()
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onRetry(apiCode: String, networkError: Boolean, exception: ApiError) {
        if (apiCode == ApiEndPoints.API_GET_LECTURE_DETAIL && exception.statusCode == HTTPCode.NO_CONTENT) {
            CommonAlertDialog.builder(baseActivity)
                .icon(R.drawable.ic_alert_title)
                .apply {

                    description(exception.message ?: "")

                }
                .positiveBtnText(baseActivity.getString(R.string.okay))
                .notCancellable(false)
                .title("")
                .hideNegativeBtn(true)
                .getCallback {
                    baseActivity.onBackPressed()
                }
                .build()
        } else {
            super.onRetry(apiCode, networkError, exception)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (exception.statusCode) {
            HTTPCode.NO_CONTENT -> {
                CommonAlertDialog.builder(baseActivity)
                    .icon(R.drawable.ic_alert_title)
                    .apply {

                        description(baseActivity.getString(R.string.the_content_not_found))

                    }
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .notCancellable(false)
                    .title("")
                    .hideNegativeBtn(true)
                    .getCallback {
                        baseActivity.onBackPressed()
                    }
                    .build()
            }
            HTTPCode.CO_AUTHOR_ACCESS_DENIED -> {
                CommonAlertDialog.builder(baseActivity)
                    .icon(R.drawable.ic_rejected_account)
                    .description(exception.message ?: "")
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .title("")
                    .notCancellable(false)
                    .hideNegativeBtn(true)
                    .getCallback {
                        (baseActivity as HomeActivity).setSelected(R.id.action_home)
                    }
                    .build()
            }
            HTTPCode.FORBIDDEN -> {
                CommonAlertDialog.builder(baseActivity)
                    .icon(R.drawable.ic_alert_title)
                    .description(exception.message ?: "")
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .notCancellable(false)
                    .title("")
                    .hideNegativeBtn(true)
                    .getCallback {
                        (baseActivity as HomeActivity).setSelected(R.id.action_home)
                    }
                    .build()
            }
            HTTPCode.CONTENT_DELETED -> {
                CommonAlertDialog.builder(baseActivity)
                    .icon(R.drawable.ic_alert_title)
                    .apply {

                        description(exception.message ?: "")

                    }
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .title("")
                    .notCancellable(false)
                    .hideNegativeBtn(true)
                    .getCallback {
                        baseActivity.onBackPressed()
                    }
                    .build()
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)

            }
        }

    }

    fun onClickBack(isOpen: Boolean = true) {
//        if (viewModel.lessonArgs?.type == Constant.CLICK_ADD) {
//            setFragmentResult(
//                "onLessonBack",
//                bundleOf("isDialogOpen" to isOpen)
//            )
//        }
//        findNavController().popBackStack()
        confirmationPopUp(isOpen)
    }

    private fun confirmationPopUp(isOpen: Boolean) {

        CommonAlertDialog.builder(baseActivity)
            .hideNegativeBtn(false)
            .title(baseActivity.getString(R.string.alerte))
            .positiveBtnText(baseActivity.getString(R.string.yes))
            .negativeBtnText(baseActivity.getString(R.string.no))
            .description(getString(R.string.are_you_do_not_want_to_save_lesson))
            .getCallback {
                if (it) {
                    if (viewModel.lessonArgs?.type == Constant.CLICK_ADD) {
                        setFragmentResult(
                            "onLessonBack",
                            bundleOf("isDialogOpen" to isOpen)
                        )
                    }
                    findNavController().popBackStack()
                }

            }.notCancellable(false).icon(R.drawable.ic_alert_title)
            .build()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.wv_enter_text, R.id.ev_enter_text_empty, R.id.constEnterText -> {
                val actionNew =
                    LessonTextFragmentDirections.actionLessonTextFragmentToTextEditorFragment(
                        Constant.DESC,
                        viewModel.textLiveData.value?.textFileText ?: "",
                        0,


                        )

                findNavController().navigateTo(actionNew)
            }
        }
    }
}
