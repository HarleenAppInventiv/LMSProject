package com.selflearningcoursecreationapp.models.rewardPoints

import com.selflearningcoursecreationapp.models.course.WishListResponse
import com.selflearningcoursecreationapp.models.user.UserProfile

data class RewardPointResponse(
    var userProfile: UserProfile, var rewardPoints: Long,
    var totalEarnAsACreator: Long,
    var totalEarnAsALearner: Long,
    var totalSpend: Long,
    var courses: WishListResponse
)