package com.selflearningcoursecreationapp.ui.moderator.qualification

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentModeDocumentsBinding
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.models.DocModelItem
import com.selflearningcoursecreationapp.utils.Constant
import org.koin.androidx.viewmodel.ext.android.viewModel

class ModeCertificateFragment : BaseFragment<FragmentModeDocumentsBinding>(),
    BaseAdapter.IViewClick {
    private val viewModel: ModCertificateVM by viewModel()
    private var adapter: ModeCertificateDocumentsAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_mode_documents
    }

//    override fun <T> onResponseSuccess(value: T, apiCode: String) {
//        super.onResponseSuccess(value, apiCode)
//        when (apiCode) {
//            ApiEndPoints.API_DOC_DOWNLOAD -> {
//                (value as BaseResponse<ArrayList<DocModelItem>>).let {
//                    setAdapter(it)
//
//                }
//            }
//        }
//    }

    override fun onApiRetry(apiCode: String) {

    }

    override fun onResume() {
        super.onResume()
        init()

    }

    private fun init() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        observe()
        viewModel.myDoc()
        callMenu()
    }


    private fun observe() {
        viewModel.myDocLiveData.observe(viewLifecycleOwner) {
            setAdapter(it)
        }
    }


    private fun setAdapter(response: ArrayList<DocModelItem>) {
        adapter = ModeCertificateDocumentsAdapter(response)
        binding.rvModeDocuments.adapter = adapter
        adapter?.setOnAdapterItemClickListener(this)
    }

    override fun onItemClick(vararg items: Any) {
        val type = items[0] as Int
        val position = items[1] as Int
        when (type) {
            Constant.CLICK_VIEW -> {
                val url = items[2] as String

                findNavController().navigateTo(
                    R.id.action_fragment_mode_doc_to_viewQualificationFragment,
                    bundleOf(
                        "fileUri" to viewModel.myDocLiveData.value?.get(position)?.contentURL,
                        "fileName" to viewModel.myDocLiveData.value?.get(position)?.contentName
                    )
                )
            }

        }
    }
}