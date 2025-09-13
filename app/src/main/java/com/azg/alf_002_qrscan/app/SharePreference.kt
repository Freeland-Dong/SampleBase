package com.azg.alf_002_qrscan.app

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.lang.reflect.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPreference(context: Context) {
    private val sharedPreferences by lazy() {
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }
    private val gson by lazy {
        GsonBuilder().create()
    }

    private inline fun <reified T> typeOf(): Type = object : TypeToken<T>() {}.type
    private val changeListeners = mutableSetOf<OnPreferenceChangeListener>()

    interface OnPreferenceChangeListener {
        fun onPreferenceChanged(key: String)
    }

    fun registerListener(listener: OnPreferenceChangeListener) {
        changeListeners.add(listener)
    }

    fun unRegisterListener(listener: OnPreferenceChangeListener) {
        changeListeners.remove(listener)
    }

    fun clear() = edit { clear() }
    class PreferenceProperty<T : Any>(
        private val prefs: SharedPreference,
        val key: String,
        val default: T
    ) {
        var value: T by prefs.preference(key, default)
        val liveData: LiveData<T> = prefs.liveData(key, default)
        val flow: Flow<T> = prefs.flow(key, default)
    }

    fun <T : Any> preferenceOf(key: String, default: T): PreferenceProperty<T> =
        PreferenceProperty(this, key, default)

    inline fun <reified T : Enum<T>> getEnum(key: String, default: T): T =
        migrateEnumToString(key, T::class.java, default)

    private fun edit(block: SharedPreferences.Editor.() -> Unit) {
        with(sharedPreferences.edit()) {
            block()
            apply()
        }
    }

    private fun notifyChange(key: String) {
        changeListeners.forEach { it.onPreferenceChanged(key) }
    }

    private fun getString(key: String, default: String = ""): String =
        sharedPreferences.getString(key, default)?.takeIf { it.isNotEmpty() } ?: default

    private fun putString(key: String, value: String) = edit {
        putString(key, value)
        notifyChange(key)
    }

    private fun getInt(key: String, default: Int = 0): Int =
        sharedPreferences.getInt(key, default)

    private fun putInt(key: String, value: Int) = edit {
        putInt(key, value)
        notifyChange(key)
    }

    private fun getBoolean(key: String, default: Boolean = false): Boolean =
        sharedPreferences.getBoolean(key, default)

    private fun putBoolean(key: String, value: Boolean) = edit {
        putBoolean(key, value)
        notifyChange(key)
    }

    private fun getFloat(key: String, default: Float = 0f): Float =
        sharedPreferences.getFloat(key, default)

    private fun putFloat(key: String, value: Float) = edit {
        putFloat(key, value)
        notifyChange(key)
    }

    private fun getLong(key: String, default: Long = 0L): Long =
        sharedPreferences.getLong(key, default)

    private fun putLong(key: String, value: Long) = edit {
        putLong(key, value)
        notifyChange(key)
    }

    private fun getDouble(key: String, default: Double = 0.0): Double =
        sharedPreferences.getString(key, default.toString())?.toDoubleOrNull() ?: default

    private fun putDouble(key: String, value: Double) = edit {
        putString(key, value.toString())
        notifyChange(key)
    }

    fun <T : Any> preference(
        key: String,
        default: T
    ): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T = getValueFor(key, default)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = edit {
            setValueFor(key, value)
            notifyChange(key)
        }
    }

    fun boolean(key: String, default: Boolean = false) =
        createProperty(key, default, ::getBoolean, ::putBoolean)

    fun string(key: String, default: String = "") =
        createProperty(key, default, ::getString, ::putString)

    fun int(key: String, default: Int = 0) =
        createProperty(key, default, ::getInt, ::putInt)

    fun long(key: String, default: Long = 0L) =
        createProperty(key, default, ::getLong, ::putLong)

    fun float(key: String, default: Float = 0f) =
        createProperty(key, default, ::getFloat, ::putFloat)

    fun double(key: String, default: Double = 0.0) =
        createProperty(key, default, ::getDouble, ::putDouble)

    internal inline fun <reified T> list(
        key: String,
        default: List<T> = emptyList()
    ): ReadWriteProperty<Any?, List<T>> = object : ReadWriteProperty<Any?, List<T>> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): List<T> {
            val json = getString(key, "")
            val type = object : TypeToken<List<T>>() {}.type
            return runCatching { gson.fromJson<List<T>>(json, type) }.getOrDefault(default)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: List<T>) = edit {
            putString(key, gson.toJson(value)); notifyChange(key)
        }
    }

    private fun <T> createProperty(
        key: String,
        default: T,
        getter: (String, T) -> T,
        setter: (String, T) -> Unit
    ): ReadWriteProperty<Any?, T> = object : ReadWriteProperty<Any?, T> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): T = getter(key, default)
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = setter(key, value)
    }

    fun <T : Any> liveData(key: String, default: T): LiveData<T> = object : LiveData<T>() {
        private val listener = OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) value = getValueFor(key, default)
        }

        override fun onActive() {
            super.onActive()
            value = getValueFor(key, default)
            sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        }

        override fun onInactive() {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            super.onInactive()
        }
    }

    fun <T : Any> flow(key: String, default: T): Flow<T> = callbackFlow {
        trySend(getValueFor(key, default))
        val listener = OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) trySend(getValueFor(key, default))
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    private fun <T> getValueFor(key: String, default: T): T = when (default) {
        is String -> getString(key, default)
        is Int -> getInt(key, default)
        is Boolean -> getBoolean(key, default)
        is Float -> getFloat(key, default)
        is Long -> getLong(key, default)
        is Double -> getDouble(key, default)
        is Set<*> -> {
            val def = default.filterIsInstance<String>().toSet()
            (sharedPreferences.getStringSet(key, def) ?: def)
        }
        is Enum<*> -> getEnumByNameOrOrdinal(key, default) as T
        else -> getJsonValue(key, default)
    } as T

    private fun <T> setValueFor(key: String, value: T) = when (value) {
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Boolean -> putBoolean(key, value)
        is Float -> putFloat(key, value)
        is Long -> putLong(key, value)
        is Enum<*> -> putString(key, value.name)
        is Set<*> -> sharedPreferences.edit()
            .putStringSet(key, value.filterIsInstance<String>().toSet()).apply()
        else -> putString(key, gson.toJson(value))
    }
    @Suppress("UNCHECKED_CAST")
    private fun <T : Enum<T>> migrateFromOrdinal(key: String, default: T): T {
        return try {
            val intValue = sharedPreferences.getInt(key, -1)
            if (intValue >= 0) {
                val enumConstants = (default::class.java as Class<out Enum<*>>).enumConstants
                if (intValue < enumConstants.size) {
                    val enumValue = enumConstants[intValue] as T
                    putString(key, enumValue.name)
                    enumValue
                } else default
            } else default
        } catch (e: Exception) {
            default
        }
    }
    @Suppress("UNCHECKED_CAST")
    private fun <T> getJsonValue(key: String, default: T): T {
        val json = sharedPreferences.getString(key, null) ?: return default
        return try {
            val type = object : TypeToken<T>() {}.type
            gson.fromJson(json, type) ?: default
        } catch (e: Exception) {
            default
        }
    }

    fun <T : Enum<T>> migrateEnumToString(key: String, enumClass: Class<T>, default: T): T {
        return try {
            val stringValue = sharedPreferences.getString(key, null)
            if (stringValue != null) {
                try {
                    java.lang.Enum.valueOf(enumClass, stringValue)
                } catch (e: IllegalArgumentException) {
                    migrateFromOrdinal(key, default)
                }
            } else {
                migrateFromOrdinal(key, default)
            }
        } catch (e: Exception) {
            default
        }
    }

    private fun getEnumByNameOrOrdinal(key: String, default: Enum<*>): Enum<*> {
        val enumClass = default::class.java as Class<out Enum<*>>
        val savedName = sharedPreferences.getString(key, null)
        if (savedName != null) {
            enumClass.enumConstants?.firstOrNull { it.name == savedName }?.let { return it }
        }
        val oldOrdinal = sharedPreferences.getInt(key, -1)
        if (oldOrdinal >= 0) {
            val constants = enumClass.enumConstants ?: emptyArray()
            if (oldOrdinal in constants.indices) {
                val migrated = constants[oldOrdinal]
                putString(key, migrated.name)
                return migrated
            }
        }
        return default
    }
    @Suppress("UNCHECKED_CAST")
    internal inline fun <reified T> readAny(key: String, default: T): T {
        return getValueFor(key, default)
    }
    @Suppress("UNCHECKED_CAST")
    internal inline fun <reified T> writeAny(key: String, value: T) {
        setValueFor(key, value)
    }
}

fun <T : Any> SharedPreference.preferenceOf(
    key: String,
    default: T
): SharedPreference.PreferenceProperty<T> =
    SharedPreference.PreferenceProperty(this, key, default)

inline fun <reified T : Enum<T>> SharedPreference.getEnum(key: String, default: T): T =
    migrateEnumToString(key, T::class.java, default)






