package com.selflearningcoursecreationapp.models

import ccom.example.roomwithmvvmdemo.all_states.StateData

data class AllStates(
    val `data`: List<StateData>,
    val message: String,
    val status: String
)