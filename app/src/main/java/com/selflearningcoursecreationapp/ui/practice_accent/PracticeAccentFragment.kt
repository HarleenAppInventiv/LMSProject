package com.selflearningcoursecreationapp.ui.practice_accent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.databinding.FragmentPracticeAccentBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.onRightDrawableClick
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.utils.Constants
import com.selflearningcoursecreationapp.utils.HandleClick
import com.selflearningcoursecreationapp.utils.LanguageConstant
import com.selflearningcoursecreationapp.utils.SpeechUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class PracticeAccentFragment : BaseFragment<FragmentPracticeAccentBinding>(), HandleClick {

    private val viewModel: PracticeAccentVM by viewModel()
    override fun getLayoutRes() = R.layout.fragment_practice_accent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }


    private fun initUI() {

        binding.etSearch.onRightDrawableClick {
            val speechUtils: SpeechUtils by inject()
            speechUtils.changeLanguage(Locale(viewModel.getToLanguage()))
            speechUtils.speakToUser(binding.etSearchDescription.content())

        }
        binding.etSearchDescription.onRightDrawableClick {
            val speechUtils: SpeechUtils by inject()
            speechUtils.changeLanguage(Locale(viewModel.getToLanguage()))
            speechUtils.speakToUser(binding.etSearchDescription.content())
        }
        binding.handleClick = this
        if (viewModel.getToLanguage().isEmpty()) {
            lifecycleScope.launch {
                val code = withContext(lifecycleScope.coroutineContext) {
                    PreferenceDataStore.getString(Constants.LANGUAGE_THEME)
                }
                viewModel.setToLanguage(
                    code ?: LanguageConstant.ENGLISH
                )

            }
        }

        setData()
        viewModel.translatedText.observe(viewLifecycleOwner, {

            binding.etSearchDescription.setText(it)
        })

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("main", "beforeTextChanged: ")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (!p0.isNullOrEmpty()) {
                    viewModel.translateData(p0.toString())
                }

            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("main", "beforeTextChanged: ")
            }
        })

        setFragmentResultListener("languageData") { _, bundle ->
            val languageCode = bundle.getString("language_code") ?: LanguageConstant.ENGLISH
            when (bundle.getInt("type")) {
                1 -> {
                    viewModel.setFromLanguage(languageCode)

                }
                2 -> {
                    viewModel.setToLanguage(languageCode)

                }
            }

            setData()
            if (binding.etSearch.content().isNotEmpty())
                viewModel.translateData(binding.etSearch.content())

        }
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.tv_from -> {
                    findNavController().navigate(
                        R.id.action_practiceAccentFragment_to_selectLanguageFragment,
                        bundleOf("type" to 1)
                    )
                }
                R.id.tv_to -> {
                    findNavController().navigate(
                        R.id.action_practiceAccentFragment_to_selectLanguageFragment,
                        bundleOf("type" to 2)
                    )
                }
            }
        }
    }


    private fun setData() {
        binding.tvTo.text = viewModel.getLanguageCode(viewModel.getToLanguage())
        binding.tvFrom.text = viewModel.getLanguageCode(viewModel.getFromLanguage())


        viewModel.initTranslator()
        viewModel.getTranslator()?.let { lifecycle.addObserver(it) }

    }
//
//    private fun speechToText() {
//        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        intent.putExtra(
//            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
//        )
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale(viewModel.getFromLanguage()))
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, viewModel.getFromLanguage())
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, viewModel.getFromLanguage());
//        intent.putExtra(
//            RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
//            viewModel.getFromLanguage()
//        );
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak")
//        try {
//            startActivityForResult(intent, 10)
//        } catch (a: ActivityNotFoundException) {
//            Toast.makeText(
//                baseActivity,
//                "Sorry your device not supported",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 10) {
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