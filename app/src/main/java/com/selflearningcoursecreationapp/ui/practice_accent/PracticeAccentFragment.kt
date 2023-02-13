package com.selflearningcoursecreationapp.ui.practice_accent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentPracticeAccentBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.CategoryData
import com.selflearningcoursecreationapp.models.user.PreferenceData
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.PREFERENCES
import com.selflearningcoursecreationapp.utils.RequestCode
import com.selflearningcoursecreationapp.utils.SpeechUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class PracticeAccentFragment : BaseFragment<FragmentPracticeAccentBinding>(), HandleClick {

    private val viewModel: PracticeAccentVM by viewModel()
    override fun getLayoutRes() = R.layout.fragment_practice_accent
    private val speechUtils: SpeechUtils by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        callMenu()

    }


    private fun initUI() {
        binding.fabAdd.setThemeTint()

        binding.etSearch.onRightDrawableClick {
            speechUtils.changeLanguage(Locale(viewModel.getFromLanguage()))
            speechUtils.speakToUser(binding.etSearch.content())

        }

        binding.fabAdd.setOnClickListener {
            speechUtils.changeLanguage(Locale("en", "IN"))
            speechUtils.speakToUser(binding.etSearchDescription.content())

        }
        binding.handleClick = this

//        if (viewModel.getFromLanguage().isEmpty()) {
//
//            binding.tvFrom.setText("Select Typing Language ")
////            lifecycleScope.launch {
////                val code = withContext(lifecycleScope.coroutineContext) {
////                    PreferenceDataStore.getString(Constants.LANGUAGE_THEME)
////                }
////                val codeId = withContext(lifecycleScope.coroutineContext) {
////                    PreferenceDataStore.getInt(Constants.LANGUAGE_ID)
////                }
////                viewModel.setFromLanguage(
////                    code ?: LanguageConstant.ENGLISH
////                )
////                viewModel.setFromLanguageId(codeId ?: 0)
////
////
////            }
//        }

        setData()
        if (binding.etSearch.content().isEmpty())
            binding.etSearchDescription.setText("")

        viewModel.translateData(binding.etSearch.text.toString())

        viewModel.translatedText.observe(viewLifecycleOwner) {
            if (binding.etSearch.content().isEmpty()) {
                binding.etSearchDescription.setText("")

            } else {
                binding.etSearchDescription.setText(it)

            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("main", "beforeTextChanged: ")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }

            override fun afterTextChanged(p0: Editable?) {

                Handler().postDelayed({
                    if (!p0.isNullOrEmpty()) {
                        viewModel.translateData(p0.toString())
                    } else {
                        viewModel.translateData(p0.toString())

                        viewModel.translatedText.value = ""
                        binding.etSearchDescription.setText("")
                    }
                }, 300)

            }
        })

        setFragmentResultListener("preferenceData") { _, bundle ->
            val value = bundle.getParcelableArrayList<CategoryData>("data")
            val languageCode =
                if (value.isNullOrEmpty()) getString(R.string.nothing) else value?.get(0)?.code
                    ?: getString(
                        R.string.null_data
                    )
            when (bundle.getInt("clickType")) {
//                Constant.CLICK_VIEW -> {
//                    viewModel.setFromLanguage(languageCode)
//
//                    setData()
////                    if (binding.etSearch.content().isNotEmpty())
////                        viewModel.translateData(binding.etSearch.content())
//
//                }
                2 -> {
                    viewModel.setFromLanguage(languageCode)
                    viewModel.setFromLanguageId(value?.get(0)?.id ?: 0)
                    setData()
                    binding.etSearch.setText("")
                    lifecycleScope.launch {
                        delay(500)
                        baseActivity.runOnUiThread {
                            if (!binding.etSearch.text.toString().isNullOrEmpty()) {
                                viewModel.translateData(binding.etSearch.text.toString())

//                        if (binding.etSearch.content().isNotEmpty())
//                            viewModel.translateData(binding.etSearch.content())
                            } else {
                                binding.etSearchDescription.setText("")
                            }
                        }
                    }


                }
            }


        }
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.tv_from -> {
                    val preferenceData = PreferenceData(
                        title = baseActivity.getString(R.string.select_language),
                        type = PREFERENCES.TYPE_LANGUAGE,
                        screenType = PREFERENCES.SCREEN_SELECT,
                        selectedValues = ArrayList<Int>().apply {
                            add(viewModel.getFromLanguageId())
                        },
                        clickType = 2
                    )
                    findNavController().navigateTo(
                        R.id.action_global_preferencesFragment,
                        bundleOf("preferenceData" to preferenceData)
                    )
                }
            }
        }
    }

    //
    private fun setData() {
//        binding.tvTo.text = viewModel.getLanguageCode(viewModel.getToLanguage())

        if (viewModel.getFromLanguage().isEmpty()) {
            binding.tvFrom.text = baseActivity.getString(R.string.select_typing_language)
            binding.etSearch.isEnabled = false
            binding.LMSTextView4.text = baseActivity.getString(R.string.your_text)


        } else {
            binding.LMSTextView4.text = "${baseActivity.getString(R.string.your_text)} ( ${
                viewModel.getLanguageCode(viewModel.getFromLanguage())
            } )"
            binding.etSearch.isEnabled = true
            binding.tvFrom.text = viewModel.getLanguageCode(viewModel.getFromLanguage())

        }
        viewModel.initTranslator()
        viewModel.getTranslator()?.let { lifecycle.addObserver(it) }
//
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.PRACTICE_ACCENT) {
//            if (requestCode == 10) {
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.forEachIndexed { index, s ->
                    showLog("SPEECH_DATA", s)
                    if (index == 0) {
                        binding.etSearch.setText(s)

                    }
                }


//            }
        }
    }

    override fun onApiRetry(apiCode: String) {

    }

}