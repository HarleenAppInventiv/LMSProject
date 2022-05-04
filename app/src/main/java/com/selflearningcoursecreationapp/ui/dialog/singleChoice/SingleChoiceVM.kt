package com.selflearningcoursecreationapp.ui.dialog.singleChoice


import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SingleChoiceVM(private val repository: SingleChoiceRepo?) : BaseViewModel() {


    fun getProfession() = viewModelScope.launch(coroutineExceptionHandle) {

        var response = repository?.professionApi()
        withContext(Dispatchers.IO) {
            response?.collect {

            updateResponseObserver(it)

            }

        }
    }


}