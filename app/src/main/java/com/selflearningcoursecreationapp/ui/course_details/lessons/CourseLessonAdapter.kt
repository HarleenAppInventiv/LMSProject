package com.selflearningcoursecreationapp.ui.course_details.lessons

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourseLessonBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.MediaType
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils

class CourseLessonAdapter(
    private var list: ArrayList<ChildModel>,
    private var showComment: Boolean = true,
    var mod: Boolean = false
) :
    BaseAdapter<AdapterCourseLessonBinding>() {
    override fun getLayoutRes() = R.layout.adapter_course_lesson

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterCourseLessonBinding
        val context = binding.root.context
        val data = list[position]
//        binding.tvLessonName.text = data.lectureTitle

        ResizeableUtils.builder(binding.tvLessonName).isBold(false)
            .isUnderline(false)
            .setFullText(data.lectureTitle)
            .setFullText(R.string.read_more_arrow)
            .setLessText(R.string.read_less_arrow)
            .setLimit(28)
            .setSpanSize(0.9f)
            .showDots(true)
//            .setSpanColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
            .build()
        val min =
//            if (data.mediaType != MediaType.QUIZ)
//                data.lectureContentDuration?.div(10000) ?: 0
//            else {

            data.lectureContentDuration ?: 0

//            }

        binding.tvLecComment.apply {
            setSpanString(
                SpanUtils.with(
                    context,
                    context.getString(R.string.mod_comment) + " " + data.lectureComment
                ).endPos(
                    21
                ).isBold().getSpanString()
            )
            visibleView((data.lectureComment?.isNotEmpty() == true) && showComment)
        }
        if (data.mediaType == MediaType.QUIZ) {
            binding.tvQuizAstr.visible()


        }
        binding.tvLessonTime.text = min.getTime(context)
        binding.tvPercent.text =
            list[position].lecturePercentageCompleted?.toInt()?.toString() + "%"
        binding.tvPercent.visibleView(
            (list[position].lecturePercentageCompleted?.toInt()
                ?: 0) > 0 && (list[position].lecturePercentageCompleted?.toInt() ?: 0) < 100
        )
        binding.ivCompleted.visibleView(
            (list[position].lecturePercentageCompleted?.toInt() ?: 0) == 100
        )
        binding.ivEye.visibleView(
            (list[position].lecturePercentageCompleted?.toInt()
                ?: 0) == 100 && list[position].mediaType == MediaType.QUIZ
        )
        binding.tvQuizStatus.visibleView(
            (list[position].lecturePercentageCompleted?.toInt()
                ?: 0) == 100 && list[position].mediaType == MediaType.QUIZ
        )


        when (list[position].quizPassed) {
            true -> {
                binding.tvQuizStatus.text = context.getString(R.string.pass)
                binding.tvQuizStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        context.getAttrColor(R.attr.accentColor_Green)
                    )
                )
            }
            false -> {
                binding.tvQuizStatus.text = context.getString(R.string.fail)
                binding.tvQuizStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        context.getAttrColor(R.attr.accentColor_Red)
                    )
                )
            }
        }


        binding.pbProgress.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }
        binding.pbProgress.apply {
            visibleView(
                (list[position].lecturePercentageCompleted?.toInt()
                    ?: 0) > 0 && (list[position].lecturePercentageCompleted?.toInt() ?: 0) < 100
            )
            max = 100
            progress = list[position].lecturePercentageCompleted?.toInt() ?: 0
//                if (data.lecturePercentageCompleted?.toInt() == 100) list[position].lectureContentDuration?.toInt() ?: 0
//            else data.lectureContentDuration?.minus(data.lectureTotalPlayedTime ?: 0L)!!.toInt()

        }

        val mediaType = data.mediaType?.getMediaType(/*true*/)
        binding.tvLessonType.text = mediaType?.second?.let { context.getString(it) } ?: ""
//        binding.ivMediaFile.setImageResource(mediaType?.first ?: R.drawable.ic_docx_icon)
        binding.ivMediaFile.loadImage(
            data.thumbNailURl,
            mediaType?.first ?: R.drawable.ic_docx_icon
        )

//        binding.ivMediaFile.
        binding.parentCL.setOnClickListener {
            if (!mod && !data.isEnabled) {


                onItemClick(Constant.CLICK_VIEW, position)

            } else
                onItemClick(Constant.CLICK_PLAY, position)
        }

        binding.ivEye.setOnClickListener {
            onItemClick(Constant.CLICK_QUIZ_REPORT, position)
        }

        if (binding.ivCompleted.isVisible) {
            binding.parentCL.contentDescription = "Lesson ${data.lectureTitle} is already completed"
        }



        if (!mod) {
            binding.root.alpha = if (!data.isEnabled) 0.5f else 1f
//            binding.root.isEnabled = data.isEnabled


        }


    }

    override fun getItemCount() = list.size
}