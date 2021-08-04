package com.matthew.jobtracker.data.rv_items

import com.matthew.jobtracker.data.Job

class CurrentJobItemData(val job : Job) : ItemData() {
    override var text = job.name
    override var subtext = job.prettyTotalTime
}