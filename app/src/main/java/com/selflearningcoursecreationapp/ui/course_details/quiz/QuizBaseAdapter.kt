package com.selflearningcoursecreationapp.ui.course_details.quiz

import android.annotation.SuppressLint
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.databinding.FragmentDragAndDropBinding
import com.selflearningcoursecreationapp.databinding.FragmentImageQuizQuesBinding
import com.selflearningcoursecreationapp.databinding.FragmentMatchColumnBinding
import com.selflearningcoursecreationapp.databinding.FragmentSingleMultiChoiceBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.ui.create_course.add_assessment.AssessmentColumnOptionAdapter
import com.selflearningcoursecreationapp.ui.create_course.quiz.MarkColumnOptionAdapter
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DragAndDropAdapter
import com.selflearningcoursecreationapp.utils.QUIZ
import com.selflearningcoursecreationapp.utils.customViews.LMSImageView

class QuizBaseAdapter(private val typeList: ArrayList<QuizQuestionData>, private val points: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            QUIZ.SINGLE_CHOICE -> {
                val binding = DataBindingUtil.inflate<FragmentSingleMultiChoiceBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.fragment_single_multi_choice,
                    parent,
                    false
                )
                return SingleChoiceViewHolder(binding)
            }
            QUIZ.MULTIPLE_CHOICE -> {
                val binding = DataBindingUtil.inflate<FragmentSingleMultiChoiceBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.fragment_single_multi_choice,
                    parent,
                    false
                )
                return MultipleChoiceViewHolder(binding)
            }
            QUIZ.DRAG_DROP -> {
                val binding = DataBindingUtil.inflate<FragmentDragAndDropBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.fragment_drag_and_drop,
                    parent,
                    false
                )


                return DragAndDropViewHolder(binding)
            }
            QUIZ.MATCH_COLUMN -> {

                val binding = DataBindingUtil.inflate<FragmentMatchColumnBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.fragment_match_column,
                    parent,
                    false
                )

                return MatchColumnViewHolder(binding)
            }
            QUIZ.IMAGE_BASED -> {
                val binding = DataBindingUtil.inflate<FragmentImageQuizQuesBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.fragment_image_quiz_ques,
                    parent,
                    false
                )

                return ImageBasedViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<FragmentSingleMultiChoiceBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.fragment_single_multi_choice,
                    parent,
                    false
                )


                return SingleChoiceViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return typeList[position].questionType ?: QUIZ.SINGLE_CHOICE
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (typeList[position].questionType) {
            QUIZ.SINGLE_CHOICE -> {
                (holder as SingleChoiceViewHolder).bind(position)
            }
            QUIZ.MULTIPLE_CHOICE -> {
                (holder as MultipleChoiceViewHolder).bind(position)

            }
            QUIZ.DRAG_DROP -> {
                (holder as DragAndDropViewHolder).bind(position)

            }

            QUIZ.MATCH_COLUMN -> {
                (holder as MatchColumnViewHolder).bind(position, holder.binding)

            }
            QUIZ.IMAGE_BASED -> {
                (holder as ImageBasedViewHolder).bind(position)

            }

        }
    }

    override fun getItemCount() = typeList.size

    private inner class SingleChoiceViewHolder(private var binding: FragmentSingleMultiChoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val context = binding.root.context
            binding.tvQuizNum.text =
                String.format(context.getString(R.string.question_no), position + 1, typeList.size)
            binding.tvQuizNum.contentDescription =
                "Question ${(position + 1)} out of ${typeList.size}"

            binding.tvTitle.text = typeList[position].title
            binding.tvSelectedValue.text =
                context.getQuantityString(R.plurals.point_quantity, points)
            binding.recyclerQuizOption.adapter = QuizAnswerListAdapter(
                typeList[position].optionList,
                QUIZ.SINGLE_CHOICE
            )

        }

    }

    private inner class MultipleChoiceViewHolder(private var binding: FragmentSingleMultiChoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val context = binding.root.context

            binding.tvQuizNum.text =
                String.format(context.getString(R.string.question_no), position + 1, typeList.size)
            binding.tvQuizNum.contentDescription =
                "Question ${(position + 1)} out of ${typeList.size}"

            binding.tvTitle.text = typeList[position].title
            binding.tvSelectedValue.text =
                context.getQuantityString(R.plurals.point_quantity, points)
            binding.recyclerQuizOption.adapter = QuizAnswerListAdapter(
                typeList[position].optionList,
                QUIZ.MULTIPLE_CHOICE
            )

        }
    }

    private inner class ImageBasedViewHolder(private var binding: FragmentImageQuizQuesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            val context = binding.root.context
            binding.parentCL.visibleView(!typeList[position].questionImage.isNullOrEmpty())
            binding.ivHeader.loadImage(
                typeList[position].questionImage,
                R.drawable.ic_default_banner
            )
            binding.tvQuesNum.text =
                String.format(context.getString(R.string.question_no), position + 1, typeList.size)

            binding.tvQuesNum.contentDescription =
                "Question ${(position + 1)} out of ${typeList.size}"

            binding.tvTitle.text = typeList[position].title
            binding.tvSelectedValue.text =
                context.getQuantityString(R.plurals.point_quantity, points)
            binding.rvOptions.adapter = QuizAnswerListAdapter(
                typeList[position].optionList,
                QUIZ.SINGLE_CHOICE
            )

        }

    }

    private inner class DragAndDropViewHolder(private var binding: FragmentDragAndDropBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val context = binding.root.context

            binding.tvQuizNum.text =
                String.format(context.getString(R.string.question_no), position + 1, typeList.size)
            binding.tvQuizNum.contentDescription =
                "Question ${(position + 1)} out of ${typeList.size}"

            binding.tvTitle.text = typeList[position].title
            binding.tvSelectedValue.text =
                context.getQuantityString(R.plurals.point_quantity, points)
            val adapter = DragAndDropAdapter(typeList[position].optionList)
            typeList[position].optionList.find { it.isSelected == true }?.let {
                binding.imgReset.visible()
                binding.ivAns.visibleView(!it.image.isNullOrEmpty())
                binding.tvSetHere.visibleView(it.image.isNullOrEmpty())
                binding.ivAns.loadImage(it.image, R.drawable.ic_default_banner)
                binding.tvSetHere.text = it.option1
            } ?: kotlin.run {
                binding.imgReset.gone()
                binding.ivAns.gone()
                binding.tvSetHere.visible()
                binding.tvSetHere.text = ""
            }

            binding.imgReset.setOnClickListener {
                typeList[position].optionList.forEach { option -> option.isSelected = false }


                notifyItemChanged(position)
            }


            val recyclerViewState = binding.rvQuestions.layoutManager?.onSaveInstanceState()
            binding.rvQuestions.layoutManager?.onRestoreInstanceState(recyclerViewState)
            binding.rvQuestions.adapter = adapter
            binding.rvQuestions.setVerticalScrollBarEnabled(true)

            binding.dropContainer.setOnDragListener(
                ChoiceDragListener(
                    typeList[position].optionList,
                    position
                )
            )
        }

    }

    private inner class MatchColumnViewHolder(var binding: FragmentMatchColumnBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, binding: FragmentMatchColumnBinding) {
            val context = binding.root.context
            val data = typeList[position]
            binding.tvQuesNum.contentDescription =
                "Question ${(position + 1)} out of ${typeList.size}"

            binding.rvOptions.adapter = AssessmentColumnOptionAdapter(data.optionList, true)
            binding.rvOption2.adapter = AssessmentColumnOptionAdapter(data.optionList, false)
            val hashmap = HashMap<String, ArrayList<QuizOptionData>>()
            data.optionList.forEachIndexed { index, quizOptionData ->
                hashmap[String.format(
                    context.getString(R.string.option_no),
                    index.getCharString()
                )] =
                    data.optionList.map { it.copy(isSelected = it.id == quizOptionData.ansId) } as ArrayList<QuizOptionData>
            }
            val markAnsAdapter = MarkColumnOptionAdapter(
                hashmap,
                data.questionType ?: QUIZ.MATCH_COLUMN
            )
            binding.rvColumnAns.adapter = markAnsAdapter
            markAnsAdapter.setOnAdapterItemClickListener(object : BaseAdapter.IViewClick {
                override fun onItemClick(vararg items: Any) {
                    if (items.isNotEmpty()) {
                        val type = items[0] as Int
                        val rowPosition = items[1] as Int
                        val columnPosition = items[2] as Int
                        when (type) {
                            Constant.CLICK_VIEW -> {
                                val keyList = hashmap.map { it.key }
                                when (data.questionType) {


                                    QUIZ.MATCH_COLUMN -> {
                                        hashmap[keyList[rowPosition]]?.forEach {
                                            it.isSelected = false
                                        }
                                        keyList.forEach {
                                            hashmap[it]?.get(columnPosition)?.isSelected = false
                                        }
                                        hashmap[keyList[rowPosition]]?.get(columnPosition)?.isSelected =
                                            true

                                        data.optionList.forEachIndexed { index, quizOptionData ->
                                            if (quizOptionData.ansId == hashmap[keyList[rowPosition]]?.get(
                                                    columnPosition
                                                )?.id
                                            ) {
                                                quizOptionData.ansId = 0
                                                quizOptionData.isSelected = false
                                            }
                                        }
                                        data.optionList.get(rowPosition).ansId =
                                            hashmap[keyList[rowPosition]]?.get(columnPosition)?.id
                                        data.optionList.get(rowPosition).isSelected = true
                                        markAnsAdapter.notifyDataSetChanged()
//                                        binding.parentNSV.fullScroll(View.FOCUS_DOWN);
//                                        binding.parentNSV.postDelayed(Runnable {
////                                            binding.parentNSV.scroll
//                                            binding.parentNSV.fullScroll(
//                                                View.FOCUS_DOWN
//                                            )
//                                        },10)

                                    }
                                }
                            }
                        }
                    }
                }

            })

            this.binding.tvQuesNum.text =
                String.format(context.getString(R.string.question_no), position + 1, typeList.size)
            this.binding.tvTitle.text = typeList[position].title
            this.binding.tvSelectedValue.text =
                context.getQuantityString(R.plurals.point_quantity, points)

        }
    }

    @SuppressLint("NewApi")
    private inner class ChoiceDragListener(
        private var list: ArrayList<QuizOptionData>,
        private var parentPosition: Int
    ) : View.OnDragListener {
        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {


                    Log.e("hithere", "asdasdasd")
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                }
                DragEvent.ACTION_DROP -> {
//                    adapter = DragAndDropAdapter()
//                    adapter.reset(arrLis)

//                    rv_questions.adapter = adapter

                    //handle the dragged view being dropped over a drop view
                    val view = event.localState as View
                    //view dragged item is being dropped on
                    val dropTarget = v as ConstraintLayout
                    //view being dragged and dropped
                    val dropped = view as LinearLayoutCompat
                    //checking whether first character of dropTarget equals first character of dropped

                    //stop displaying the view where it was before it was dragged
//                  view.setVisibility(View.INVISIBLE)

                    //update the text in the target view to reflect the data being dropped
                    val childPosition = dropped.tag as Int
                    val optionData = list[childPosition]

                    typeList[parentPosition].optionList.forEachIndexed { index, quizOptionData ->
                        quizOptionData.isSelected = index == childPosition
                    }
                    dropTarget.children.forEach { childView ->
                        if (childView is TextView) {
                            childView.visibleView(optionData.image.isNullOrEmpty())
                            childView.text = optionData.option1
                        }
                        if (childView is LMSImageView) {
                            childView.loadImage(optionData.image, R.drawable.ic_default_banner)
                            childView.visibleView(!optionData.image.isNullOrEmpty())
                        }
                        if (childView is ImageView && childView.id == R.id.img_reset) {
                            childView.visibleView(!optionData.image.isNullOrEmpty() || !optionData.option1.isNullOrEmpty())
                        }

                    }

                    typeList[parentPosition].optionList[childPosition].isSelected = true
                    notifyItemChanged(parentPosition)

                    //make it bold to highlight the fact that an item has been dropped
//                    dropTarget.typeface = Typeface.DEFAULT_BOLD
//                    dropTarget.background = dropped.background

//                    view.setBackgroundColor(ContextCompat.getColor(view.context,R.color.colorBlack))

//                    view.alpha = .5f
//                    view.isEnabled=false
                    //if an item has already been dropped here, there will be a tag
                    val tag = dropTarget.tag
                    //if there is already an item here, set it back visible in its original place
                    if (tag != null) {
                        //the tag is the view id already dropped here
                        val existingID = tag as Int
                        //set the original view visible again
//                        requireActivity().findViewById<View>(existingID).visibility = View.VISIBLE
                    }


                    //set the tag in the target view being dropped on - to the ID of the view being dropped
                    dropTarget.tag = dropped.id

//                    adapter.notifyDataSetChanged()
                    //remove setOnDragListener by setting OnDragListener to null, so that no further drag & dropping on this TextView can be done
//                    dropTarget.setOnDragListener(null)
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                }
                else -> {
                }
            }
            return true
        }
    }


}

