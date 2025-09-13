package com.azg.alf_002_qrscan.app

import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import com.azg.alf_002_qrscan.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.reflect.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class RemoteConfig() : KoinComponent {
    companion object {
        private val Instance: RemoteConfig by lazy {
            RemoteConfig()
        }
    }

    private val sharedPreference: SharedPreference by inject()
    private val gson: Gson by lazy { GsonBuilder().create() }

    inline fun <reified T> listFromCsv(
        value: String,
        defaultList: List<T> = emptyList(),
        transform: (String) -> T?
    ): List<T> {
        if (value.isBlank()) return defaultList
        return value.split(",")
            .mapNotNull { transform(it.trim()) }
            .ifEmpty { defaultList }
    }

    private inline fun <reified T> getAndApply(
        config: String,
        default: T,
        block: T.() -> Unit = {}
    ): T {
        val parsed: T? = try {
            gson.fromJson(config, T::class.java)
        } catch (e: Exception) {
            null
        }
        return (parsed ?: default).apply(block)
    }

    fun syncRemote(remoteConfig: FirebaseRemoteConfig) {}

    private var remoteConfig: FirebaseRemoteConfig

    init {
        val configSettings = remoteConfigSettings {
            setFetchTimeoutInSeconds(30L)
            setMinimumFetchIntervalInSeconds(if (BuildConfig.build_debug) 0L else 3600L)
        }
        Firebase.remoteConfig.apply {
            setConfigSettingsAsync(configSettings)
        }
        this.remoteConfig = Firebase.remoteConfig
    }

    suspend fun setupRemoteConfig(isDebug: Boolean) {
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setFetchTimeoutInSeconds(30L)
            .setMinimumFetchIntervalInSeconds(if (isDebug) 0L else 3600L)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        val isSuccess = runCatching {
            firebaseRemoteConfig.fetchAndActivate().await()
        }.getOrElse {
            false
        }
        if (isSuccess) {
            syncRemote(firebaseRemoteConfig)
        }
    }
    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> FirebaseRemoteConfig.readAsType(
        key: String,
        default: T
    ): T {
        val rcVal = getValue(key)
        val raw = rcVal.asString().trim()
        val result: T = when (T::class) {
            Boolean::class -> rcVal.asBoolean() as T
            Int::class -> rcVal.asLong().toInt() as T
            Long::class -> rcVal.asLong() as T
            Float::class -> rcVal.asDouble().toFloat() as T
            Double::class -> rcVal.asDouble() as T
            String::class -> raw as T
            else -> when {
                T::class.java.isEnum -> {
                    if (raw.isBlank()) default
                    else runCatching {
                        @Suppress("UNCHECKED_CAST")
                        java.lang.Enum.valueOf(
                            T::class.java as Class<out Enum<*>>,
                            raw
                        ) as T
                    }.getOrDefault(default)
                }
                (default is ByteArray) || (T::class == ByteArray::class) -> {
                    if (raw.isBlank()) default
                    else runCatching {
                        Base64.decode(raw, android.util.Base64.DEFAULT) as T
                    }.getOrDefault(default)
                }
                (default is Set<*>) && (default.all { it is String }) -> {
                    if (raw.isBlank()) default
                    else {
                        val jsonSet = runCatching {
                            val listType =
                                object : TypeToken<List<String>>() {}.type
                            val list = Gson().fromJson<List<String>>(raw, listType)
                            list.toSet()
                        }.getOrNull()

                        @Suppress("UNCHECKED_CAST")
                        (jsonSet ?: raw.split(',')
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                            .toSet()
                            .ifEmpty { default as Set<String> }) as T
                    }
                }
                raw.isBlank() -> default
                else -> {
                    runCatching {
                        Gson().fromJson<T>(raw, T::class.java)
                    }.getOrElse { default }
                }
            }
        }

        Log.d("FetchRemote", "Key: $key\nValue: $result")
        return result
    }

    private inline fun <reified T> dataObject(
        key: String,
        default: T,
        noinline block: (T.() -> Unit)? = null,
    ): ReadWriteProperty<Any?, T> {
        return object : ReadWriteProperty<Any?, T> {
            override fun getValue(thisRef: Any?, property: KProperty<*>): T {
                val result = sharedPreference.readAny(key, default)

                block?.invoke(result)
                return result
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                sharedPreference.writeAny(key, value)
            }
        }
    }

    private inline fun <reified T> FirebaseRemoteConfig.remoteObject(
        key: String,
        default: T,
        block: T.() -> Unit = {}
    ): T {
        val value = readAsType<T>(key, default).apply(block)
        sharedPreference.writeAny(key, value)
        return value
    }
}