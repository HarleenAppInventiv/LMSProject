package com.selflearningcoursecreationapp.ui.dialog.singleChoice

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface SingleChoiceRepo {

    suspend fun professionApi(): Flow<Resource>

}