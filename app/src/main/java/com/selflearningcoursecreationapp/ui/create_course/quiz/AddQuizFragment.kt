package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.databinding.FragmentAddQuizBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.ui.dialog.UploadImageOptionsDialog
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceBottomDialog
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

@SuppressLint("NotifyDataSetChanged")
class AddQuizFragment : BaseFragment<FragmentAddQuizBinding>(), BaseAdapter.IViewClick,
    BaseBottomSheetDialog.IDialogClick, View.OnClickListener {
    private var adapter: AddQuizViewAdapter? = null

    //    private var sectionChildPosition = -1
    private val viewModel: AddQuizVM by viewModel()
    private var bundleArgs: AddQuizFragmentArgs? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_quiz
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        adapter?.notifyDataSetChanged()
        adapter = null
        getBundleData()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        if (viewModel.quizData.quizId.isNullOrZero() && viewModel.isQuiz) {
            addQuestion()
        } else if (!viewModel.isQuiz) {
            addQuestion()
        } else setAdapter()


        binding.btAdd.setOnClickListener(this)
        binding.btContinue.setOnClickListener(this)

    }

    private fun getBundleData() {
        arguments?.let {
            bundleArgs = AddQuizFragmentArgs.fromBundle(it)
//            sectionChildPosition = bundleArgs?.childPosition ?: -1
            viewModel.isQuiz = bundleArgs?.lessonArgs?.isQuiz ?: true
            viewModel.quizData.courseId = bundleArgs?.lessonArgs?.courseId ?: 0
            if (viewModel.isQuiz) {
                viewModel.quizData.sectionId =
                    bundleArgs?.lessonArgs?.sectionId ?: 0
                if (bundleArgs?.lessonArgs?.type == Constant.CLICK_EDIT) {
                    viewModel.quizData.lectureId = bundleArgs?.lessonArgs?.lectureId ?: 0
                    viewModel.quizData.quizId = bundleArgs?.lessonArgs?.quizId ?: 0
                    viewModel.getQuizQuestions()
                    baseActivity.setToolbar(baseActivity.getString(R.string.update_quiz))
                }

            } else {
                if (!bundleArgs?.lessonArgs?.quizId.isNullOrZero()) {
                    viewModel.quizData.assessmentId = bundleArgs?.lessonArgs?.quizId

                    viewModel.getQuizQuestions()
                    baseActivity.setToolbar(baseActivity.getString(R.string.update_assessment))
                } else {
                    baseActivity.setToolbar(baseActivity.getString(R.string.add_assessment))

                }
            }

        }
    }

    private fun setAdapter(doScroll: Boolean = false) {
        val count: String =
            baseActivity.getQuantityString(
                R.plurals.question_quantity,
                viewModel.quizData.list?.size
            )
        binding.tvSelectedValue.text = count

        binding.btContinue.setBtnDisabled(viewModel.quizData.list?.size ?: 0 != 0)
        binding.btContinue.isEnabled = (viewModel.quizData.list?.size ?: 0 != 0)
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = AddQuizViewAdapter(viewModel.quizData.list ?: ArrayList())
            binding.rvSections.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
        if (doScroll) {
            binding.rvSections.smoothScrollToPosition((viewModel.quizData.list?.size ?: 1) - 1)
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            viewModel.adapterPosition = items[1] as Int
            when (type) {
                DialogType.CLICK_QUIZ_TYPE -> {
                    SingleChoiceBottomDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.CLICK_QUIZ_TYPE,
                            "title" to this@AddQuizFragment.baseActivity.getString(
                                R.string.select_ques_type
                            ),
                            "id" to viewModel.getListData(viewModel.adapterPosition)?.questionType
                        )
                        setOnDialogClickListener(this@AddQuizFragment)
                    }.show(childFragmentManager, "")
                }

                DialogType.CLICK_QUIZ_OPTION -> {
                    viewModel.childPosition = items[2] as Int
//                    if (viewModel.getListData(viewModel.adapterPosition)?.questionType == QUIZ.DRAG_DROP) {
//                        UploadImageOptionsDialog().apply {
//                            arguments = bundleOf("type" to DialogType.CLICK_DRAG_OPTION)
//
//                            setOnDialogClickListener(this@AddQuizFragment)
//                        }.show(childFragmentManager, "")
//                    } else {
                    AddQuizOptionDialog().apply {

                        setOnDialogClickListener(this@AddQuizFragment)
                    }.show(childFragmentManager, "")
//                    }
                }
                DialogType.QUIZ_ANSWER -> {
                    QuizMarkAnsDialog().apply {
                        arguments = bundleOf(

                            "quizData" to viewModel.quizData,
                            "position" to viewModel.adapterPosition

                        )
                        setOnDialogClickListener(this@AddQuizFragment)
                    }.show(childFragmentManager, "")
                }

                Constant.CLICK_SAVE -> {


                    viewModel.saveQuestionValidation()
                }
                DialogType.CLICK_PORTRAIT_QUES -> {
                    UploadImageOptionsDialog().apply {
                        arguments = bundleOf("type" to DialogType.CLICK_PORTRAIT_QUES)
                        setOnDialogClickListener(this@AddQuizFragment)
                    }.show(childFragmentManager, "")
                }

                DialogType.CLICK_BANNER -> {
                    UploadImageOptionsDialog().apply {
                        arguments = bundleOf("type" to DialogType.CLICK_BANNER)
                        setOnDialogClickListener(this@AddQuizFragment)
                    }.show(childFragmentManager, "")
                }
                Constant.CLICK_DELETE -> {
                    deleteQuestionFunctionality()
                }
            }
        }

    }

    private fun deleteQuestionFunctionality() {
        CommonAlertDialog.builder(baseActivity)
            .title(baseActivity.getString(R.string.delete_question))
            .description(baseActivity.getString(R.string.delete_ques_alert_text))
            .positiveBtnText(baseActivity.getString(R.string.yes))
            .icon(R.drawable.ic_delete_icon)
            .getCallback {
                if (it) {
                    if (viewModel.getListData(viewModel.adapterPosition)?.questionId.isNullOrZero()) {
                        viewModel.quizData.list?.removeAt(viewModel.adapterPosition)
                        showToastShort(baseActivity.getString(R.string.question_deleted_successfully))
                        setAdapter()

                        if (!viewModel.isQuizTitleAdded() && viewModel.isQuizAdded() && viewModel.quizData.list.isNullOrEmpty()) {
                            viewModel.deleteQuizAssessment()
                        }
                    } else {
                        viewModel.deleteQuestion()
                    }
                }
            }
            .build()
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            when (items[0] as Int) {
                DialogType.CLICK_PORTRAIT_QUES -> {

                    viewModel.getListData(viewModel.adapterPosition)?.questionImage =
                        items[1] as String

                    if (viewModel.isQuizAdded()) {

                        viewModel.uploadImage(
                            File(items[1] as String),
                            if (viewModel.isQuiz) MediaType.QUIZ_QUES else MediaType.ASSESSMENT_QUES,
                            viewModel.adapterPosition,
                            0
                        )
                    } else {
                        viewModel.currentAction = 2
                        viewModel.addQuizAssessment()
                    }
                    setAdapter()
                }

                DialogType.CLICK_BANNER -> {

                    viewModel.getListData(viewModel.adapterPosition)?.questionImage =
                        items[1] as String

                    if (viewModel.isQuizAdded()) {

                        viewModel.uploadImage(
                            File(items[1] as String),
                            if (viewModel.isQuiz) MediaType.QUIZ_QUES else MediaType.ASSESSMENT_QUES,
                            viewModel.adapterPosition,
                            0
                        )
                    } else {
                        viewModel.currentAction = 2
                        viewModel.addQuizAssessment()
                    }
                    setAdapter()
                }
                DialogType.CLICK_DRAG_OPTION -> {
                    val image = items[1] as String
                    viewModel.getListData(viewModel.adapterPosition)?.optionList?.get(
                        viewModel.childPosition
                    )?.image = image
                    if (viewModel.isQuizAdded()) {
                        viewModel.uploadImage(
                            File(image),
                            if (viewModel.isQuiz) MediaType.QUIZ_OPTION else MediaType.ASSESSMENT_OPTION,
                            viewModel.adapterPosition,
                            viewModel.childPosition
                        )
                    } else {
                        viewModel.currentAction = 3
                        viewModel.addQuizAssessment()
                    }
                }
                DialogType.CLICK_QUIZ_TYPE -> {
                    viewModel.quizData.list?.set(
                        viewModel.adapterPosition, QuizQuestionData(
                            true,
                            questionTypeTitle = (items[1] as SingleChoiceData).title,
                            isEnabled = true
                        ).apply {
                            title = ""
                            optionList = ArrayList()
                            questionType = (items[1] as SingleChoiceData).id

                        })
                    adapter?.notifyItemChanged(viewModel.adapterPosition)
                }
                DialogType.CLICK_QUIZ_OPTION -> {
                    val selectedType = items[1] as Int
                    val image = items[2] as String
                    setImageTextOption(selectedType, image)
                }


                DialogType.QUIZ_ANSWER -> {
                    val hashMap = items[1] as HashMap<String, ArrayList<QuizOptionData>>
                    setQuizAns(hashMap)
                }
            }
        }
    }

    private fun setQuizAns(hashMap: HashMap<String, ArrayList<QuizOptionData>>) {
        viewModel.getListData(viewModel.adapterPosition)?.ansMarked = true
        when (viewModel.getListData(viewModel.adapterPosition)?.questionType) {
            QUIZ.MATCH_COLUMN -> {
                hashMap.keys.forEachIndexed { index, s ->
                    hashMap[s]?.filter { it.isSelected == true }?.forEach {
                        viewModel.getListData(viewModel.adapterPosition)?.optionList?.get(
                            index
                        )?.ansId =
                            it.id
                    }
                }
            }
            else -> {
                val key = hashMap.map { it.key }[0]
                viewModel.getListData(viewModel.adapterPosition)?.optionList?.forEach { option ->
                    option.isSelected = !(hashMap[key]
                        ?.filter { it.isSelected == true && it.id == option.id }
                        ?.isNullOrEmpty() ?: true)
                }
            }
        }
//        isAllAnsMarked()

        adapter?.notifyItemChanged(viewModel.adapterPosition)
    }

    private fun setImageTextOption(selectedType: Int, image: String) {
        if (selectedType == 1) {
            viewModel.getListData(viewModel.adapterPosition)?.optionList?.get(
                viewModel.childPosition
            )?.option1 = image
            adapter?.notifyItemChanged(viewModel.adapterPosition)

        } else {
            viewModel.getListData(viewModel.adapterPosition)?.optionList?.get(
                viewModel.childPosition
            )?.image = image
            if (viewModel.isQuizAdded()) {
                viewModel.uploadImage(
                    File(image),
                    if (viewModel.isQuiz) MediaType.QUIZ_OPTION else MediaType.ASSESSMENT_OPTION,
                    viewModel.adapterPosition,
                    viewModel.childPosition
                )
            } else {
                viewModel.currentAction = 3
                viewModel.addQuizAssessment()
            }
        }
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            "${ApiEndPoints.API_ADD_QUIZ}/get", "${ApiEndPoints.API_ADD_ASSESSMENT}/get" -> {
                adapter?.notifyDataSetChanged()
                adapter = null
                setAdapter()
            }
            "${ApiEndPoints.API_ADD_QUIZ}/add" -> {
//                bundleArgs?.sectionData?.get(bundleArgs?.adapterPosition ?: 0)?.lessonList?.add(
//                    ChildModel(
//                        viewModel.quizData.lectureId,
//                        mediaType = MediaType.QUIZ,
//                        quizId = viewModel.quizData.quizId,
//                        lectureStatusId = LectureStatus.IN_PROCESS
//                    )
//                )
//                bundleArgs?.sectionData?.get(bundleArgs?.adapterPosition ?: 0)
//                    ?.notifyPropertyChanged(BR.uploadLesson)

                viewModel.continueAction()
//                sectionChildPosition = (bundleArgs?.sectionData?.get(
//                    bundleArgs?.adapterPosition ?: 0
//                )?.lessonList?.size ?: 0) - 1

                adapter?.notifyDataSetChanged()
                adapter = null
                setAdapter()
            }
            "${ApiEndPoints.API_ADD_ASSESSMENT}/add" -> {
                bundleArgs?.lessonArgs?.courseData?.assessmentId = viewModel.quizData.assessmentId
                adapter?.notifyDataSetChanged()
                adapter = null
                setAdapter()
                viewModel.continueAction()
            }
            ApiEndPoints.API_ADD_QUIZ_IMAGE, ApiEndPoints.API_ADD_ASSESSMENT_IMAGE -> {
                adapter?.notifyItemChanged(viewModel.adapterPosition)
            }
            "${ApiEndPoints.API_ADD_QUIZ_QUESTION}/add", "${ApiEndPoints.API_ADD_ASSESSMENT_QUESTION}/add" -> {

//                if (viewModel.isQuiz) {
//                    isAllAnsMarked()
//                }
                showToastShort((value as BaseResponse<QuizData>).message)
                setAdapter()
                binding.btAdd.visible()
                QuizMarkAnsDialog().apply {
                    arguments = bundleOf(

                        "quizData" to viewModel.quizData,
                        "position" to viewModel.adapterPosition

                    )
                    setOnDialogClickListener(this@AddQuizFragment)
                }.show(childFragmentManager, "")
            }
            "${ApiEndPoints.API_ADD_QUIZ_QUESTION}/delete" -> {
                if (viewModel.quizData.list.isNullOrEmpty()) {
//                    deleteQuizLecture()
                    findNavController().navigateUp()
                } else {
//                    if (viewModel.isQuiz) {
//                        isAllAnsMarked()
//                    }
                    showToastShort((value as BaseResponse<QuizData>).message)
                    setAdapter()
                    binding.btAdd.visible()
                }
            }
            "${ApiEndPoints.API_ADD_ASSESSMENT_QUESTION}/delete" -> {
                if (viewModel.quizData.list.isNullOrEmpty()) {
                    deleteAssessment()
                    findNavController().navigateUp()
                } else {
//                    if (viewModel.isQuiz) {
//                        isAllAnsMarked()
//                    }
                    showToastShort((value as BaseResponse<QuizData>).message)
                    setAdapter()
                    binding.btAdd.visible()
                }
            }
//            ApiEndPoints.API_LECTURE_DELETE + "/delete" -> {
//                deleteQuizLecture()
//            }
            ApiEndPoints.API_ADD_ASSESSMENT + "/delete" -> {
                deleteAssessment()
            }
        }
    }

    private fun deleteAssessment() {
        bundleArgs?.lessonArgs?.courseData?.assessmentId = null
        bundleArgs?.lessonArgs?.courseData?.assessmentFreezeContent = false
        bundleArgs?.lessonArgs?.courseData?.assessmentMandatory = false
        bundleArgs?.lessonArgs?.courseData?.assessmentName = ""
    }

