package com.azg.alf_002_qrscan.app

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PreferenceData : KoinComponent {
    private val sharedPreference: SharedPreference by inject()


    var isFinishFirstFlow by sharedPreference.boolean("isFinishFirstFlow", false)
    var firstOpenApp by sharedPreference.int("firstOpenApp", 0)
    fun isUfo(): Boolean {
        return firstOpenApp == 1
    }
}