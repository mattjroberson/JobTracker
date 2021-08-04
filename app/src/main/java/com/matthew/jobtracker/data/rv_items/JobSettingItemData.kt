package com.matthew.jobtracker.data.rv_items

import com.matthew.jobtracker.data.JobTemplate

class JobSettingItemData(val template: JobTemplate) : ItemData() {
    override var text : String = template.name
    override var subtext : String = ""
}