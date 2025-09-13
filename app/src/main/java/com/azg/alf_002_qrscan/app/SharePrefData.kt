package com.azg.alf_002_qrscan.app

import com.azg.alf_002_qrscan.app.sharedPreference

var isFinishFirstFlow by sharedPreference.boolean("isFinishFirstFlow", false)
var firstOpenApp by sharedPreference.int("firstOpenApp", 0)
fun isUfo(): Boolean {
    return firstOpenApp == 1
}