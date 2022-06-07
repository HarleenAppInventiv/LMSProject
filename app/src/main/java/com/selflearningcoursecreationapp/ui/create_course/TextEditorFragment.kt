package com.selflearningcoursecreationapp.ui.create_course

import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentTextEditorBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.extensions.wordCount
import com.selflearningcoursecreationapp.textEditor.TextEditor
import com.selflearningcoursecreationapp.utils.HandleClick


class TextEditorFragment : BaseFragment<FragmentTextEditorBinding>(), HandleClick,
    TextEditor.OnTextChangeListener {
    var type = 0
    var htmlValue = ""
    var from = 0
    var count = -1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.handleClick = this
        binding.textEditor.setOnTextChangeListener(this)
        binding.textEditor.requestFocus()


        arguments?.let {
            type = it.getInt("type")
            htmlValue = it.getString("htmlValue").toString()
            from = it.getInt("from")
            binding.tvWordCount.text = (Html.fromHtml(htmlValue).toString()).wordCount().toString()

            if (htmlValue.endsWith("</b>")) {
                binding.actionBold.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
            }
            if (htmlValue.endsWith("</i>")) {

                binding.actionItalic.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
            }
            if (htmlValue.endsWith("</u>")) {
                binding.actionUnderline.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
            }
            if (htmlValue.endsWith("</i></b>") || htmlValue.endsWith("</b></i>")) {
                binding.actionBold.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
                binding.actionItalic.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
            }
            if (htmlValue.endsWith("</i></u>") || htmlValue.endsWith("</u></i>")) {
                binding.actionItalic.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
                binding.actionUnderline.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
            }
            if (htmlValue.endsWith("</b></u>") || htmlValue.endsWith("</u></b>")) {
                binding.actionUnderline.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
                binding.actionBold.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
            }
            if (htmlValue.endsWith("</b></u></i>") || htmlValue.endsWith("</b></i></u>") || htmlValue.endsWith(
                    "</i></b></u>"
                )
                || htmlValue.endsWith("</i></u></b>") || htmlValue.endsWith("</u></i></b>") || htmlValue.endsWith(
                    "</u></b></i>"
                )
            ) {
                binding.actionUnderline.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
                binding.actionBold.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
                binding.actionItalic.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange_text_color
                    )
                )
            }

            if (from == 1) {
                binding.tvWordCount.visible()
                binding.tvWordCountTitle.visible()
            } else {
                binding.tvWordCount.gone()
                binding.tvWordCountTitle.gone()
            }
            if (htmlValue.isEmpty()) {
                binding.textEditor.setPlaceholder("Enter description")
                binding.btAddText.isEnabled = false
                binding.textEditor.focusEditor()


            } else {
                binding.textEditor.html = htmlValue + ""
                binding.textEditor.focusEditor()


            }
        }

        binding.btAddText.setOnClickListener {
            showLog("RESULT_LiSTENER", "data >> ${binding.textEditor.html.toString()}")

            setFragmentResult(
                "valueHTML",
                bundleOf("value" to binding.textEditor.html.toString(), "type" to type)
            )
            findNavController().popBackStack()

        }


    }

    override fun getLayoutRes() = R.layout.fragment_text_editor
    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.action_undo -> {
                    binding.textEditor.undo()
                }
                R.id.action_redo -> {
                    binding.textEditor.redo()
                }
                R.id.action_bold -> {
                    binding.textEditor.setBold()
                    if (binding.actionBold.background == null)
                        binding.actionBold.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.orange_text_color
                            )
                        )
                    else
                        binding.actionBold.background = null
                }
                R.id.action_italic -> {
                    binding.textEditor.setItalic()
                    if (binding.actionItalic.background == null)
                        binding.actionItalic.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.orange_text_color
                            )
                        )
                    else
                        binding.actionItalic.background = null

                }
                R.id.action_underline -> {
                    binding.textEditor.setUnderline()
                    if (binding.actionUnderline.background == null)
                        binding.actionUnderline.setBackgroundColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.orange_text_color
                            )
                        )
                    else
                        binding.actionUnderline.background = null
                }
                R.id.action_align_left -> {
                    binding.textEditor.setAlignLeft()
                }
                R.id.action_align_centre -> {
                    binding.textEditor.setAlignCenter()
                }
                R.id.action_align_right -> {
                    binding.textEditor.setAlignRight()
                }
                R.id.action_bullet -> {
                    binding.textEditor.setBullets()
                }
                R.id.action_number_bullet -> {
                    binding.textEditor.setNumbers()
                }
            }
        }
    }

    override fun onTextChange(text1: String?) {

        count = (Html.fromHtml(text1).toString()).wordCount()
        binding.btAddText.isEnabled = if (count == 0 || count > 500) false else true

        binding.tvWordCount.apply {
            text = count.toString()
            if (count < 500) {
                setTextColor(ContextCompat.getColor(context, R.color.black))
            } else {
                showToastShort("Reached max limit")
                setTextColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
            }
        }


    }

    override fun onResume() {
        super.onResume()
        baseActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun onPause() {
        super.onPause()
        baseActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

    }


}