package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSectionViewBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.MediaType
import com.selflearningcoursecreationapp.utils.ValidationConst
import java.util.*


class AddSectionAdapter(
    private val sectionData: ArrayList<SectionModel>,
    private var userId: Int,
    private var isCreator: Boolean,
    private var courseCreatorId: Int,
) :
    BaseAdapter<AdapterSectionViewBinding>(), View.OnTouchListener {
    override fun getLayoutRes() = R.layout.adapter_section_view

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = DataBindingUtil.inflate<AdapterSectionViewBinding>(
            LayoutInflater.from(parent.context),
            getLayoutRes(),
            parent,
            false
        )

        return AddSectionViewHolder(binding)
    }

    @SuppressLint("DefaultLocale", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val viewHolder = (holder as AddSectionViewHolder)
        val binding = (holder as AddSectionViewHolder).itemBinding
        val context = binding.root.context
        val data = sectionData[position]
        val doEnable = isCreator || userId == data.sectionCreatedById


        binding.lecture = sectionData[position]
        binding.doEnable = doEnable
        binding.executePendingBindings()

        if (data.isSectionSaved == true) {
            binding.ivSectionSaved.setImageResource(R.drawable.ic_saved_secton)
        } else {
            binding.ivSectionSaved.setImageResource(R.drawable.ic_unsaved_section)

        }
        binding.ivSectionSaved.setOnClickListener {
            if (data.isSectionSaved == false) {
                onItemClick(Constant.CLICK_INFO, position)
            }
        }

        binding.etSectionDesc.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                onItemClick(Constant.CLICK_BEFORE_TEXT_CHANGES, position)

                editTxtTouchScrolling(p0, p1)
                return false
            }

        })
        binding.etSectionTitle.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                onItemClick(Constant.CLICK_BEFORE_TEXT_CHANGES, position)

                editTxtTouchScrolling(p0, p1)
                return false
            }

        })
//        binding.etSectionTitle.setOnTouchListener(this)
//        binding.etSectionDesc.setOnTouchListener(this)
        binding.logoGroup.visibleView(courseCreatorId != data.sectionCreatedById)
        binding.ivUserImage.loadImage(
            data.sectionCreatedByProfileURL,
            R.drawable.ic_default_user_grey
        )
        binding.ivUserLogo.loadImage(
            data.sectionLogoURL,
            R.drawable.ic_logo_default
        )
        binding.llChild.gone()
        binding.btSave.gone()
        binding.rvLecture.gone()
        binding.ivVisible.setImageResource(R.drawable.ic_arrow_bottom)
        binding.ivDelete.setOnClickListener {
            if (doEnable) {
                onItemClick(Constant.CLICK_DELETE, position)
            }
        }

        binding.ivDelete.alpha = if (doEnable) 1f else 0.2f

//        binding.etSectionDesc.doOnTextChanged { input, _, _, _ ->
//            onItemClick(Constant.CLICK_TEXT_CHANGES, position)
//            val list = input.toString().split(" ")
////            var count = 0
////            for (i in 0 until list.size)
////            {
////                if (i<100)
////                {
////                    count+=list[i].length
////                    count+=1
////                }else{
////                    break
////                }
////            }
//            if (list.size > ValidationConst.MAX_COURSE_SECTION_DESC_LENGTH) {
//                binding.etSectionDesc. setText(list.dropLast(list.size - ValidationConst.MAX_COURSE_SECTION_DESC_LENGTH).joinToString(" ").toString())
//                binding.etSectionDesc.setSelection(  binding.etSectionDesc.text?.length?:0)
//            }
//            binding.tvDescTotalChar.text = binding.etSectionDesc.content().wordCount().toString()
//        }
//
        binding.etSectionDesc.setWordLimit(ValidationConst.MAX_COURSE_SECTION_DESC_LENGTH) {
            onItemClick(Constant.CLICK_TEXT_CHANGES, position)
            binding.tvDescTotalChar.text = binding.etSectionDesc.content().wordCount().toString()

        }
        binding.tvDescTotalChar.text = binding.etSectionDesc.content().wordCount().toString()

        binding.etSectionDesc.doBeforeTextChanged { _, _, _, _ ->
            onItemClick(Constant.CLICK_BEFORE_TEXT_CHANGES, position)
        }
        binding.etSectionDesc.doAfterTextChanged { _ ->
            onItemClick(Constant.CLICK_AFTER_TEXT_CHANGES, position)
        }

        binding.etSectionTitle.doOnTextChanged { _, _, _, _ ->
            onItemClick(Constant.CLICK_TEXT_CHANGES, position)
        }