//    private fun deleteQuizLecture() {
//        if (!sectionChildPosition.isNullOrNegative()) {
//            bundleArgs?.sectionData?.get(
//                bundleArgs?.adapterPosition ?: 0
//            )?.lessonList?.removeAt(sectionChildPosition)
//        } else {
//            bundleArgs?.sectionData
//                ?.get(bundleArgs?.adapterPosition ?: 0)
//                ?.lessonList?.removeAt(
//                    bundleArgs?.sectionData?.get(
//                        bundleArgs?.adapterPosition ?: 0
//                    )?.lessonList?.size ?: 0
//                )
//        }
//    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.bt_add -> {
                viewModel.quizData.let {
                    val errorId = it.isSingleQuizValid()
                    if (errorId == 0) {
                        addQuestion()
                    } else {
                        showToastShort(baseActivity.getString(errorId))
                    }
                }

            }
            R.id.bt_continue -> {
                viewModel.quizData.let {
                    val errorId = it.isQuizValid()
                    if (errorId == 0) {
                        val bundle = arguments ?: Bundle()
                        bundle.putParcelable("quizData", viewModel.quizData)
                        findNavController().navigateTo(
                            R.id.action_addQuizFragment_to_quizSettingsFragment,
                            bundle
                        )
                    } else {
                        showToastShort(baseActivity.getString(errorId))
                    }
                }

            }
        }
    }

    private fun addQuestion() {
        if (viewModel.quizData.list == null) {
            viewModel.quizData.list = ArrayList()
        }
        viewModel.quizData.list?.forEach {
            it.isExpanded = false
        }
        viewModel.quizData.list?.add(
            QuizQuestionData(
                true,
                questionTypeTitle = baseActivity.getQuizTypeTitle(QUIZ.MULTIPLE_CHOICE),
                isEnabled = true
            ).apply {
                title = ""
                optionList = ArrayList()
                questionType = QUIZ.MULTIPLE_CHOICE

            }
        )

        setAdapter(true)
//        showToastShort("New question is added.")

    }

    override fun onResume() {
        super.onResume()
        baseActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }


