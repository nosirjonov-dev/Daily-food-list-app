package com.example.dailyschedule.models


data class TaskModel(
    val id: Int,
    val dateId: Int,
    val title: String,
    val time: String,
    val image: String
)