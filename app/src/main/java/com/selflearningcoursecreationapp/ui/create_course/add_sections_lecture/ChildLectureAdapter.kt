package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterLectureLayoutBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.LectureStatus
import com.selflearningcoursecreationapp.utils.MediaType

class ChildLectureAdapter(
    private val lessonList: ArrayList<ChildModel>,
    private val doEnable: Boolean,
    private var isEdit: Boolean = true,
) :
    BaseAdapter<AdapterLectureLayoutBinding>() {
    override fun getLayoutRes() = R.layout.adapter_lecture_layout

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterLectureLayoutBinding
        val context = binding.root.context
        binding.doEnable = doEnable

        binding.ivDelete.visibleView(isEdit)
        binding.ivEdit.visibleView(isEdit)
        binding.ivPlay.visibleView(!isEdit)


        val min = lessonList[position].lectureContentDuration ?: 0



        binding.tvLessonTime.text = min.getTime(context)
        binding.ivDelete.setOnClickListener {
            if (doEnable) {
                onItemClick(Constant.CLICK_OPTION_DELETE, position)
            }

        }
        binding.ivPlay.setOnClickListener {

            onItemClick(Constant.CLICK_PLAY, position)

        }
        binding.ivEdit.setOnClickListener {
            if (doEnable) {
                onItemClick(Constant.CLICK_EDIT, position)
            }
        }
        binding.tvStatus.setOnClickListener {
            if (doEnable) {
                onItemClick(Constant.CLICK_EDIT, position)
            }
        }
        val mediaType =
            lessonList[position].mediaType?.getMediaType(lessonList[position].lectureStatusId == LectureStatus.IN_PROCESS)
        binding.tvLessonType.text = mediaType?.second?.let { context.getString(it) } ?: ""
//        binding.ivMediaFile.setImageResource(mediaType?.first ?: R.drawable.ic_docx_icon)
        binding.ivMediaFile.loadImage(
            lessonList[position].thumbNailURl,
            mediaType?.first ?: R.drawable.ic_docx_icon
        )

        when (lessonList[position].mediaType) {
            MediaType.QUIZ -> {
                binding.parentCV.strokeColor =
                    ContextCompat.getColor(context, R.color.accent_color_fc6d5b)
                binding.parentCV.strokeWidth =
                    if (lessonList[position].lectureStatusId == LectureStatus.IN_PROCESS) context.resources.getDimensionPixelOffset(
                        R.dimen._1sdp
                    ) else 0
                binding.tvStatus.visibleView(lessonList[position].lectureStatusId == LectureStatus.IN_PROCESS)
                binding.tvStatus.text = context.getString(R.string.in_complete_quiz)
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.accent_color_fc6d5b
                    )
                )
                binding.ivEdit.visibleView(lessonList[position].lectureStatusId == LectureStatus.COMPLETED)
            }
            else -> {
                binding.ivEdit.visible()
                binding.tvStatus.gone()
            }
        }

        if (lessonList[position].lectureTitle.isNullOrEmpty()) {
            binding.tvLessonName.text = String.format(
                "%s %d",
                mediaType?.second?.let { context.getString(it) },
                position + 1
            )
        } else {
            binding.tvLessonName.text = lessonList[position].lectureTitle

        }
        binding.ivEdit.setOnClickListener {
            onItemClick(Constant.CLICK_EDIT, position)
        }


    }

    override fun getItemCount() = lessonList.size

}