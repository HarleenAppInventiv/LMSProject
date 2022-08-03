package com.selflearningcoursecreationapp.ui.moderator.moderatorHome

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentRequestBinding
import com.selflearningcoursecreationapp.ui.moderator.dialog.ReasonForRejectionDialogue
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ModeratorListType


class RequestFragment : BaseFragment<FragmentRequestBinding>(), BaseAdapter.IViewClick {
    lateinit var adapter: AdapterRequestList
    override fun getLayoutRes() = R.layout.fragment_request
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        adapter = AdapterRequestList(ModeratorListType.REQUESTED)
        adapter.setOnAdapterItemClickListener(this)

        binding.rvRequestedList.adapter = adapter
        adapter.setOnAdapterItemClickListener(this)

    }

    override fun onApiRetry(apiCode: String) {
    }

    override fun onItemClick(vararg items: Any) {
        var type = items[0] as Int
        var position = items[1] as Int

        when (type) {
            Constant.CLICK_ACCEPT -> {
                findNavController().navigate(R.id.action_moderatorBaseFragment_to_modCourseDetailsFragment)
            }
            Constant.CLICK_REJECT -> {
//                findNavController().navigate(R.id.action_moderatorBaseFragment_to_notificationFragment)
                ReasonForRejectionDialogue().show(childFragmentManager, "")
            }

        }
    }


}