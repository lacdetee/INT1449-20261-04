package com.example.th2

data class Task(
    val id: Int = 0,
    val title: String,
    val deadline: String,
    val note: String = "",
    var isCompleted: Boolean = false
)
