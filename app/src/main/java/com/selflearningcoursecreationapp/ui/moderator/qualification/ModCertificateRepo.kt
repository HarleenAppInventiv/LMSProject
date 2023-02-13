package com.selflearningcoursecreationapp.ui.moderator.qualification

import com.selflearningcoursecreationapp.data.network.Resource
import kotlinx.coroutines.flow.Flow

interface ModCertificateRepo {
    suspend fun myDocs(): Flow<Resource>

}