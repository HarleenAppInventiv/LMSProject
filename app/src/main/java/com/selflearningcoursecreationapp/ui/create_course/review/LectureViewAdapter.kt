package com.selflearningcoursecreationapp.ui.create_course.review

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterLectureViewBinding
import com.selflearningcoursecreationapp.extensions.getMediaType
import com.selflearningcoursecreationapp.extensions.getTime
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.Constant

class LectureViewAdapter(
    private val lessonList: ArrayList<ChildModel>,
    private var fromModerator: Boolean = false
) :
    BaseAdapter<AdapterLectureViewBinding>() {
    override fun getLayoutRes() = R.layout.adapter_lecture_view

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterLectureViewBinding
        val context = binding.root.context
        binding.tvLessonName.text = lessonList[position].lectureTitle
        if (fromModerator) {
            binding.ivPlay.setVectorDrawable(R.drawable.ic_play_content_filled)
        } else {
            binding.ivPlay.setVectorDrawable(R.drawable.ic_play_file)

        }

        val min =
//            if (lessonList[position].mediaType != MediaType.QUIZ)
//                lessonList[position].lectureContentDuration?.div(10000) ?: 0
//            else {

            lessonList[position].lectureContentDuration ?: 0

//            }


        binding.tvLessonTime.text = min.getTime(context)


        binding.ivDelete.setOnClickListener {

            onItemClick(Constant.CLICK_DELETE, position)

        }
        binding.ivPlay.setOnClickListener {

            onItemClick(Constant.CLICK_PLAY, position)

        }
        binding.ivEdit.setOnClickListener {
            onItemClick(Constant.CLICK_EDIT, position)
        }


        val mediaType = lessonList[position].mediaType?.getMediaType(false)
        binding.tvLessonType.text = mediaType?.second?.let { context.getString(it) } ?: ""
//        binding.ivMediaFile.setImageResource(mediaType?.first ?: R.drawable.ic_docx_icon)
        binding.ivMediaFile.loadImage(
            lessonList[position].thumbNailURl,
            mediaType?.first ?: R.drawable.ic_docx_icon
        )

//        binding.ivMediaFile.
        binding.ivEdit.setOnClickListener {
            onItemClick(Constant.CLICK_EDIT, position)
        }


    }

    override fun getItemCount() = lessonList.size

}