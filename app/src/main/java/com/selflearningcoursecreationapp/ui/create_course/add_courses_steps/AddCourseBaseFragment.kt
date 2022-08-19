package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentAddCourseBaseBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.create_course.add_assessment.AssessmentFragment
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.AddSectionOrLectureFragment
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.create_course.co_author.InviteCoAuthorDialog
import com.selflearningcoursecreationapp.ui.create_course.review.CourseReviewFragment
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.ui.preferences.ScreenSlidePagerAdapter
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddCourseBaseFragment : BaseFragment<FragmentAddCourseBaseBinding>(),
    HandleClick {
    private var authorMenu: MenuItem? = null
    private var deleteMenu: MenuItem? = null

    private val viewModel: AddCourseViewModel by viewModel()
    private var isFirstTime = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {

        arguments?.let {
            viewModel.courseData.value?.courseId = it.getInt("courseId")
            isFirstTime += 1
        }
//        viewModel.courseData.value?.courseId = 1890
//        isFirstTime += 1
//        arguments?.let {
//            viewModel.courseData.value?.courseId = it.getInt("courseId")
//            isFirstTime += 1
//        }
        activityResultListener()
        initViewPager()
        setHasOptionsMenu(true)
//        binding.lifecycleOwner = parentFragment
        binding.viewModel = viewModel
        binding.handleClick = this
//        if (!viewModel.courseData.value?.courseId.isNullOrZero() && isFirstTime == 1) {
//            viewModel.getCourseDetail()
//            isFirstTime += 1
//        }

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        if (!viewModel.masterData.isDataAdded()) {
            viewModel.getMasterData()
        }
        initClickListener()


    }

    private fun initClickListener() {

        binding.btEdit.setOnClickListener {
            when (binding.vpAddCourses.currentItem) {

                3 -> {
                    findNavController().navigate(
                        AddCourseBaseFragmentDirections.actionAddCourseBaseFragmentToAddQuizFragment(
                            viewModel.courseData.value,
                            false
                        )
                    )
                }
                4 -> {
                    viewModel.getCourseSections()
                    binding.vpAddCourses.currentItem = 2
                }

            }
        }

        binding.btContinue.setOnClickListener {
            when (binding.vpAddCourses.currentItem) {
                0 -> viewModel.step1Validation()
                1 -> viewModel.step2Validation()
                2 -> viewModel.step3Validation()
                3 -> viewModel.step4Validation()
                4 -> viewModel.step5Validation()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
        authorMenu = menu.findItem(R.id.add_co_author)
        deleteMenu = menu.findItem(R.id.action_delete)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_delete)
        item.icon?.colorFilter = PorterDuffColorFilter(
            ThemeUtils.getAppColor(baseActivity),
            PorterDuff.Mode.SRC_IN
        )

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_co_author -> {
                openCoAuthorDialog()
                true
            }
            R.id.action_read -> {
                true
            }

            R.id.action_delete -> {
                CommonAlertDialog.builder(baseActivity)
                    .title(baseActivity.getString(R.string.delete_assessment))
                    .description(baseActivity.getString(R.string.delete_assessment_alert_text))
                    .positiveBtnText(baseActivity.getString(R.string.yes))
                    .icon(R.drawable.ic_delete_icon)
                    .getCallback {
                        if (it) {
                            viewModel.deleteAssessment()
                        }
                    }
                    .build()
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun getLayoutRes() = R.layout.fragment_add_course_base
    private fun initViewPager() {
        val list = ArrayList<Fragment>()
        val fragList = arrayListOf(
            Step1Fragment(),
            Step2Fragment(),
            AddSectionOrLectureFragment(),
            AssessmentFragment(),
            CourseReviewFragment()
        )

        list.addAll(fragList)


        val adapter = ScreenSlidePagerAdapter(childFragmentManager, list, lifecycle)
        binding.vpAddCourses.adapter = adapter

//binding.vpAddCourses.isSaveEnabled=false
//binding.vpAddCourses.isSaveFromParentEnabled=false
        binding.vpAddCourses.isUserInputEnabled = false
        binding.vpAddCourses.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)


                viewModel.courseData.value =
                    viewModel.courseData.value?.apply { currentPage = position }

                handlePageChangeCallback(position)


            }
        })
    }

    private fun handlePageChangeCallback(position: Int) {
        lifecycleScope.launch {
            delay(200)
            baseActivity.runOnUiThread {

                authorMenu?.isVisible = false
                deleteMenu?.isVisible = false


                handleStepFunctionality()
                binding.group.visibleView(position == 2)
                binding.clBottom.visible()
                binding.btEdit.gone()
                when (position) {
                    0 -> {
                        binding.btContinue.text = baseActivity.getString(R.string.continue_text)
                        baseActivity.setToolbar(baseActivity.getString(R.string.create_course))
                    }
                    1 -> {
                        binding.btContinue.text =
                            if (viewModel.isCreator.value == true) baseActivity.getString(R.string.create_course) else baseActivity.getString(
                                R.string.next
                            )
                        baseActivity.setToolbar(baseActivity.getString(R.string.create_course))

                    }
                    2 -> {
                        authorMenu?.isVisible = viewModel.isCreator.value == true

                        binding.btContinue.text =
                            if (viewModel.isCreator.value == true) baseActivity.getString(R.string.continue_text) else baseActivity.getString(
                                R.string.submit
                            )
                        baseActivity.setToolbar(viewModel.courseData.value?.courseTitle)

                        binding.clBottom.visibleView(!viewModel.courseData.value?.sectionData.isNullOrEmpty())

                    }
                    3 -> {
                        deleteMenu?.isVisible =
                            !viewModel.courseData.value?.assessmentId.isNullOrZero()
                        binding.btContinue.text = baseActivity.getString(R.string.continue_text)
                        binding.btEdit.visibleView(!viewModel.courseData.value?.assessmentId.isNullOrZero())
                        baseActivity.setToolbar(if (viewModel.courseData.value?.assessmentName.isNullOrEmpty()) viewModel.courseData.value?.courseTitle else viewModel.courseData.value?.assessmentName)

                    }
                    4 -> {
                        binding.btEdit.visible()
                        binding.btContinue.text = baseActivity.getString(R.string.submit)

                        baseActivity.setToolbar(baseActivity.getString(R.string.course_content_review))
                    }


                }

                setSectionCount()


            }
        }
    }


    private fun openCoAuthorDialog() {
        InviteCoAuthorDialog().apply {
            arguments = bundleOf("courseId" to viewModel.courseData.value?.courseId)
        }.show(childFragmentManager, "")

    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        viewModel.isCreator.value =
            (viewModel.courseData.value?.createdById == viewModel.userProfile?.id)


        when (apiCode) {

            ApiEndPoints.API_COAUTHOR_INVITATION -> {


                CommonAlertDialog.builder(baseActivity)
                    .title(baseActivity.getString(R.string.submitted_successfully))
                    .description(

                        String.format(
                            baseActivity.getString(R.string.course_coauthor_submitted_desc_text),
                            viewModel.courseData.value?.courseTitle
                        )

                    )
                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .hideNegativeBtn(true)
                    .icon(R.drawable.ic_assessment_submitted)
                    .getCallback {
                        if (it) {
                            (baseActivity as HomeActivity).setSelected(R.id.action_home)
                        }
                    }
                    .build()
            }

            ApiEndPoints.API_CRE_STEP_1 -> {
                (value as BaseResponse<UserProfile>)

//                viewModel.completedStep =
//                    when (viewModel.courseData.value?.completeStep) {
//                        1 -> 1
//                        3 -> 2
//                        7 -> 3
//                        15 -> 4
//                        else -> {1}
//                    }

                binding.vpAddCourses.currentItem += 1


            }
            ApiEndPoints.API_GET_SECTIONS -> {
                if (binding.vpAddCourses.currentItem == 2) {
                    binding.clBottom.visibleView(!viewModel.courseData.value?.sectionData.isNullOrEmpty())
                    setSectionCount()
                }
            }
            ApiEndPoints.API_CRE_STEP_2 -> {
                binding.vpAddCourses.currentItem += 1
            }
            ApiEndPoints.API_ADD_SECTION -> {
                binding.clBottom.visibleView(!viewModel.courseData.value?.sectionData.isNullOrEmpty())

                showToastShort((value as BaseResponse<SectionModel>).message)
                setSectionCount()
            }
            ApiEndPoints.API_ADD_ASSESSMENT + "/delete/true" -> {
                showToastShort((value as BaseResponse<QuizData>).message)
                viewModel.courseData.value?.notifyPropertyChanged(BR.dataEntered)
                handlePageChangeCallback(binding.vpAddCourses.currentItem)
            }
            ApiEndPoints.API_ADD_ASSESSMENT + "/delete/false" -> {
                viewModel.courseData.value?.notifyPropertyChanged(BR.dataEntered)
                handlePageChangeCallback(binding.vpAddCourses.currentItem)
            }

            ApiEndPoints.API_PUBLISH_COURSE -> {

                val desc = String.format(
                    baseActivity.getString(R.string.course_submitted_desc_text),
                    viewModel.courseData.value?.courseTitle
                )
                CommonAlertDialog.builder(baseActivity)
                    .title(baseActivity.getString(R.string.submitted_successfully))
                    .spannedText(
                        SpanUtils.with(baseActivity, desc).startPos(54)
                            .endPos(54 + (viewModel.courseData.value?.courseTitle?.length ?: 0) + 1)
                            .textColor().isBold().getSpanString()
                    )


                    .positiveBtnText(baseActivity.getString(R.string.okay))
                    .hideNegativeBtn(true)
                    .icon(R.drawable.ic_assessment_submitted)
                    .getCallback {
                        if (it) {
                            (baseActivity as HomeActivity).setSelected(R.id.action_home)
                        }
                    }
                    .build()
            }

            ApiEndPoints.VALID_DATA -> {
                binding.vpAddCourses.currentItem += 1
            }
            ApiEndPoints.API_ADD_LECTURE_POST + "/delete" -> {
                showToastShort((value as BaseResponse<*>).message)

            }
            ApiEndPoints.API_MASTER_DATA -> {
                if (!viewModel.courseData.value?.courseId.isNullOrZero() && isFirstTime == 1) {
                    viewModel.getCourseDetail()
                    isFirstTime += 1
                }
            }


        }
    }

    private fun setSectionCount() {
        val count: String =
            baseActivity.getQuantityString(
                R.plurals.section_quantity,
                viewModel.courseData.value?.sectionData?.size ?: 0
            )
        binding.tvSelectedValue.text = count
    }

    private fun activityResultListener() {
        setFragmentResultListener(
            "valueHTML"
        ) { _, bundle ->
            val value = bundle.getString("value")
            val type = bundle.getInt("type")
            showLog("RESULT_LISTENER", "value >> $value")
            showLog("RESULT_LISTENER", "type >> $type")
            when (type) {
                Constant.DESC -> {
                    viewModel.courseData.value?.courseDescription = value ?: ""

                }
                Constant.KEY_TAKEAWAY -> {
                    viewModel.courseData.value?.keyTakeaways = value ?: ""

                }
            }
            viewModel.notifyData()
        }
    }

    fun onClickBack() {


        when (binding.vpAddCourses.currentItem) {
            0 -> {
                findNavController().navigateUp()
            }
            else -> {

                binding.vpAddCourses.currentItem -= 1
            }
        }

    }


    private fun handleStepFunctionality() {
        val current = binding.vpAddCourses.currentItem
        binding.tvFirst.setStepColor(current == 0)
        binding.tvSecond.setStepColor(current == 1)
        binding.tvThird.setStepColor(current == 2)
        binding.tvFourth.setStepColor(current == 3)
        binding.tvFifth.setStepColor(current == 4)

        binding.tvFirstTitle.setTextSelected(0 <= viewModel.completedStep || current == 0)
        binding.tvSecondTitle.setTextSelected(1 < viewModel.completedStep || current == 1)
        binding.tvThirdTitle.setTextSelected(2 < viewModel.completedStep || current == 2)
        binding.tvFourthTitle.setTextSelected(3 < viewModel.completedStep || current == 3)
        binding.tvFifthTitle.setTextSelected(4 < viewModel.completedStep || current == 4)

        binding.ivFirst.visibleView(viewModel.completedStep > 0 && current != 0)
        binding.ivSecond.visibleView(viewModel.completedStep > 1 && current != 1)
        binding.ivThird.visibleView(viewModel.completedStep > 2 && current != 2)
        binding.ivFourth.visibleView(viewModel.completedStep > 3 && current != 3)
        binding.ivFifth.visibleView(viewModel.completedStep > 4 && current != 4)
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.tv_first, R.id.tv_first_title, R.id.iv_first -> {
//                    binding.vpAddCourses.currentItem = 0
                }
                R.id.tv_second, R.id.tv_second_title, R.id.iv_second -> {
//                    viewModel.checkAllSteps()

//                    if (viewModel.completedStep >= 1) {
//                        binding.vpAddCourses.currentItem = 1
//                    }
                }
                R.id.tv_third, R.id.tv_third_title, R.id.iv_third -> {
//                    if (viewModel.completedStep >= 2) {
//                        viewModel.getCourseSections()
//
//                        binding.vpAddCourses.currentItem = 2
//                    }
                }
                R.id.tv_fourth, R.id.tv_fourth_title, R.id.iv_fourth -> {
//                    if (viewModel.completedStep >= 3) {
//                        binding.vpAddCourses.currentItem = 3
//                    }
                }
                R.id.tv_fifth, R.id.tv_fifth_title, R.id.iv_fifth -> {
//                    if (viewModel.completedStep >= 4) {
//                        viewModel.getCourseSections()
//
//                        binding.vpAddCourses.currentItem = 4
//                    }
                }
            }
        }
    }

    override fun onLoading(message: String, apiCode: String?) {
        if (!apiCode.equals(ApiEndPoints.API_GET_KEYWORDS))
            super.onLoading(message, apiCode)
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        if (apiCode == ApiEndPoints.API_PUBLISH_COURSE && exception.statusCode == HTTPCode.DATA_MISSING_VALIDATION) {
            baseActivity.hideProgressBar()
            CommonAlertDialog.builder(baseActivity)
                .title(baseActivity.getString(R.string.alert))
                .description(
                    exception.message
                        ?: baseActivity.getString(R.string.co_author_data_not_added)

                )
                .positiveBtnText(baseActivity.getString(R.string.yes))
                .negativeBtnText(baseActivity.getString(R.string.no))
                .icon(R.drawable.ic_alert_title)
                .getCallback {
                    if (it) {
                        viewModel.publishCourse(true)
                    }
                }
                .build()
        } else {
            super.onException(isNetworkAvailable, exception, apiCode)
        }

    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onResume() {
        super.onResume()
        baseActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

}