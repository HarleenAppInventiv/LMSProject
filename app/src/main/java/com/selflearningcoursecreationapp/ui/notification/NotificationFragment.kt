package com.selflearningcoursecreationapp.ui.notification

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentNotificationBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.NotificationData
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.ui.moderator.ModeratorActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), BaseAdapter.IViewClick {

    private val viewModel: NotificationVM by viewModel()
    private var adapter: AdapterNotificationChild? = null
    override fun getLayoutRes() = R.layout.fragment_notification

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callMenu()
        initUI()
    }

    fun refreshData() {
        viewModel.getNotification()
    }

    private fun initUI() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        viewModel.getNotification()

        observerNotificationData()

        binding.tvClear.setOnClickListener {
            viewModel.deleteAllNotification()
        }
        binding.tvReadAll.setOnClickListener {
            viewModel.readAllNotification()
        }
    }

    private fun observerNotificationData() {
        viewModel.notificationLiveData.observe(viewLifecycleOwner) {
            viewModel.myCategories()

        }
        viewModel.myCategoriesLiveData.observe(viewLifecycleOwner) {
            setAdapter()
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_GET_NOTIFICATION -> {
                if (viewModel.apiType == 2) {
                    if (viewModel.notificationLiveData.value?.isEmpty() == true) {
                        setAdapter()
                    } else {
                        viewModel.notificationLiveData.value?.also {
                            it.removeAt(viewModel.adapterPosition)
                            setVisibilityOfViews()
                        }
                        adapter?.notifyItemRemoved(viewModel.adapterPosition)
                        adapter?.notifyItemRangeChanged(
                            viewModel.adapterPosition,
                            viewModel.notificationLiveData.value?.size ?: 0
                        );
                    }

                } else if (viewModel.apiType == 3) {
                    var data = NotificationData()
                    viewModel.notificationLiveData.value?.also {
                        data = it[viewModel.adapterPosition].apply {
                            isRead = true
                        }
                    }
                    adapter?.notifyItemChanged(viewModel.adapterPosition)

                }

            }
            ApiEndPoints.API_NOTIFICATION_READ_ALL -> {
                viewModel.notificationLiveData.value = viewModel.notificationLiveData.value?.apply {
                    forEach {
                        it.isRead = true
                    }
                }
            }
            ApiEndPoints.API_NOTIFICATION_DELETE_ALL -> {
                setAdapter()
            }
        }
    }

    private fun setVisibilityOfViews() {
        if (viewModel.notificationLiveData.value.isNullOrEmpty()) {
            binding.llNoNotification.visible()
            binding.rvNotificationParent.gone()
            binding.tvClear.gone()
            binding.tvReadAll.gone()
        } else {
            binding.rvNotificationParent.visible()
            binding.tvClear.visible()
            binding.tvReadAll.visible()
            binding.llNoNotification.gone()
        }
    }


    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        viewModel.adapterPosition = items[1] as Int
        when (type) {
            Constant.CLICK_DELETE -> {

                viewModel.deleteNotification()
            }
            Constant.CLICK_VIEW -> {
                val data = items[2] as NotificationData
                val bundle = bundleOf(
                    "courseId" to data.courseId.toString(),
                    "body" to data.desciption,
                    "type" to data.type,
                    "title" to data.title,
                    "notification_Payload" to data.notificationPayload
                )
                viewModel.patchNotification()

                if (baseActivity is HomeActivity) {
                    (baseActivity as HomeActivity).handleNotification(bundle, true)
                } else if (baseActivity is ModeratorActivity) {
                    (baseActivity as ModeratorActivity).handleNotification(
                        bundle,
                        true,
                        viewModel.myCategoriesLiveData.value?.languages?.size ?: 0
                    )

                }
            }

        }
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    fun setAdapter() {
        if (viewModel.notificationLiveData.value.isNullOrEmpty()) {
            binding.llNoNotification.visible()
            binding.rvNotificationParent.gone()
            binding.tvClear.gone()
            binding.tvReadAll.gone()
        } else {
            binding.rvNotificationParent.visible()
            binding.tvClear.visible()
            binding.tvReadAll.visible()
            binding.llNoNotification.gone()

            adapter = AdapterNotificationChild(viewModel.notificationLiveData.value ?: ArrayList())
            binding.rvNotificationParent.adapter = adapter
            adapter?.setOnAdapterItemClickListener(this)
        }

    }
}