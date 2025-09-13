package com.azg.alf_002_qrscan.app

import android.content.Context
import android.content.res.Configuration
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Locale

object PreferenceData : KoinComponent {
    private val sharedPreference: SharedPreference by inject()
    var languageCode by sharedPreference.string("languageCode", "en")
        private set
    var isFinishFirstFlow by sharedPreference.boolean("isFinishFirstFlow", false)
    var firstOpenApp by sharedPreference.int("firstOpenApp", 0)
    fun isUfo(): Boolean {
        return firstOpenApp == 1
    }

    fun setLocale(context: Context, language: String = "en") {
        languageCode = language
        if (language.isEmpty()) {
            val locale = context.resources.configuration.locale
            Locale.setDefault(locale)
            val config = Configuration().apply { this.locale = locale }
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        } else {
            changeLang(language, context)
        }
    }

    private fun changeLang(lang: String?, context: Context) {
        if (lang.isNullOrEmpty()) return
        val parts = lang.split("_")
        val languageCode = parts[0]
        val countryCode = parts.getOrNull(1) ?: ""
        val myLocale = if (countryCode.isNotEmpty())
            Locale(languageCode, countryCode)
        else
            Locale(languageCode)
        Locale.setDefault(myLocale)
        val config = Configuration().apply { locale = myLocale }
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}