package com.matthew.jobtracker.data

import kotlinx.serialization.Serializable

@Serializable
class JobTemplate (
    var name: String = "Default Name",
    var taskTemplates: MutableList<String> = mutableListOf()
)