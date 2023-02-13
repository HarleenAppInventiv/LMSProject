package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterLectureLayoutBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.LectureStatus
import com.selflearningcoursecreationapp.utils.MediaType
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

class ChildLectureAdapter(
    private val lessonList: ArrayList<ChildModel>,
    private var doEnable: Boolean
) :
    BaseAdapter<AdapterLectureLayoutBinding>() {
    override fun getLayoutRes() = R.layout.adapter_lecture_layout

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterLectureLayoutBinding
        val context = binding.root.context
        binding.doEnable = doEnable


        val min = lessonList[position].lectureContentDuration ?: 0



        binding.tvLessonTime.text =
            if (min.isNullOrZero()) {
                if (lessonList[position].mediaType.isMediaLecture()) {
                    context.getString(
                        R.string.calculating
                    )
                } else {
                    "--:--"
                }
            } else min.getTime(context)
        binding.ivDelete.setOnClickListener {
            if (doEnable) {
                onItemClick(Constant.CLICK_OPTION_DELETE, position)
            }
        }

        binding.ivEdit.setOnClickListener {
            if (doEnable) {
                onItemClick(Constant.CLICK_EDIT, position)
            }
        }

        binding.tvStatus.setOnClickListener {
            if (doEnable && !lessonList[position].contentStatus.isLectureInProcessing()) {
                onItemClick(Constant.CLICK_EDIT, position)
            }
        }

        binding.parentCV.setOnClickListener {
            if (doEnable && !lessonList[position].contentStatus.isLectureInProcessing()) {
                onItemClick(Constant.CLICK_EDIT, position)
            }
        }

        var mediaType =
            lessonList[position].mediaType?.getMediaType(/*lessonList[position].lectureStatusId == LectureStatus.IN_PROCESS*/
                lessonList[position].lectureContentType
            )



        binding.tvLessonType.text = mediaType?.second?.let { context.getString(it) } ?: ""
        binding.ivMediaFile.loadImage(
            lessonList[position].thumbNailURl,
            mediaType?.first ?: R.drawable.ic_docx_icon
        )
        binding.parentCV.strokeColor = context.getAttrResource(R.attr.accentColor_Red)

//        binding.tvStatus.visibleView(lessonList[position].lectureStatusId != LectureStatus.COMPLETED)
        binding.tvStatus.setTextColor(
            context.getAttrResource(R.attr.accentColor_Red)

        )
        when (lessonList[position].mediaType) {
            MediaType.QUIZ -> {
                binding.tvStatus.text = context.getString(R.string.in_complete_quiz)
            }
            else -> {
                if (lessonList[position].contentStatus.isLectureFailed()) {
                    binding.tvStatus.text = context.getString(R.string.retry)

                } else if (lessonList[position].contentStatus.isLectureInProcessing()) {
                    binding.tvStatus.text = context.getString(R.string.in_processing)
                    binding.parentCV.strokeColor = ThemeUtils.getAppColor(context)
                    binding.tvStatus.setTextColor(
                        ThemeUtils.getAppColor(context)

                    )


                } else {
                    binding.tvStatus.text = context.getString(R.string.in_complete)

                }
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

        val isEdit = if (lessonList[position].mediaType == MediaType.QUIZ) {
            lessonList[position].lectureStatusId == LectureStatus.COMPLETED
        } else
            lessonList[position].lectureStatusId == LectureStatus.COMPLETED && lessonList[position].contentStatus == LectureStatus.COMPLETED

//
//        binding.ivDelete.visibleView(isEdit)
        binding.ivEdit.visibleView(isEdit)
        binding.tvStatus.visibleView(!isEdit)
        binding.parentCV.strokeWidth =
            if (isEdit) 0 else context.resources.getDimensionPixelOffset(
                R.dimen._1sdp
            )

    }


    override fun getItemCount() = lessonList.size

}