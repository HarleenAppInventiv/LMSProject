package com.selflearningcoursecreationapp.ui.bottom_home


import com.selflearningcoursecreationapp.base.BaseViewModel


class HomeVM(private val repo: HomeRepo) : BaseViewModel() {

    init {
        getUserData()
    }


}