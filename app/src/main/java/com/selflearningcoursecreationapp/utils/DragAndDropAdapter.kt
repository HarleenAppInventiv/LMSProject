package com.selflearningcoursecreationapp.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterDragQuestionsBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.getCharString
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants

class DragAndDropAdapter(private var list: ArrayList<QuizOptionData>) :
    BaseAdapter<AdapterDragQuestionsBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.adapter_drag_questions
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterDragQuestionsBinding
        val context = binding.root.context
        binding.llDrag.setTag(position)
        binding.tvTitle.visibleView(list[position].image.isNullOrEmpty())
        binding.ivOption.visibleView(!list[position].image.isNullOrEmpty())

        binding.tvTitle.text = list[position].option1
        binding.ivOption.loadImage(list[position].image, R.drawable.ic_default_banner)
        binding.llDrag.setOnTouchListener(ChoiceTouchListener())
        if (list[position].isSelected == true) {
            binding.llDrag.alpha = 0.5f
            binding.tvOptionNo.changeBackgroundTint(ThemeConstants.TYPE_THEME)
            binding.tvOptionNo.setTextColor(ContextCompat.getColor(context, R.color.white))

        } else {
            binding.llDrag.alpha = 1f
            binding.tvOptionNo.backgroundTintList = null
            binding.tvOptionNo.changeTextColor(ThemeConstants.TYPE_BODY)
        }
        binding.tvOptionNo.text = position.getCharString()
        binding.tvTitle.contentDescription =
            "Option ${binding.tvOptionNo.content()} ${binding.tvTitle.content()}, drag and drop option at the bottom to submit the answer"
        binding.ivOption.contentDescription =
            "Option ${binding.tvOptionNo.content()} image, drag and drop option at the bottom to submit the answer"
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }


    private inner class ChoiceTouchListener : View.OnTouchListener {
        @SuppressLint("NewApi")
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            return if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                /*
             * Drag details: we only need default behavior
             * - clip data could be set to pass data as part of drag
             * - shadow can be tailored
             */
                val data = ClipData.newIntent("draggedData", Intent().apply {
                    putExtra("data", "test")
                })
                val shadowBuilder = View.DragShadowBuilder(view)
                //start dragging the item touched
                view.startDrag(data, shadowBuilder, view, 0)
                true
            } else {
                false
            }
        }
    }


}