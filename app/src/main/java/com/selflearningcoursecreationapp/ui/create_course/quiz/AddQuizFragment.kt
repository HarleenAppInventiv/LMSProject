package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentAddQuizBinding
import com.selflearningcoursecreationapp.extensions.getQuizTypeTitle
import com.selflearningcoursecreationapp.extensions.isNullOrZero
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.ui.dialog.UploadImageOptionsDialog
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceBottomDialog
import com.selflearningcoursecreationapp.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class AddQuizFragment : BaseFragment<FragmentAddQuizBinding>(), BaseAdapter.IViewClick,
    BaseBottomSheetDialog.IDialogClick, View.OnClickListener {
    private var adapter: AddQuizViewAdapter? = null
    private var adapterPosition: Int = 0
    private var childPosition: Int = 0
    private val viewModel: AddQuizVM by viewModel()

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
        arguments?.let {
            viewModel.quizData.courseId = it.getInt("courseId")
            viewModel.quizData.sectionId = it.getInt("sectionId")
        }
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        if (viewModel.quizData.quizId.isNullOrZero())
            viewModel.addQuiz()
        else setAdapter()


        binding.btAdd.setOnClickListener(this)
        binding.btContinue.setOnClickListener(this)

    }

    private fun setAdapter() {
        val count: String =
            baseActivity.resources.getQuantityString(
                R.plurals.question_quantity,
                viewModel.quizData.list?.size ?: 0,
                viewModel.quizData.list?.size ?: 0
            )
        binding.tvSelectedValue.text = count

        binding.btContinue.setBtnDisabled(viewModel.quizData.list?.size ?: 0 != 0)
        binding.btContinue.isEnabled = (viewModel.quizData.list?.size ?: 0 != 0)
        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = AddQuizViewAdapter(viewModel.quizData.list ?: ArrayList())
            binding.rvSections.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            adapterPosition = items[1] as Int
            when (type) {
                DialogType.CLICK_QUIZ_TYPE -> {
                    SingleChoiceBottomDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.CLICK_QUIZ_TYPE,
                            "title" to this@AddQuizFragment.baseActivity.getString(
                                R.string.select_ques_type
                            ),
                            "id" to viewModel.getListData(adapterPosition)?.questionType
                        )
                        setOnDialogClickListener(this@AddQuizFragment)
                    }.show(childFragmentManager, "")
                }

                DialogType.CLICK_QUIZ_OPTION -> {
                    childPosition = items[2] as Int
                    AddQuizOptionDialog().apply {

                        setOnDialogClickListener(this@AddQuizFragment)
                    }.show(childFragmentManager, "")
                }
                DialogType.QUIZ_ANSWER -> {
                    QuizMarkAnsDialog().apply {
                        arguments = bundleOf(

                            "quizData" to viewModel.quizData,
                            "position" to adapterPosition

                        )
                        setOnDialogClickListener(this@AddQuizFragment)
                    }.show(childFragmentManager, "")
                }

                Constant.CLICK_SAVE -> {


                    viewModel.saveQuestionValidation(adapterPosition)
                }

                DialogType.CLICK_BANNER -> {
                    UploadImageOptionsDialog().apply {
                        arguments = bundleOf("type" to DialogType.CLICK_BANNER)
                        setOnDialogClickListener(this@AddQuizFragment)
                    }.show(childFragmentManager, "")
                }
                Constant.CLICK_DELETE -> {
                    CommonAlertDialog.builder(baseActivity).title("Delete Question?")
                        .description("Are you sure, you want to delete this question?")
                        .positiveBtnText(baseActivity.getString(R.string.yes))
                        .icon(R.drawable.ic_delete_alert)
                        .getCallback {
                            if (it) {
                                if (viewModel.getListData(adapterPosition)?.questionId.isNullOrZero()) {
                                    viewModel.quizData.list?.removeAt(adapterPosition)
                                    setAdapter()
                                } else {
                                    viewModel.deleteQuestion(viewModel.getListData(adapterPosition))
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

                    viewModel.getListData(adapterPosition)?.questionImage = items[1] as String
                    viewModel.uploadImage(
                        File(items[1] as String),
                        MEDIA.QUIZ_QUES,
                        adapterPosition,
                        0
                    )
                    setAdapter()
                }
                DialogType.CLICK_QUIZ_TYPE -> {
                    viewModel.getListData(adapterPosition)?.questionType =
                        (items[1] as SingleChoiceData).id
                    viewModel.getListData(adapterPosition)?.questionTypeTitle =
                        baseActivity.getQuizTypeTitle(
                            viewModel.getListData(adapterPosition)?.questionType ?: 1
                        )
                    viewModel.getListData(adapterPosition)?.optionList?.forEach {
                        it.quizType = viewModel.getListData(adapterPosition)?.questionType
                    }

                    setAdapter()
//                    list[adapterPosition]
                }
                DialogType.CLICK_QUIZ_OPTION -> {
                    val selectedType = items[1] as Int
                    if (selectedType == 1) {
                        viewModel.getListData(adapterPosition)?.optionList?.get(
                            childPosition
                        )?.option1 = items[2] as String
                    } else {
                        viewModel.getListData(adapterPosition)?.optionList?.get(
                            childPosition
                        )?.image = items[2] as String
                        viewModel.uploadImage(
                            File(items[2] as String),
                            MEDIA.QUIZ_OPTION,
                            adapterPosition,
                            childPosition
                        )
                    }
                    setAdapter()

                }
                DialogType.QUIZ_ANSWER -> {
                    val hashMap = items[1] as HashMap<String, ArrayList<QuizOptionData>>
                    when (viewModel.getListData(adapterPosition)?.questionType) {
                        QUIZ.MATCH_COLUMN -> {
                            hashMap.keys?.forEachIndexed { index, s ->
                                hashMap[s]?.filter { it.isSelected == true }?.forEach {
                                    viewModel.getListData(adapterPosition)?.optionList?.get(index)?.ansId =
                                        it.id
                                }
                            }
                        }
                        else -> {
                            val key = hashMap.map { it.key }?.get(0)
                            viewModel.getListData(adapterPosition)?.optionList?.forEach { option ->
                                option.isSelected = !(hashMap.get(key)
                                    ?.filter { it.isSelected == true && it.id == option.id }
                                    ?.isNullOrEmpty() ?: true)
                            }
                        }
                    }
                    setAdapter()
                }
            }
        }
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_ADD_QUIZ -> {
                addQuestion()
            }
            ApiEndPoints.API_ADD_QUIZ_IMAGE, ApiEndPoints.API_ADD_QUIZ_QUESTION -> {
                setAdapter()
            }
        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.bt_add -> {
                addQuestion()
            }
            R.id.bt_continue -> {
                if (viewModel.quizData.list?.find { it.questionId.isNullOrZero() } != null) {
                    showToastShort("Please save all quiz")
                } else {
                    findNavController().navigate(
                        R.id.action_addQuizFragment_to_quizSettingsFragment,
                        bundleOf("quizData" to viewModel.quizData)
                    )
                }
            }
        }
    }

    private fun addQuestion() {
        if (viewModel.quizData.list == null) {
            viewModel.quizData.list = ArrayList()
        }
        viewModel.quizData.list?.add(
            QuizQuestionData(
                true,
                QUIZ.MULTIPLE_CHOICE,
                questionTypeTitle = baseActivity.getQuizTypeTitle(QUIZ.MULTIPLE_CHOICE),
                isEnabled = true,
                title = "",
                optionList = ArrayList<QuizOptionData>()
            )
        )

        setAdapter()

    }
}