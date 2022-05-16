package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterLectureLayoutBinding
import com.selflearningcoursecreationapp.utils.Constant

class ChildLectureAdapter(private val lessonList: ArrayList<ChildModel>) :
    BaseAdapter<AdapterLectureLayoutBinding>() {
    override fun getLayoutRes() = R.layout.adapter_lecture_layout

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        var binding = holder.binding as AdapterLectureLayoutBinding

        binding.tvLessonName.text = lessonList[position].lectureTitle
        binding.ivDelete.setOnClickListener {

            onItemClick(Constant.CLICK_DELETE, position)

        }

        when (lessonList[position].mediaType) {
            1 -> {
                loadGlide(R.drawable.ic_video_icon)
                binding.tvLessonType.text = "Video"

            }
            2 -> {
                loadGlide(R.drawable.ic_audio_icon)
                binding.tvLessonType.text = "Audio"

            }
            3 -> {
//                binding.ivMediaFile.setImageResource(R.drawable.ic_doc_icon)
                loadGlide(R.drawable.ic_docx_icon)
                binding.tvLessonType.text = "Doc"
            }
            4 -> {
                loadGlide(R.drawable.ic_text_icon)
                binding.tvLessonType.text = "Text"

            }
            5 -> {

                loadGlide(R.drawable.ic_text_icon)
                binding.tvLessonType.text = "Quiz"
            }
        }
//        binding.ivMediaFile.
//        binding.btUpload.setOnClickListener {
//            onItemClick(Constant.CLICK_UPLOAD, position)
//        }


    }

    override fun getItemCount() = lessonList.size

    fun loadGlide(icDocIcon: Int) {
//        Glide.with(binding.root.context).load().into
        binding.ivMediaFile.setImageResource(icDocIcon)

    }
}