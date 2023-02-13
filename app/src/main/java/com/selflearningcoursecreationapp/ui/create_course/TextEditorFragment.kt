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
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.textEditor.TextEditor
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.ValidationConst
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils


class TextEditorFragment : BaseFragment<FragmentTextEditorBinding>(), HandleClick,
    TextEditor.OnTextChangeListener, TextEditor.OnDecorationStateListener {
    private var type = 0
    private var htmlValue = ""
    private var from = 0
    private var count = -1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initUI()

    }


    private fun initUI() {
        binding.handleClick = this
        binding.textEditor.setOnTextChangeListener(this)
        binding.textEditor.setOnDecorationChangeListener(this)
        binding.textEditor.requestFocus()
        binding.root.fitSystemWindowsAndAdjustResize()

        binding.textEditor.setEditorFontSize(14)
        binding.textEditor.setEditorFontColor(ThemeUtils.getPrimaryTextColor(baseActivity))
        arguments?.let {
            type = it.getInt("type")
            htmlValue = it.getString("htmlValue").toString()
            from = it.getInt("from")
            binding.tvWordCount.text = (Html.fromHtml(htmlValue).toString()).wordCount().toString()

            if (from == 1) {
                binding.tvWordCount.visible()
                binding.tvWordCountTitle.visible()
            } else {
                binding.tvWordCount.gone()
                binding.tvWordCountTitle.gone()
            }
            if (htmlValue.isEmpty()) {
                binding.textEditor.setPlaceholder(getString(R.string.enter_descriptions))
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
                            ThemeUtils.getBtnBgColor(baseActivity)
                        )
                    else
                        binding.actionBold.background = null
                }
                R.id.action_italic -> {
                    binding.textEditor.setItalic()
                    if (binding.actionItalic.background == null)
                        binding.actionItalic.setBackgroundColor(
                            ThemeUtils.getBtnBgColor(baseActivity)
                        )
                    else
                        binding.actionItalic.background = null

                }
                R.id.action_underline -> {
                    binding.textEditor.setUnderline()
                    if (binding.actionUnderline.background == null)
                        binding.actionUnderline.setBackgroundColor(
                            ThemeUtils.getBtnBgColor(baseActivity)
                        )
                    else
                        binding.actionUnderline.background = null
                }
                R.id.action_align_left -> {

                    binding.textEditor.setAlignLeft()
                    binding.actionAlignCentre.background = null
                    binding.actionAlignRight.background = null

//                    if (binding.actionAlignLeft.background == null)
                    binding.actionAlignLeft.setBackgroundColor(
                        ThemeUtils.getBtnBgColor(baseActivity)

                    )
//                    else
//                        binding.actionAlignLeft.background = null
                }
                R.id.action_align_centre -> {
                    binding.textEditor.setAlignCenter()
                    binding.actionAlignLeft.background = null
                    binding.actionAlignRight.background = null

//                    if (binding.actionAlignCentre.background == null)
                    binding.actionAlignCentre.setBackgroundColor(
                        ThemeUtils.getBtnBgColor(baseActivity)
                    )
//                    else
//                        binding.actionAlignCentre.background = null
                }
                R.id.action_align_right -> {
                    binding.textEditor.setAlignRight()
                    binding.actionAlignLeft.background = null
                    binding.actionAlignCentre.background = null

//                    if (binding.actionAlignRight.background == null)
                    binding.actionAlignRight.setBackgroundColor(
                        ThemeUtils.getBtnBgColor(baseActivity)
                    )
//                    else
//                        binding.actionAlignRight.background = null
                }
                R.id.action_bullet -> {
                    binding.actionNumberBullet.background = null
                    binding.textEditor.setBullets()
                    if (binding.actionBullet.background == null) {

//                        binding.textEditor.setAlignLeft()
                        binding.actionBullet.setBackgroundColor(
                            ThemeUtils.getBtnBgColor(
                                baseActivity
                            )
                        )
                    } else
                        binding.actionBullet.background = null
                }
                R.id.action_number_bullet -> {
                    binding.textEditor.setNumbers()
                    binding.actionBullet.background = null
                    if (binding.actionNumberBullet.background == null) {

//                        binding.textEditor.setAlignLeft()
                        binding.actionNumberBullet.setBackgroundColor(
                            ThemeUtils.getBtnBgColor(
                                baseActivity
                            )
                        )
                    } else
                        binding.actionNumberBullet.background = null
                }
            }
        }
    }

    override fun onTextChange(text1: String?) {

        count = (Html.fromHtml(text1).toString()).wordCount()
        binding.btAddText.isEnabled =
            !(count == 0 || count > ValidationConst.MAX_COURSE_DESC_LENGTH)



        binding.tvWordCount.apply {
            text = count.toString()
            if (count < ValidationConst.MAX_COURSE_DESC_LENGTH) {
                setTextColor(ContextCompat.getColor(context, R.color.black))
            } else if (count == ValidationConst.MAX_COURSE_DESC_LENGTH) {
                setTextColor(
                    baseActivity.getAttrResource(R.attr.accentColor_Red)
                )
            } else {
                showToastShort(context.getString(R.string.reached_max_limit))
                setTextColor(
                    baseActivity.getAttrResource(R.attr.accentColor_Red)
                )

            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }

    override fun onStateChangeListener(text: String?, types: List<TextEditor.Type>?) {
        try {
            if (types!!.contains(TextEditor.Type.BOLD))
                binding.actionBold.setBackgroundColor(
                    ThemeUtils.getBtnBgColor(baseActivity)

                )
            else
                binding.actionBold.background = null
            if (types!!.contains(TextEditor.Type.ITALIC))
                binding.actionItalic.setBackgroundColor(
                    ThemeUtils.getBtnBgColor(baseActivity)

                )
            else
                binding.actionItalic.background = null

            if (types!!.contains(TextEditor.Type.UNDERLINE))
                binding.actionUnderline.setBackgroundColor(
                    ThemeUtils.getBtnBgColor(baseActivity)

                )
            else
                binding.actionUnderline.background = null

            if (types!!.contains(TextEditor.Type.JUSTIFYCENTER)) {
                binding.actionAlignLeft.background = null
                binding.actionAlignRight.background = null
                binding.actionAlignCentre.setBackgroundColor(
                    ThemeUtils.getBtnBgColor(baseActivity)

                )
            } else if (types!!.contains(TextEditor.Type.JUSTIFYRIGHT)) {
                binding.actionAlignLeft.background = null
                binding.actionAlignCentre.background = null
                binding.actionAlignRight.setBackgroundColor(
                    ThemeUtils.getBtnBgColor(baseActivity)

                )
            } else if (types!!.contains(TextEditor.Type.JUSTIFYLEFT)) {

                binding.actionAlignCentre.background = null
                binding.actionAlignRight.background = null
                binding.actionAlignLeft.setBackgroundColor(
                    ThemeUtils.getBtnBgColor(baseActivity)

                )
            }

            if (types!!.contains(TextEditor.Type.UNORDEREDLIST)) {
                binding.actionNumberBullet.background = null
//                binding.textEditor.setAlignLeft()

                binding.actionBullet.setBackgroundColor(
                    ThemeUtils.getBtnBgColor(baseActivity)

                )
            } else if (types!!.contains(TextEditor.Type.ORDEREDLIST)) {
                binding.actionBullet.background = null
//                binding.textEditor.setAlignLeft()

                binding.actionNumberBullet.setBackgroundColor(
                    ThemeUtils.getBtnBgColor(baseActivity)

                )
            } else {
                binding.actionBullet.background = null
                binding.actionNumberBullet.background = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//            super.onApplyWindowInsets(
//                insets.replaceSystemWindowInsets(
//                    0,
//                    0,
//                    0,
//                    insets.getSystemWindowInsetBottom()
//                )
//            )
//        } else {
//            insets
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.clearFlags(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }


}