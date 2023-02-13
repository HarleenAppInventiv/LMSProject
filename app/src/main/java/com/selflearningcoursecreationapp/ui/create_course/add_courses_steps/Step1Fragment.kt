package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentStep1Binding
import com.selflearningcoursecreationapp.extensions.disableCopyPaste
import com.selflearningcoursecreationapp.extensions.getAttrResource
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.ui.dialog.CourseCategoriesOptionDialog
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.ValidationConst
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class Step1Fragment : BaseFragment<FragmentStep1Binding>(), HandleClick,
    BaseBottomSheetDialog.IDialogClick, View.OnTouchListener, View.OnClickListener {
    private val viewModel: AddCourseViewModel by viewModels({ requireParentFragment().requireParentFragment() })
    private var filter: InputFilter? = null
    private lateinit var mainFragment: Fragment
    var isFirstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainFragment = requireParentFragment().requireParentFragment()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    fun init() {
        binding.step1Click = this
        binding.step1 = viewModel

        enablefields()

        binding.evEnterDescription.setPlaceholder(baseActivity.getString(R.string.enter_description))

        binding.evEnterDescription.setEditorFontSize(14)
        binding.evEnterDescription.setEditorFontColor(ThemeUtils.getPrimaryTextColor(baseActivity))



        binding.wvCourseDesc.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!viewModel.courseData.value?.courseDescription.isNullOrEmpty()) {
                    binding.constDesc.setPadding(10, 20, 10, 20)
                }
            }
        }

        binding.wvTakeaway.setWebViewClient(object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!viewModel.courseData.value?.keyTakeaways.isNullOrEmpty()) {
                    binding.constTakeaway.setPadding(10, 20, 10, 20)
                }
            }
        })
        binding.constDesc.setOnClickListener {
            mainFragment.findNavController().navigateTo(
                R.id.textEditorFragment,
                bundleOf(
                    "type" to Constant.DESC,
                    "htmlValue" to (viewModel.courseData.value?.courseDescription ?: ""),
                    "from" to 1
                )
            )
        }
        binding.constTakeaway.setOnClickListener {
            val action =
                AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
                    Constant.KEY_TAKEAWAY,
                    viewModel.courseData.value?.keyTakeaways ?: "",
                    from = 1
                )
            mainFragment.findNavController().navigateTo(action)
        }
//        binding.evEnterTitle.setMaxLines(3)
//        binding.evEnterTitle.setVerticalScrollBarEnabled(true)
//        binding.evEnterTitle.setMovementMethod(ScrollingMovementMethod())


//        binding.evEnterTitle.setOnFocusChangeListener { view, b ->
//            if (b)
//            {
//
//                binding.parentNSV.requestDisallowInterceptTouchEvent(true)
//            }else{
//                binding.parentNSV.requestDisallowInterceptTouchEvent(false)
//
//            }
//        }

        binding.evEnterTitle.setOnTouchListener(this)
        binding.wvCourseDesc.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP && viewModel.isCreator.value == true) {
                mainFragment.findNavController().navigateTo(
                    R.id.textEditorFragment,
                    bundleOf(
                        "type" to Constant.DESC,
                        "htmlValue" to (viewModel.courseData.value?.courseDescription ?: ""),
                        "from" to 1
                    )
                )
            }
            return@setOnTouchListener true
        }

        binding.wvTakeaway.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP && viewModel.isCreator.value == true) {
                val action =
                    AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
                        Constant.KEY_TAKEAWAY,
                        viewModel.courseData.value?.keyTakeaways ?: "",
                        from = 1
                    )
                mainFragment.findNavController().navigateTo(action)
            }
            return@setOnTouchListener true
        }


        binding.evEnterTitle.doAfterTextChanged { textAfter ->
            binding.tvTitleTotalChar.setText(textAfter?.length.toString())
            binding.tvTitleTotalChar.apply {
                if ((textAfter?.length ?: 0) < ValidationConst.MAX_COURSE_TITLE_LENGTH) {
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                } else {
                    setTextColor(
                        context.getAttrResource(R.attr.accentColor_Red)
                    )
                }
            }
        }
    }


    private fun enablefields() {
        if (!viewModel.enableFields) {

        }
    }


    override fun getLayoutRes() = R.layout.fragment_step1

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.ev_choose_course_category -> {
                    CourseCategoriesOptionDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.CATEGORY,
                            "selectedId" to viewModel.courseData.value?.categoryId
                        )
                        setOnDialogClickListener(this@Step1Fragment)
                    }.show(childFragmentManager, "")
                }
                R.id.ev_choose_course_language -> {
                    CourseCategoriesOptionDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.LANGUAGE,
                            "list" to viewModel.masterData.allLanguages?.list,
                            "selectedId" to viewModel.courseData.value?.languageId
                        )
                        setOnDialogClickListener(this@Step1Fragment)
                    }.show(childFragmentManager, "")
                }
                R.id.wv_course_desc, R.id.ev_desc_empty -> {
                    val action =
                        AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
                            Constant.DESC, viewModel.courseData.value?.courseDescription ?: "", 1
                        )
                    mainFragment.findNavController().navigateTo(
                        action
                    )
                }
                R.id.ev_enter_key_takeaway_empty, R.id.wv_takeaway -> {
                    val action =
                        AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToTextEditorFragment(
                            Constant.KEY_TAKEAWAY,
                            viewModel.courseData.value?.keyTakeaways ?: "",
                            from = 1
                        )
                    mainFragment.findNavController().navigateTo(action)
                }
            }
        }
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val value = items[1] as CategoryData
            when (type) {
                DialogType.CATEGORY -> {
                    viewModel.courseData.value?.categoryId = value.id ?: 0
                    binding.evChooseCourseCategory.setText(value.name)
                    viewModel.notifyData()
                }
                DialogType.LANGUAGE -> {
                    viewModel.courseData.value?.languageId = value.id ?: 0
                    binding.evChooseCourseLanguage.setText(value.name)

                }
            }
        }
    }

    override fun onApiRetry(apiCode: String) {
        //handled in AddCourseBaseFragment

    }

    override fun onResume() {
        super.onResume()
        binding.evEnterDescription.disableCopyPaste()

        binding.evChooseCourseCategory.disableCopyPaste()
        binding.evChooseCourseLanguage.disableCopyPaste()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ev_enter_description -> {
                mainFragment.findNavController().navigateTo(
                    R.id.textEditorFragment,
                    bundleOf(
                        "type" to Constant.DESC,
                        "htmlValue" to (viewModel.courseData.value?.courseDescription ?: ""),
                        "from" to 1
                    )
                )
            }
        }
    }


}