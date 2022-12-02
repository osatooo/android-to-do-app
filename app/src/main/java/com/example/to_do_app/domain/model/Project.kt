package com.example.to_do_app.domain.model

data class Project(
    override val projectId: Int,
    override val name: String,
) : Items
