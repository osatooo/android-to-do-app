package com.example.to_do_app.domain.model

data class TaskClass(
    override val name: String,
    override val projectId: Int,
    val taskClassId: Int,
) : Items
