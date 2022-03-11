package com.selflearningcoursecreationapp.ui.bottom_more.cards

import android.os.Bundle

import android.view.View
import android.widget.Toast
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCardsBinding
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.HandleClick


class CardsFragment : BaseFragment<FragmentCardsBinding>(), HandleClick, BaseAdapter.IViewClick {
    lateinit var cardDetailsBottomSheet: CardDetailsBottomDialog

    lateinit var adapter: CardsAdapter
    override fun getLayoutRes(): Int {
        return R.layout.fragment_cards
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    private fun initUi() {
        binding.fragCard = this
        cardDetailsBottomSheet = CardDetailsBottomDialog()
        adapter = CardsAdapter()
        binding.rvList.adapter = adapter
        adapter.setOnAdapterItemClickListener(this)


    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.bt_add -> {
                    cardDetailsBottomSheet.show(childFragmentManager, "card details")
                }

            }

        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            var type = items[0] as Int
            var position = items[1] as Int

            when (type) {
                Constant.CLICK_VIEW -> {
                    cardDetailsBottomSheet.show(childFragmentManager, "card details")
                }
                Constant.CLICK_DELETE -> {
                    Toast.makeText(requireContext(), "delete${position}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}