//var millis= sectionData.get()

        var millis: Long = 0
        sectionData[position].lessonList.forEach {
            millis += if (it.mediaType != MediaType.QUIZ) {
                it.lectureContentDuration ?: 0
            } else {
                it.lectureContentDuration ?: 0
            }
        }
        binding.tvTotalTime.text = millis.getTime(context, true, true)

        binding.tvSectionNumber.text =
            String.format(binding.root.context.getString(R.string.section), position + 1)

        binding.ivVisible.contentDescription = "Expand Session${position + 1} from list"

        binding.tvLectureNumber.text = context.getQuantityString(
            R.plurals.lecture_quantity,
            sectionData[position].lessonList.size
        )

        binding.llChild.visibleView(sectionData[position].isVisible)
        binding.group.visibleView(!sectionData[position].isVisible && !data.lessonList.isNullOrEmpty())
//        binding.tvLectureNumber.visibleView(!data.lessonList.isNullOrEmpty())

        if (sectionData[position].isVisible) {
            binding.ivVisible.setImageResource(R.drawable.ic_arrow_top)
        } else {
            binding.ivVisible.setImageResource(R.drawable.ic_arrow_bottom)
        }


        binding.tvLectureList.text = data.lessonList.getLessonCount(context).joinToString(", ")




        binding.ivVisible.setOnClickListener {
            sectionData.forEachIndexed { index, sectionModel ->
                if (index != position) {
                    sectionModel.isVisible = false
                } else {
                    sectionModel.isVisible = !sectionModel.isVisible
                }
            }
            notifyDataSetChanged()

        }

        lectureFunctionality(position, binding, holder, data)


        binding.btSave.apply {
            isVisible = sectionData[position].lessonList.isEmpty()
                .not() && sectionData[position].uploadLesson


        }

        binding.btSave
            .setOnClickListener {
//            notifyDataSetChanged()

                onItemClick(
                    Constant.CLICK_SAVE,
                    position,
                    binding.etSectionTitle.content(),
                    binding.etSectionDesc.content()
                )
            }

        binding.btUpload.setOnClickListener {

            onItemClick(Constant.CLICK_UPLOAD, position)
        }

        binding.executePendingBindings()
        binding.tvTitleTotalChar.text = binding.etSectionTitle.text?.trim()?.length.toString()


        binding.etSectionTitle.doAfterTextChanged { textAfter ->
            binding.tvTitleTotalChar.setText(textAfter?.length.toString())
            binding.tvTitleTotalChar.apply {
                if ((textAfter?.length ?: 0) < ValidationConst.MAX_COURSE_SECTION_LENGTH) {
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                } else {
                    setTextColor(
                        context.getAttrResource(R.attr.accentColor_Red)
                    )
                }
            }
        }


//        binding.etSectionDesc.doAfterTextChanged {
//            val count = binding.etSectionDesc.content().wordCount()
//            if (count >= ValidationConst.MAX_COURSE_SECTION_DESC_LENGTH) {
////                val result: List<String>? = binding.etSectionDesc.text?.split(" ")
////                var finalText = ""
////                if (result != null) {
////                    for (i in 1 .. 500) {
////                        finalText += " " + result[i]
////                    }
////                }
////                binding.etSectionDesc.setText(finalText)
//                setCharLimit(binding.etSectionDesc, binding.etSectionDesc.content().length)
////                binding.tvDescTotalChar.apply {
////                    text ="500"
////                    setTextColor(ContextCompat.getColor(context, R.color.black))
////                }
//            } else {
//                binding.tvDescTotalChar.apply {
//                    text = count.toString()
//                    setTextColor(context.getAttrResource(R.attr.accentColor_Red))
//                }
//                removeFilter(binding.etSectionDesc)
//            }
//
////            binding.tvDescTotalChar.apply {
////                text = count.toString()
////                if (count < ValidationConst.MAX_COURSE_DESC_LENGTH_SHOW) {
////                    setTextColor(ContextCompat.getColor(context, R.color.black))
////                } else {
////                    setTextColor(context.getAttrResource(R.attr.accentColor_Red))
////                }
////            }
//
//
//        }

