package com.matthew.jobtracker.data.rv_items

import com.matthew.jobtracker.data.Job
import com.matthew.jobtracker.data.Task

class CurrentTaskItemData(val task : Task) : ItemData() {
    override var text = task.name
    override var subtext = task.prettyTime
}