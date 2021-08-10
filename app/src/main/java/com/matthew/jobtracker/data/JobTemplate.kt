package com.matthew.jobtracker.data

import kotlinx.serialization.Serializable

@Serializable
class JobTemplate (
    var name: String,
    var taskTemplates: MutableList<String> = mutableListOf()
)