//        binding.etSectionDesc.doOnTextChanged { text, _, _, _ ->
//            val count = text.toString().wordCount()
//            if (count >= ValidationConst.MAX_COURSE_DESC_LENGTH_SHOW) {
//                val text: List<String>? = binding.etSectionDesc.text?.split(" ")
//                var finalText = ""
//                if (text != null) {
//                    for (i in 0 until 500) {
//                        finalText += " "+text[i]
//                    }
//                }
//                binding.etSectionDesc.setText(finalText)
//
//            }
//        }
    }

    private fun lectureFunctionality(
        position: Int,
        binding: AdapterSectionViewBinding,
        holder: BaseViewHolder,
        data: SectionModel
    ) {
        if (sectionData[position].lessonList.isNotEmpty()) {

            binding.rvLecture.visible()
            val touchHelper = object : TouchHelper() {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder,
                ): Boolean {

                    val childLectureAdapter = recyclerView.adapter as ChildLectureAdapter
                    sectionData[holder.adapterPosition].fromPosition = viewHolder.adapterPosition
                    sectionData[holder.adapterPosition].toPosition = target.adapterPosition
                    sectionData[holder.adapterPosition].mOrderChanged = true
                    onItemClick(Constant.CLICK_BEFORE_TEXT_CHANGES, position)

//                    if (! sectionData[holder.adapterPosition].fromPosition.isNullOrNegative() && ! sectionData[holder.adapterPosition].toPosition.isNullOrNegative()) {
//                        Collections.swap(
//                            sectionData[holder.adapterPosition].lessonList,
//                            sectionData[holder.adapterPosition].    fromPosition,
//                            sectionData[holder.adapterPosition].     toPosition
//                        )
//                        childLectureAdapter.notifyItemMoved( sectionData[holder.adapterPosition].fromPosition,  sectionData[holder.adapterPosition].toPosition)
//                        onItemClick(
//                            Constant.CLICK_SWAP,
//                            holder.adapterPosition,
//                            sectionData[holder.adapterPosition].   fromPosition,
//                            sectionData[holder.adapterPosition].  toPosition
//                        )
//                    }
                    return true
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && sectionData[holder.adapterPosition].mOrderChanged) {
                        sectionData[holder.adapterPosition].mOrderChanged = false
                        val childLectureAdapter = binding.rvLecture.adapter as ChildLectureAdapter

                        if (!sectionData[holder.adapterPosition].fromPosition.isNullOrNegative() && !sectionData[holder.adapterPosition].toPosition.isNullOrNegative()) {
                            Collections.swap(
                                sectionData[holder.adapterPosition].lessonList,
                                sectionData[holder.adapterPosition].fromPosition,
                                sectionData[holder.adapterPosition].toPosition
                            )

                            childLectureAdapter.notifyItemMoved(
                                sectionData[holder.adapterPosition].fromPosition,
                                sectionData[holder.adapterPosition].toPosition
                            )
                            childLectureAdapter.notifyDataSetChanged()
                            onItemClick(
                                Constant.CLICK_SWAP,
                                holder.adapterPosition,
                                sectionData[holder.adapterPosition].fromPosition,
                                sectionData[holder.adapterPosition].toPosition
                            )
                        }
//
                    }
                }
            }
            binding.rvLecture.adapter =
                ChildLectureAdapter(
                    sectionData[position].lessonList,
                    isCreator || userId == data.sectionCreatedById
                ).apply {
                    val itemTouchHelper = ItemTouchHelper(touchHelper)
                    itemTouchHelper.attachToRecyclerView(binding.rvLecture)
                    setOnAdapterItemClickListener(object : IViewClick {
                        override fun onItemClick(vararg items: Any) {
                            if (items.isNotEmpty()) {
                                val type = items[0] as Int
                                val childPosition = items[1] as Int
                                this@AddSectionAdapter.onItemClick(
                                    type,
                                    holder.adapterPosition,
                                    childPosition
                                )
                            }
                        }
                    })
                }
        } else {
            binding.rvLecture.gone()
        }
    }


    override fun getItemCount() = sectionData.size


    class AddSectionViewHolder(var itemBinding: AdapterSectionViewBinding) :
        BaseViewHolder(itemBinding) {
        val textChanges = false
    }
}