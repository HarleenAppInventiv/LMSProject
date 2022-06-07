package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentAddQuizBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.ui.dialog.UploadImageOptionsDialog
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceBottomDialog
import com.selflearningcoursecreationapp.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class AddQuizFragment : BaseFragment<FragmentAddQuizBinding>(), BaseAdapter.IViewClick,
    BaseBottomSheetDialog.IDialogClick, View.OnClickListener {
    private var adapter: AddQuizViewAdapter? = null
    private var sectionChildPosition = -1
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
//            viewModel.addQuiz()
        } else if (!viewModel.isQuiz) {
            addQuestion()
//            viewModel.addAssessment()
        } else setAdapter()


        binding.btAdd.setOnClickListener(this)
        binding.btContinue.setOnClickListener(this)

    }

    private fun getBundleData() {
        arguments?.let {
            bundleArgs = AddQuizFragmentArgs.fromBundle(it)
            sectionChildPosition = bundleArgs?.childPosition ?: -1
            viewModel.isQuiz = bundleArgs?.isQuiz ?: true
            viewModel.quizData.courseId = bundleArgs?.courseData?.courseId ?: 0
            if (viewModel.isQuiz) {
                viewModel.quizData.sectionId =
                    bundleArgs?.sectionData?.get(bundleArgs?.adapterPosition ?: 0)?.sectionId ?: 0
                if (!bundleArgs?.childPosition.isNullOrNegative()) {
                    viewModel.quizData.lectureId =
                        bundleArgs?.sectionData?.get(
                            bundleArgs?.adapterPosition ?: 0
                        )?.lessonList?.get(bundleArgs?.childPosition ?: 0)?.lectureId
                            ?: 0
                    viewModel.quizData.quizId =
                        bundleArgs?.sectionData?.get(
                            bundleArgs?.adapterPosition ?: 0
                        )?.lessonList?.get(bundleArgs?.childPosition ?: 0)?.quizId
                            ?: 0
                    viewModel.getQuizQuestions()
                    baseActivity.setToolbar(baseActivity.getString(R.string.update_quiz))
                }
            } else {
                if (!bundleArgs?.courseData?.assessmentId.isNullOrZero()) {
                    viewModel.quizData.assessmentId = bundleArgs?.courseData?.assessmentId

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
                    AddQuizOptionDialog().apply {

                        setOnDialogClickListener(this@AddQuizFragment)
                    }.show(childFragmentManager, "")
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


                    viewModel.saveQuestionValidation(viewModel.adapterPosition)
                }

                DialogType.CLICK_BANNER -> {
                    UploadImageOptionsDialog().apply {
                        arguments = bundleOf("type" to DialogType.CLICK_BANNER)
                        setOnDialogClickListener(this@AddQuizFragment)
                    }.show(childFragmentManager, "")
                }
                Constant.CLICK_DELETE -> {
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
                                    viewModel.deleteQuestion(viewModel.getListData(viewModel.adapterPosition))
                                }
                            }
                        }
                        .build()
                }
            }
        }

    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            when (type) {
                DialogType.CLICK_BANNER -> {

                    viewModel.getListData(viewModel.adapterPosition)?.questionImage =
                        items[1] as String

                    if (viewModel.isQuizAdded()) {

                        viewModel.uploadImage(
                            File(items[1] as String),
                            if (viewModel.isQuiz) MEDIA_TYPE.QUIZ_QUES else MEDIA_TYPE.ASSESSMENT_QUES,
                            viewModel.adapterPosition,
                            0
                        )
                    } else {
                        viewModel.currentAction = 2
                        viewModel.addQuizAssessment()
                    }
                    setAdapter()
                }
                DialogType.CLICK_QUIZ_TYPE -> {
//                    viewModel.getListData(viewModel.adapterPosition)?.questionType =
//                        (items[1] as SingleChoiceData).id
//                    viewModel.getListData(viewModel.adapterPosition)?.questionTypeTitle =
//                        baseActivity.getQuizTypeTitle(
//                            viewModel.getListData(viewModel.adapterPosition)?.questionType ?: 1
//                        )
//                    viewModel.getListData(viewModel.adapterPosition)?.optionList?.forEach {
//                        it.quizType = viewModel.getListData(viewModel.adapterPosition)?.questionType
//                    }
                    viewModel.quizData.list?.set(viewModel.adapterPosition, QuizQuestionData(
                        true,
                        questionTypeTitle = (items[1] as SingleChoiceData).title,
                        isEnabled = true
                    ).apply {
                        title = ""
                        optionList = ArrayList<QuizOptionData>()
                        questionType = (items[1] as SingleChoiceData).id

                    })
                    adapter?.notifyItemChanged(viewModel.adapterPosition)
//                    setAdapter()
//                    list[adapterPosition]
                }
                DialogType.CLICK_QUIZ_OPTION -> {
                    val selectedType = items[1] as Int
                    if (selectedType == 1) {
                        viewModel.getListData(viewModel.adapterPosition)?.optionList?.get(
                            viewModel.childPosition
                        )?.option1 = items[2] as String
                        adapter?.notifyItemChanged(viewModel.adapterPosition)

                    } else {
                        viewModel.getListData(viewModel.adapterPosition)?.optionList?.get(
                            viewModel.childPosition
                        )?.image = items[2] as String
                        if (viewModel.isQuizAdded()) {
                            viewModel.uploadImage(
                                File(items[2] as String),
                                if (viewModel.isQuiz) MEDIA_TYPE.QUIZ_OPTION else MEDIA_TYPE.ASSESSMENT_OPTION,
                                viewModel.adapterPosition,
                                viewModel.childPosition
                            )
                        } else {
                            viewModel.currentAction = 3
                            viewModel.addQuizAssessment()
                        }
                    }
                }


                DialogType.QUIZ_ANSWER -> {
                    val hashMap = items[1] as HashMap<String, ArrayList<QuizOptionData>>
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
                            val key = hashMap.map { it.key }.get(0)
                            viewModel.getListData(viewModel.adapterPosition)?.optionList?.forEach { option ->
                                option.isSelected = !(hashMap.get(key)
                                    ?.filter { it.isSelected == true && it.id == option.id }
                                    ?.isNullOrEmpty() ?: true)
                            }
                        }
                    }
                    isAllAnsMarked()

                    adapter?.notifyItemChanged(viewModel.adapterPosition)
                }
            }
        }
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_ADD_QUIZ, ApiEndPoints.API_ADD_ASSESSMENT -> {
                adapter?.notifyDataSetChanged()
                adapter = null
                setAdapter()
            }
            "${ApiEndPoints.API_ADD_QUIZ}/add" -> {
                bundleArgs?.sectionData?.get(bundleArgs?.adapterPosition ?: 0)?.lessonList?.add(
                    ChildModel(
                        viewModel.quizData.lectureId,
                        mediaType = MEDIA_TYPE.QUIZ,
                        quizId = viewModel.quizData.quizId
                    )
                )
                bundleArgs?.sectionData?.get(bundleArgs?.adapterPosition ?: 0)
                    ?.notifyPropertyChanged(BR.uploadLesson)

                viewModel.continueAction()
                sectionChildPosition = (bundleArgs?.sectionData?.get(
                    bundleArgs?.adapterPosition ?: 0
                )?.lessonList?.size ?: 0) - 1
//                sectionChildPosition =
//                    bundleArgs?.sectionData?.get(bundleArgs?.adapterPosition ?: 0)?.lessonList?.size
//                        ?: -1

                adapter?.notifyDataSetChanged()
                adapter = null
                setAdapter()
            }
            "${ApiEndPoints.API_ADD_ASSESSMENT}/add" -> {
                bundleArgs?.courseData?.assessmentId = viewModel.quizData.assessmentId
                adapter?.notifyDataSetChanged()
                adapter = null
                setAdapter()
                viewModel.continueAction()
            }
            ApiEndPoints.API_ADD_QUIZ_IMAGE, ApiEndPoints.API_ADD_ASSESSMENT_IMAGE -> {
                adapter?.notifyItemChanged(viewModel.adapterPosition)
            }
            ApiEndPoints.API_ADD_QUIZ_QUESTION, ApiEndPoints.API_ADD_ASSESSMENT_QUESTION -> {

                if (viewModel.isQuiz) {
                    isAllAnsMarked()
                }
                showToastShort((value as BaseResponse<QuizData>).message)
                setAdapter()
                binding.btAdd.visible()
            }
            ApiEndPoints.API_LECTURE_DELETE + "/delete" -> {
                if (!sectionChildPosition.isNullOrNegative()) {
                    bundleArgs?.sectionData?.get(
                        bundleArgs?.adapterPosition ?: 0
                    )?.lessonList?.removeAt(sectionChildPosition)
                } else {
                    bundleArgs?.sectionData
                        ?.get(bundleArgs?.adapterPosition ?: 0)
                        ?.lessonList?.removeAt(
                            bundleArgs?.sectionData?.get(
                                bundleArgs?.adapterPosition ?: 0
                            )?.lessonList?.size ?: 0
                        )
                }
            }
            ApiEndPoints.API_ADD_ASSESSMENT + "/delete" -> {
                bundleArgs?.courseData?.assessmentId = null
                bundleArgs?.courseData?.assessmentFreezeContent = false
                bundleArgs?.courseData?.assessmentMandatory = false
                bundleArgs?.courseData?.assessmentName = ""
            }
        }
    }


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
                        bundle.putInt("childPosition", sectionChildPosition)
                        findNavController().navigate(
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
                optionList = ArrayList<QuizOptionData>()
                questionType = QUIZ.MULTIPLE_CHOICE

            }
        )

        setAdapter(true)

    }

    override fun onResume() {
        super.onResume()
        baseActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun isAllAnsMarked() {
        bundleArgs?.sectionData?.get(bundleArgs?.adapterPosition ?: 0)?.lessonList?.apply {
            val pos = if (bundleArgs?.childPosition.isNullOrNegative()) {
                size - 1
            } else {
                bundleArgs?.childPosition!!
            }
            get(pos).totalQuizQues = viewModel.quizData.list?.size

            get(pos).allAnsMarked =
                viewModel.quizData.list?.filter { !it.isAnsMarked() }.isNullOrEmpty()
        }
    }
}