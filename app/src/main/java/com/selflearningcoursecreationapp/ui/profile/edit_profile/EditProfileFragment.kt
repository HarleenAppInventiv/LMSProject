package com.selflearningcoursecreationapp.ui.profile.edit_profile

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentEditProfileBinding
import com.selflearningcoursecreationapp.extensions.getStringDate
import com.selflearningcoursecreationapp.extensions.openDatePickerDialog
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.models.*
import com.selflearningcoursecreationapp.ui.dialog.ChooseGenderDialog
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceBottomDialog
import com.selflearningcoursecreationapp.utils.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(), View.OnClickListener,
    BaseBottomSheetDialog.IDialogClick, HandleClick {
    private val viewModel: EditProfileViewModel by viewModel()
    var stateId = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()


    }

    fun init() {
        binding.editProfile = viewModel
        binding.handleClick = this
        binding.evChooseProfession.setText(viewModel.userData.value?.profession?.name)
        binding.evBio.setText(viewModel.userData.value?.bio)
        binding.edtChooseState.setText(viewModel.userData.value?.state)
        binding.edtChooseCity.setText(viewModel.userData.value?.city)
        binding.evChooseGender.setText(viewModel.userData.value?.genderName)
        binding.edtChooseCity.setText(viewModel.userData.value?.city)

        setHasOptionsMenu(true)
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.edtUserDob.setOnClickListener(this)

        binding.textView6.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.personal_details))
                .endPos(8).isBold()
                .textColor(ContextCompat.getColor(baseActivity, R.color.heading_color_262626))
                .getSpanString()
        )
        binding.textView7.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.address_details)).endPos(7)
                .isBold()
                .textColor(ContextCompat.getColor(baseActivity, R.color.heading_color_262626))
                .getSpanString()
        )

        binding.btnSave.setOnClickListener {
            viewModel.saveEditProfile(binding.countryCodePicker.selectedCountryCodeWithPlus)
        }

        binding.countryCodePicker.apply {

            setCountryForPhoneCode(
                viewModel.userData?.value?.countryCode?.subSequence(
                    1,
                    viewModel.userData?.value?.countryCode?.length ?: 0
                )?.toString()?.toInt() ?: 91
            )
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }


    override fun getLayoutRes() = R.layout.fragment_edit_profile
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.edt_user_dob -> {
                baseActivity.openDatePickerDialog(setMaxDate = true) {
                    viewModel.userData.value?.apply {
                        it.set(Calendar.HOUR_OF_DAY, 0)
                        it.set(Calendar.MINUTE, 0)
                        it.set(Calendar.SECOND, 0)
                        dob = it.time.getStringDate("yyyy-MM-dd'T'HH:mm:ss")
                        notifyChange()
                    }
                }
            }
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        val response = value as? BaseResponse<*>
        when (apiCode) {
            ApiEndPoints.API_UPDATE_PROFILE -> {
                showToastShort(response?.message.toString())
                findNavController().popBackStack()
            }
            ApiEndPoints.API_GET_ALL_STATES -> {
                val result = response?.resource as ArrayList<StateModel>
                result.let {
                    StateListDialog().apply {
                        arguments = bundleOf(
                            "data" to it,
                            "title" to this@EditProfileFragment.baseActivity.getString(R.string.select_state)
                        )
                        setOnDialogClickListener(this@EditProfileFragment)
                    }.show(childFragmentManager, "")
                }


            }
            ApiEndPoints.API_GET_ALL_CITY -> {
                var result = response?.resource as? ArrayList<CityModel>
                result?.let {
                    CityListDialog().apply {
                        arguments = bundleOf(
                            "data" to it,
                            "title" to this@EditProfileFragment.baseActivity.getString(R.string.select_city)
                        )
                        setOnDialogClickListener(this@EditProfileFragment)
                    }.show(childFragmentManager, "")
                }

            }


        }


    }

    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            var value = items[1]

            when (type) {
                DialogType.PROFESSION -> {
                    value as SingleChoiceData
                    binding.evChooseProfession.setText(value.title)
                    viewModel.userData.value?.professionId = value.id.toString()

                }
                DialogType.STATE -> {
                    value as StateModel
                    stateId = value.stateId!!
                    binding.edtChooseState.setText(value.stateName)
                    if (!value.stateId?.toString().equals(viewModel.userData.value?.stateId)) {
                        binding.edtChooseCity.text?.clear()
                        viewModel.userData.value?.cityId = ""
                    }
                    viewModel.userData.value?.stateId = value.stateId.toString()

                }
                DialogType.CITY -> {
                    value as CityModel
                    binding.edtChooseCity.setText(value.cityName)
                    viewModel.userData.value?.cityId = value.cityId.toString()

                }

                DialogType.GENDER -> {
                    value as GenderModel
                    binding.evChooseGender.setText(value.genderName)
                    viewModel.userData.value?.genderId = value.genderId.toString()

                }
            }

        }
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.ev_choose_profession -> {
                    SingleChoiceBottomDialog().apply {
                        arguments = bundleOf(
                            "type" to DialogType.PROFESSION,
                            "title" to this@EditProfileFragment.baseActivity.getString(R.string.select_profession),
                            "id" to if (!viewModel.userData.value?.professionId.isNullOrEmpty()) {
                                viewModel.userData.value?.professionId!!.toInt()
                            } else 0
                        )
                        setOnDialogClickListener(this@EditProfileFragment)
                    }.show(childFragmentManager, "")
                }
                R.id.edt_choose_state -> {
                    viewModel.getStateList()

                }
                R.id.edt_choose_city -> {
                    if (stateId == 0) showToastShort(getString(R.string.select_state_first)) else viewModel.getCityList(
                        stateId
                    )
                }
                R.id.ev_choose_gender -> {
                    ChooseGenderDialog().apply {
                        if (!viewModel.userData.value?.genderId.isNullOrEmpty() && !viewModel.userData.value?.genderId.equals(
                                "null"
                            )
                        ) {
                            arguments =
                                bundleOf("id" to viewModel.userData.value?.genderId!!.toInt())
                        }
                        setOnDialogClickListener(this@EditProfileFragment)
                    }.show(childFragmentManager, "")
                }

            }

        }
    }


}