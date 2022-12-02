package com.example.to_do_app.domain.model

data class Task(
    override val name: String,
    override val projectId: Int,
    val taskClassId: Int,
    var comment: String? = null,
    var checkStatus: Boolean = false,
) : Items
