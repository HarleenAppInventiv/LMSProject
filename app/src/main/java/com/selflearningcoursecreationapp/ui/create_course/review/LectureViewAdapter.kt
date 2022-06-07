package com.selflearningcoursecreationapp.ui.create_course.review

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterLectureViewBinding
import com.selflearningcoursecreationapp.extensions.getTime
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.ChildModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.MEDIA_TYPE

class LectureViewAdapter(
    private val lessonList: ArrayList<ChildModel>,
    private var isEdit: Boolean = true
) :
    BaseAdapter<AdapterLectureViewBinding>() {
    override fun getLayoutRes() = R.layout.adapter_lecture_view

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        var binding = holder.binding as AdapterLectureViewBinding
        val context = binding.root.context
        binding.tvLessonName.text = lessonList[position].lectureTitle
        val min =
            if (lessonList[position].mediaType != MEDIA_TYPE.QUIZ)
                lessonList[position].lectureContentDuration?.toLongOrNull()?.div(10000) ?: 0
            else {

                lessonList[position].lectureContentDuration?.toLongOrNull() ?: 0

            }


        binding.tvLessonTime.text = context.getTime(min)


        binding.ivDelete.setOnClickListener {

            onItemClick(Constant.CLICK_DELETE, position)

        }
        binding.ivPlay.setOnClickListener {

            onItemClick(Constant.CLICK_PLAY, position)

        }
        binding.ivEdit.setOnClickListener {
            onItemClick(Constant.CLICK_EDIT, position)
        }

        var title = ""
        title = when (lessonList[position].mediaType) {
            1 -> {
                loadGlide(R.drawable.ic_video_icon)

                context.getString(R.string.video)
            }
            2 -> {
                loadGlide(R.drawable.ic_audio_icon)
                context.getString(R.string.audio)

            }
            3 -> {
//                binding.ivMediaFile.setImageResource(R.drawable.ic_doc_icon)
                loadGlide(R.drawable.ic_text_icon)
                context.getString(R.string.doc)
            }
            4 -> {
                loadGlide(R.drawable.ic_docx_icon)
                context.getString(R.string.text)

            }
            else -> {

                loadGlide(R.drawable.ic_quiz)
                context.getString(R.string.quiz)


            }
        }
        binding.tvLessonType.text = title
//        binding.ivMediaFile.
        binding.ivEdit.setOnClickListener {
            onItemClick(Constant.CLICK_EDIT, position)
        }


    }

    override fun getItemCount() = lessonList.size

    fun loadGlide(icDocIcon: Int) {
//        Glide.with(binding.root.context).load().into
        binding.ivMediaFile.setImageResource(icDocIcon)

    }
}