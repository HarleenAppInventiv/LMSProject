package com.selflearningcoursecreationapp.ui.create_course.add_courses_steps

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.databinding.FragmentAddCourseBaseNewBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.CourseData
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.course.LessonArgs
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.create_course.co_author.ExistsCoAuthorResponse
import com.selflearningcoursecreationapp.ui.create_course.co_author.InviteCoAuthorDialog
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddCourseBaseNewFragment : BaseFragment<FragmentAddCourseBaseNewBinding>(),
    HandleClick {
    private var authorMenu: MenuItem? = null
    private var deleteMenu: MenuItem? = null
    private var navController: NavController? = null
    private val viewModel: AddCourseViewModel by viewModel()
    private var isFirstTime = 0
    private var fromEdit: Boolean = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        initNavController()

        arguments?.let {
            viewModel.courseData.value?.courseId = it.getInt("courseId")
            if (it.containsKey("courseId")) {
                fromEdit = it.getBoolean("fromEdit")
            }
            isFirstTime += 1
        }/*?:run{
            binding.clParent.visible()
        }*/



        activityResultListener()
        setHasOptionsMenu(true)
        binding.viewModel = viewModel
        binding.handleClick = this
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        if (!viewModel.masterData.isDataAdded()) {
            viewModel.getMasterData()
        }
        initClickListener()


    }

    private fun initNavController() {
        val container = childFragmentManager.findFragmentById(R.id.child_container)
        navController = container?.findNavController()
    }

    private fun initClickListener() {

        binding.btEdit.setOnClickListener {
            when (viewModel.currentPosition) {

                3 -> {
                    val lessonArgs = LessonArgs(
                        courseId = viewModel.courseData.value?.courseId ?: 0,
                        type = Constant.CLICK_EDIT,
                        isQuiz = false,
                        quizId = viewModel.courseData.value?.assessmentId,
                        courseData = viewModel.courseData.value
                    )
                    findNavController().navigateTo(
                        AddCourseBaseNewFragmentDirections.actionAddCourseBaseFragmentToAddQuizFragment(
                            lessonArgs
                        )
                    )
                }
                4 -> {
                    viewModel.getCourseSections()
                    goToFragment(2)
                }

            }
        }

        binding.btContinue.setOnClickListener {

            when (viewModel.currentPosition) {
                0 -> viewModel.step1Validation()
                1 -> viewModel.step2Validation()
                2 -> validateStep3()
                3 -> viewModel.step4Validation()
                4 -> {
                    viewModel.courseData.value?.singleClick = false
                    viewModel.step5Validation()
                }
            }
            lifecycleScope.launch {
                delay(500)
                baseActivity.runOnUiThread {
                    binding.btContinue.isClickable = true

                }
            }
        }
    }

    private fun validateStep3() {

        viewModel.courseData.value?.let {
            val errorId = it.isStep3Verified(
                viewModel.userProfile?.id,
                ignoreEmpty = viewModel.userProfile?.id == it.createdById
            )
            if (errorId == 0) {
                viewModel.completeSteps(2)
                if (viewModel.isCreator.value == true) {
                    viewModel.updateResponseObserver(
                        Resource.Success(
                            true,
                            ApiEndPoints.VALID_DATA
                        )
                    )
                } else {
                    CommonAlertDialog.builder(baseActivity)
                        .title(baseActivity.getString(R.string.alerte))
                        .description(baseActivity.getString(R.string.you_cannot_change))
                        .positiveBtnText(baseActivity.getString(R.string.confirm))
                        .negativeBtnText(baseActivity.getString(R.string.cancel))
                        .icon(R.drawable.ic_alert_title)
                        .getCallback {
                            if (it) {
                                viewModel.hitCoAuthorExitApi()
                            }
                        }
                        .build()

                }
            } else {
                viewModel.updateResponseObserver(Resource.Error(ToastData(errorCode = errorId)))

            }
        }
    }

    private fun goToFragment(step: Int) {
        when (step) {
            0 -> navController?.navigateTo(R.id.step1Fragment)
            1 -> navController?.navigateTo(R.id.step2Fragment)
            2 -> navController?.navigateTo(R.id.addSectionOrLectureFragment2)
            3 -> navController?.navigateTo(R.id.assessmentFragment2)
            4 -> {
                if (viewModel.courseData.value?.enableFields == true)
                    viewModel.getCourseSections()
                navController?.navigateTo(R.id.courseReviewFragment)
            }
        }

        viewModel.currentPosition = step
        viewModel.courseData.value =
            viewModel.courseData.value?.apply { currentPage = step }

        handlePageChangeCallback(step)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
        authorMenu = menu.findItem(R.id.add_co_author)
        deleteMenu = menu.findItem(R.id.action_delete)
        authorMenu?.isVisible = viewModel.isCreator.value == true && viewModel.currentPosition == 2
        deleteMenu?.isVisible =
            !viewModel.courseData.value?.assessmentId.isNullOrZero() && viewModel.currentPosition == 3

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
                if (viewModel.courseData.value?.enableFields == true)
                    viewModel.existsCoAuthorDetails()
//                    openCoAuthorDialog()
                true
            }
            R.id.action_read -> {
                baseActivity.checkAccessibilityService()
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


    override fun getLayoutRes() = R.layout.fragment_add_course_base_new


    private fun handlePageChangeCallback(position: Int) {
        lifecycleScope.launch {
            delay(200)
            baseActivity.runOnUiThread {

                authorMenu?.isVisible = viewModel.isCreator.value == true && position == 2
                deleteMenu?.isVisible = false


                handleStepFunctionality()
                binding.group.visibleView(position == 2)
                binding.clBottom.visible()
                binding.btEdit.gone()
                when (position) {
                    0 -> {
                        binding.btContinue.text = baseActivity.getString(R.string.continue_text)
                        baseActivity.setToolbar(
                            if (fromEdit) baseActivity.getString(R.string.edit_course_small) else baseActivity.getString(
                                R.string.create_course
                            )
                        )
                    }
                    1 -> {
                        binding.btContinue.text =
                            if (viewModel.isCreator.value == true) {
                                if (fromEdit) baseActivity.getString(R.string.update_course) else baseActivity.getString(
                                    R.string.create_course
                                )
                            } else baseActivity.getString(
                                R.string.next
                            )
                        baseActivity.setToolbar(
                            if (fromEdit) baseActivity.getString(R.string.edit_course_small) else baseActivity.getString(
                                R.string.create_course
                            )
                        )

                    }
                    2 -> {

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
                        binding.btEdit.isEnabled = true
                        binding.btEdit.visibleView(!viewModel.courseData.value?.assessmentId.isNullOrZero())
                        baseActivity.setToolbar(if (viewModel.courseData.value?.assessmentName.isNullOrEmpty()) viewModel.courseData.value?.courseTitle else viewModel.courseData.value?.assessmentName)

                    }
                    4 -> {
                        binding.btEdit.visible()
                        binding.btEdit.setSecondaryBtnDisabled(
                            viewModel.courseData.value?.enableFields ?: true
                        )
                        binding.btEdit.isEnabled = viewModel.courseData.value?.enableFields ?: true
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
        handleStepFunctionality()


        when (apiCode) {

            ApiEndPoints.API_EXISTS_COAUTHOR -> {

                var data = (value as BaseResponse<ExistsCoAuthorResponse>)
                when (data.resource?.coAuthorExists) {
                    true -> {

                        val finalSpannedStr = if (data.resource?.email.isNullOrEmpty()) {
                            val desc = String.format(
                                baseActivity.getString(R.string.course_coauthor_exists_desc_text),
                                data.resource?.phone
                            )
                            SpanUtils.with(
                                baseActivity, desc

                            ).startPos(63)
                                .endPos(63.plus(data.resource?.phone?.length ?: 0).plus(2)).isBold()
                                .getSpanString()
                        } else {

                            val firstString = String.format(
                                baseActivity.getString(R.string.course_coauthor_exists_desc_text_2),
                                data.resource?.phone
                            )
                            var firstHalfSpannedStr = SpanUtils.with(
                                baseActivity, firstString

                            ).startPos(63)
                                .endPos(63.plus(data.resource?.phone?.length ?: 0).plus(1)).isBold()
                                .getSpanString()

                            val secondString = String.format(
                                baseActivity.getString(R.string.course_coauthor_exists_desc_text_with_email),
                                data.resource?.email
                            )


                            var secondHalfStr =
                                SpanUtils.with(
                                    baseActivity,
                                    secondString
                                ).startPos(11)
                                    .endPos(11.plus(data.resource?.email?.length ?: 0).plus(2))
                                    .isBold().getSpanString()





                            SpannableString(TextUtils.concat(firstHalfSpannedStr, secondHalfStr))


                        }

                        CommonAlertDialog.builder(baseActivity)
                            .title(baseActivity.getString(R.string.coauthor_already_exists))
                            .spannedText(
                                finalSpannedStr
                            )
                            .positiveBtnText(baseActivity.getString(R.string.proceed))
                            .negativeBtnText(baseActivity.getString(R.string.cancel))
                            .icon(R.drawable.ic_assessment_submitted)
                            .getCallback {
                                if (it) {
                                    openCoAuthorDialog()
                                }
                            }
                            .build()
                    }
                    false -> {
                        openCoAuthorDialog()
                    }
                }


            }

            ApiEndPoints.API_COAUTHOR_INVITATION -> {

                val desc = String.format(
                    baseActivity.getString(R.string.course_coauthor_submitted_desc_text),
                    viewModel.courseData.value?.courseTitle
                )
                CommonAlertDialog.builder(baseActivity)
                    .title(baseActivity.getString(R.string.submitted_successfully))
                    .spannedText(
                        SpanUtils.with(baseActivity, desc).startPos(93)
                            .endPos(93 + (viewModel.courseData.value?.courseTitle?.length ?: 0) + 2)
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

            ApiEndPoints.API_CRE_STEP_1 -> {
                (value as BaseResponse<UserProfile>)

                isFirstTime += 1
                goToFragment(1)


            }
            ApiEndPoints.API_GET_SECTIONS -> {
                if (viewModel.currentPosition == 2) {
                    binding.clBottom.visibleView(!viewModel.courseData.value?.sectionData.isNullOrEmpty())
                    setSectionCount()
                }
            }
            ApiEndPoints.API_CRE_STEP_2 -> {
                goToFragment(2)
            }
            ApiEndPoints.API_ADD_SECTION -> {
                binding.clBottom.visibleView(!viewModel.courseData.value?.sectionData.isNullOrEmpty())

                showToastShort((value as BaseResponse<SectionModel>).message)
                setSectionCount()
            }
            ApiEndPoints.API_ADD_ASSESSMENT + "/delete/true" -> {
                showToastShort((value as BaseResponse<QuizData>).message)
                viewModel.courseData.value?.notifyPropertyChanged(BR.dataEntered)
                handlePageChangeCallback(viewModel.currentPosition)
            }
            ApiEndPoints.API_ADD_ASSESSMENT + "/delete/false" -> {
                viewModel.courseData.value?.notifyPropertyChanged(BR.dataEntered)
                handlePageChangeCallback(viewModel.currentPosition)
            }

            ApiEndPoints.API_PUBLISH_COURSE -> {
                val isUnderProcessing =
                    (value as BaseResponse<CourseData>).statusCode != HTTPCode.SUCCESS
                val desc = String.format(
                    if (isUnderProcessing) baseActivity.getString(R.string.course_submitted_processing_desc_text) else baseActivity.getString(
                        R.string.course_submitted_desc_text
                    ),
                    viewModel.courseData.value?.courseTitle
                )
                CommonAlertDialog.builder(baseActivity)
                    .title(baseActivity.getString(R.string.submitted_successfully))
                    .notCancellable(false)
                    .spannedText(
                        SpanUtils.with(baseActivity, desc).startPos(54)
                            .endPos(54 + (viewModel.courseData.value?.courseTitle?.length ?: 0) + 2)
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
            ApiEndPoints.API_CRE_STEP_1 + "/detail" -> {
//                binding.clParent.visible()
            }
            ApiEndPoints.VALID_DATA -> {

                if (viewModel.isCreator.value == true) {
                    viewModel.getCourseComplete(if (viewModel.completedStep == 3) 4 else 8)
                } else {
                    viewModel.courseData.value?.enableFields = true
                    goToFragment(viewModel.currentPosition + 1)
                }
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
            ApiEndPoints.API_UPLOAD_IMAGE -> {
                showToastShort((value as BaseResponse<ImageResponse>).message)

            }
            ApiEndPoints.API_ADD_SECTION + "/delete" -> {
                showToastShort((value as BaseResponse<SectionModel>).message)

            }
            ApiEndPoints.API_COMPLETE_STATUS -> {
                viewModel.courseData.value?.enableFields = true
                goToFragment(viewModel.currentPosition + 1)
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
        setFragmentResultListener(
            "onLessonBack"
        ) { _, bundle ->
            val value = bundle.getBoolean("isDialogOpen")
            if (value && viewModel.currentPosition == 2) {
                findNavController().navigateTo(
                    R.id.action_addCourseBaseFragment_to_uploadDocOptionsDialog,
                    bundleOf(
                        "courseId" to viewModel.courseData.value?.courseId,
                        "sectionId" to viewModel.getSectionList()
                            ?.get(viewModel.sectionAdapterPosition)?.sectionId
                    )
                )
            }
        }
    }

    fun onClickBack() {

        when (viewModel.currentPosition) {
            0 -> {
                findNavController().navigateUp()
            }
//            2 -> {
//                confirmationPopUp()
//            }
            else -> {
                goToFragment(viewModel.currentPosition - 1)
            }
        }

    }


    private fun handleStepFunctionality() {
        val current = viewModel.currentPosition
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
                    if (isFirstTime == 0) {
                        viewModel.courseData.value?.enableFields = true
                        goToFragment(0)
                    }
                }
                R.id.tv_second, R.id.tv_second_title, R.id.iv_second -> {
                    if (isFirstTime == 0) {
                        viewModel.courseData.value?.enableFields = false
                        goToFragment(1)
                    }

                }
                R.id.tv_third, R.id.tv_third_title, R.id.iv_third -> {
                    if (isFirstTime == 0) {
                        viewModel.courseData.value?.enableFields = false
                        goToFragment(2)
                    }

                }
                R.id.tv_fourth, R.id.tv_fourth_title, R.id.iv_fourth -> {
                    if (isFirstTime == 0) {
                        viewModel.courseData.value?.enableFields = false
                        goToFragment(3)
                    }

                }
                R.id.tv_fifth, R.id.tv_fifth_title, R.id.iv_fifth -> {
                    if (isFirstTime == 0) {
                        viewModel.courseData.value?.enableFields = false
                        goToFragment(4)
                    }

                }
            }
        }
    }

    override fun onLoading(message: String, apiCode: String?) {
        if (!apiCode.equals(ApiEndPoints.API_GET_KEYWORDS))
            super.onLoading(message, apiCode)
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        baseActivity.hideProgressBar()
        if (apiCode == ApiEndPoints.API_PUBLISH_COURSE && exception.statusCode == HTTPCode.NO_MODERATOR_AVAILABLE) {
            CommonAlertDialog.builder(baseActivity)
                .title(baseActivity.getString(R.string.alert))
                .description(
                    exception.message
                        ?: baseActivity.getString(R.string.no_moderator_available)

                )
                .positiveBtnText(baseActivity.getString(R.string.create_ticket))
                .hideNegativeBtn(true)
                .isCloseIconEnabled(true)
                .icon(R.drawable.ic_alert_title)
                .getCallback {
                    if (it) {

                        findNavController().navigateTo(R.id.action_addCourseBaseFragment_to_supportFragment)
                    }
                }
                .build()
        } else if (apiCode == ApiEndPoints.API_PUBLISH_COURSE && exception.statusCode == HTTPCode.DATA_MISSING_VALIDATION) {
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
        } /*else if (apiCode == ApiEndPoints.API_COAUTHOR_INVITATION && exception.statusCode == HTTPCode.UN_SUCESS) {
            CommonAlertDialog.builder(baseActivity)
                .title(baseActivity.getString(R.string.alert))
                .description(
                    exception.message
                        ?: ""

                )
                .positiveBtnText(baseActivity.getString(R.string.okay))
                .hideNegativeBtn(true)
                .notCancellable()
                .icon(R.drawable.ic_alert_title)
                .getCallback {
                    if (it) {
                        (baseActivity as HomeActivity).setSelected(R.id.action_home)

                    }
                }
                .build()
        } */ else if (exception.statusCode == HTTPCode.CO_AUTHOR_ACCESS_DENIED) {
            CommonAlertDialog.builder(baseActivity)
                .icon(R.drawable.ic_rejected_account)
                .description(exception.message ?: "")
                .positiveBtnText(baseActivity.getString(R.string.okay))
                .notCancellable(false)
                .title("")

                .hideNegativeBtn(true)
                .getCallback {
                    (baseActivity as HomeActivity).setSelected(R.id.action_home)
                }
                .build()
        } else if (exception.statusCode == HTTPCode.CONTENT_DELETED) {
            CommonAlertDialog.builder(baseActivity)
                .icon(R.drawable.ic_alert_title)
                .description(exception.message ?: "")
                .positiveBtnText(baseActivity.getString(R.string.okay))
                .notCancellable(false)
                .title("")
                .hideNegativeBtn(true)
                .getCallback {
                    goToFragment(viewModel.currentPosition)
                }
                .build()
        } else if (exception.statusCode == HTTPCode.FORBIDDEN) {
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
        handlePageChangeCallback(viewModel.currentPosition)
    }

    fun refreshData() {
        handlePageChangeCallback(viewModel.currentPosition)
    }

}