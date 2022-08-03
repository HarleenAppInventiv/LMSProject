package com.selflearningcoursecreationapp.ui.moderator

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentBecomeModertorBinding
import com.selflearningcoursecreationapp.utils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.SpanUtils


class BecomeModeratorFragment : BaseFragment<FragmentBecomeModertorBinding>(), HandleClick {

    override fun getLayoutRes(): Int {

        return R.layout.fragment_become_modertor

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init() {
        binding.rvQualificationDoc.adapter = AdapterBecomeMode()
        binding.handleClick = this
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
//        dialog.window?.setLayout(width, height)

    }

    override fun onApiRetry(apiCode: String) {
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.ev_choose_category -> {

                }
                R.id.tv_course_language -> {

                }
                R.id.bt_enroll -> {
                    CommonAlertDialog.builder(baseActivity)
                        .hideNegativeBtn(true)
                        .title("Under review")
                        .spannedText(
                            SpanUtils.with(
                                baseActivity,
                                "We’ve received your request. Your “become a moderator” request is under review we’ll let you know when it’s done."
                            ).startPos(34).endPos(54).isBold().getSpanString()
                        )
                        .getCallback {
                            baseActivity.goToModeratorActivity()

                        }.notCancellable().icon(R.drawable.ic_under_review)
                        .build()
                }
            }
        }
    }

}