package com.selflearningcoursecreationapp.ui.bottom_more.cards

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCardsBinding
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.HandleClick


class CardsFragment : BaseFragment<FragmentCardsBinding>(), HandleClick, BaseAdapter.IViewClick,
    BaseBottomSheetDialog.IDialogClick {
    private lateinit var cardDetailsBottomSheet: CardDetailsBottomDialog

    lateinit var adapter: CardsAdapter
    override fun getLayoutRes(): Int {
        return R.layout.fragment_cards
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }


    private fun initUi() {
        binding.fragCard = this
        cardDetailsBottomSheet = CardDetailsBottomDialog()
        adapter = CardsAdapter()
        binding.rvList.adapter = adapter
        adapter.setOnAdapterItemClickListener(this)

        cardDetailsBottomSheet.setOnDialogClickListener(this)


    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.bt_add -> {
                    cardDetailsBottomSheet.apply {
                        arguments = bundleOf("type" to Constant.CLICK_ADD)
                    }
                    cardDetailsBottomSheet.show(childFragmentManager, "card details")
                }

            }

        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int

            when (type) {
                Constant.CLICK_VIEW -> {
                    cardDetailsBottomSheet.apply {
                        arguments = bundleOf("type" to Constant.CLICK_EDIT, "position" to position)
                    }
                    cardDetailsBottomSheet.show(childFragmentManager, "card details")
                }
                Constant.CLICK_DELETE -> {
                    Toast.makeText(requireContext(), "delete${position}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int
            when (type) {
                Constant.CLICK_ADD -> {
                    Toast.makeText(requireContext(), "Add${position}", Toast.LENGTH_SHORT).show()
                }
                Constant.CLICK_EDIT -> {
                    Toast.makeText(requireContext(), "Edit${position}", Toast.LENGTH_SHORT).show()

                }
                else -> {
                    Toast.makeText(requireContext(), "fsfdsfs", Toast.LENGTH_SHORT).show()

                }
            }
        }

    }

    override fun onApiRetry(apiCode: String) {

    }


}