//    private fun isAllAnsMarked() {
//        bundleArgs?.sectionData?.get(bundleArgs?.adapterPosition ?: 0)?.lessonList?.apply {
//            val pos = if (bundleArgs?.childPosition.isNullOrNegative()) {
//                size - 1
//            } else {
//                bundleArgs?.childPosition!!
//            }
//            get(pos).totalQuizQues = viewModel.quizData.list?.size
//
//            get(pos).allAnsMarked =
//                viewModel.quizData.list?.filter { !it.isAnsMarked() }.isNullOrEmpty()
//        }
//    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        when (exception.statusCode) {
            HTTPCode.CO_AUTHOR_ACCESS_DENIED -> {
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
            }
            HTTPCode.CONTENT_DELETED -> {
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
            }
            else -> {
                super.onException(isNetworkAvailable, exception, apiCode)

            }
        }

    }

    fun onClickBack(isOpen: Boolean = true) {
//        if (bundleArgs?.lessonArgs?.type == Constant.CLICK_ADD) {
//            setFragmentResult(
//                "onLessonBack",
//                bundleOf("isDialogOpen" to isOpen)
//            )
//        }
//        findNavController().popBackStack()
        if (viewModel.isQuizAdded()) {
            if (bundleArgs?.lessonArgs?.type == Constant.CLICK_ADD) {
                setFragmentResult(
                    "onLessonBack",
                    bundleOf("isDialogOpen" to isOpen)
                )
            }
            findNavController().popBackStack()
        } else {
            confirmationPopUp(isOpen)
        }
    }

    private fun confirmationPopUp(isOpen: Boolean) {

        CommonAlertDialog.builder(baseActivity)
            .hideNegativeBtn(false)
            .positiveBtnText(baseActivity.getString(R.string.yes))
            .negativeBtnText(baseActivity.getString(R.string.no))
            .title(baseActivity.getString(R.string.alerte))
            .description(getString(R.string.are_you_do_not_want_to_save_lesson))
            .getCallback {
                if (it) {
                    if (bundleArgs?.lessonArgs?.type == Constant.CLICK_ADD) {
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
}