package com.selflearningcoursecreationapp.ui.bottom_more

import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.network.Resource
import com.selflearningcoursecreationapp.utils.MODTYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoreFragmentVM(private val repo: MoreFragmentRepo) : BaseViewModel() {
    init {
        getUserData()
    }


    fun switchMod() = viewModelScope.launch(Dispatchers.IO) {
        val response =
            repo.switchMod(if (userProfile?.currentMode == MODTYPE.LEARNER) MODTYPE.MODERATOR else MODTYPE.LEARNER)
        withContext(Dispatchers.IO) {
            response.collect {
                if (it is Resource.Success<*>) {
                    updateResponseObserver(it)
//                    val data = it.value as BaseResponse<UserProfile>
//                    saveUser(UserResponse(user = data.resource))
                } else {
                    updateResponseObserver(it)
                }
            }
        }
    }

    fun support(desc: String) = viewModelScope.launch(Dispatchers.IO) {
        val map = HashMap<String, Any>()
        map["description"] = desc
        val response = repo.support(map)
        withContext(Dispatchers.IO) {
            response.collect {
                updateResponseObserver(it)
            }
        }
    }


//    fun staticPages(type: Int) = viewModelScope.launch(Dispatchers.IO) {
//        val response = repo.staticPages(type)
//        withContext(Dispatchers.IO) {
//            response.collect {
//                updateResponseObserver(it)
//            }
//        }
//    }

    override fun onApiRetry(apiCode: String) {
    }
}