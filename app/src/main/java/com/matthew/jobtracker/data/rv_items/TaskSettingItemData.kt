package com.matthew.jobtracker.data.rv_items

import com.matthew.jobtracker.data.JobTemplate

class TaskSettingItemData(val name: String) : ItemData() {
    override var text : String = name
    override var subtext : String = ""
}