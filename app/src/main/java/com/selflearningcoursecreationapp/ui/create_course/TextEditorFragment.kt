package com.selflearningcoursecreationapp.ui.create_course

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentTextEditorBinding
import com.selflearningcoursecreationapp.utils.HandleClick
import kotlinx.android.synthetic.main.fragment_text_editor.*


class TextEditorFragment : BaseFragment<FragmentTextEditorBinding>(), HandleClick {
    var type = 0
    var htmlValue = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.handleClick = this


        arguments?.let {
            type = it.getInt("type")
            htmlValue = it.getString("htmlValue").toString()
//            showToastShort(htmlValue)
            if (htmlValue.isEmpty()) {
                textEditor.setPlaceholder("Enter description")
            } else {
                textEditor.html = htmlValue
            }
        }

        binding.btAddText.setOnClickListener {

            requireActivity().supportFragmentManager.setFragmentResult(
                "valueHTML",
                bundleOf("value" to textEditor.html.toString(), "type" to type)
            )
            findNavController().navigateUp()
        }


    }

    override fun getLayoutRes() = R.layout.fragment_text_editor
    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.action_undo -> {
                    textEditor.undo()
                }
                R.id.action_redo -> {
                    textEditor.redo()
                }
                R.id.action_bold -> {
                    textEditor.setBold()
                }
                R.id.action_italic -> {
                    textEditor.setItalic()
                }
                R.id.action_underline -> {
                    textEditor.setUnderline()
                }
                R.id.action_align_left -> {
                    textEditor.setAlignLeft()
                }
                R.id.action_align_centre -> {
                    textEditor.setAlignCenter()
                }
                R.id.action_align_right -> {
                    textEditor.setAlignRight()
                }
                R.id.action_bullet -> {
                    textEditor.setBullets()
                }
                R.id.action_number_bullet -> {
                    textEditor.setNumbers()
                }
            }
        }
    }

}