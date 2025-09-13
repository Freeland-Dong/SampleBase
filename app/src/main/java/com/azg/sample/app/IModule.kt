package com.azg.sample.app

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val viewModelModule = module {
}
val dataModule = module {
    singleOf(::SharedPreference)
}
