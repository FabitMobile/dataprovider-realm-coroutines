package ru.fabit.dataprovider.realm.coroutines

import org.json.JSONArray
import org.json.JSONObject

object JsonTransformerUtils {
    fun JSONObject.notNull(jsonField: String) = has(jsonField) && !isNull(jsonField)

    @Suppress("UNCHECKED_CAST")
    fun <T> JSONObject.getOrNull(jsonField: String): T? =
        if (has(jsonField)) get(jsonField) as? T else null

    fun <T> JSONObject.getListOrNull(jsonField: String): List<T>? =
        if (has(jsonField)) {
            val array = JSONArray(getJSONArray(jsonField))
            array.toList()
        } else null

    fun JSONArray.toListInt(): List<Int> = this.toList()

    @Suppress("UNCHECKED_CAST")
    fun <T> JSONArray.toList(): List<T> {
        val result = mutableListOf<T>()
        for (i in 0 until length())
            result.add(get(i) as T)
        return result
    }

    fun <T> List<T>.toJsonArray(): JSONArray {
        val result = JSONArray()
        forEach {
            result.put(it)
        }
        return result
    }

    inline fun <T> List<T>.toJsonArray(transform: (T) -> JSONObject?): JSONArray {
        val result = JSONArray()
        forEach { element ->
            transform(element)?.let { transformed ->
                result.put(transformed)
            }
        }
        return result
    }

    inline fun <T> JSONArray.map(transform: (JSONObject) -> T): JSONArray {
        val result = JSONArray()
        for (i in 0 until length())
            result.put(i, transform(getJSONObject(i)))
        return result
    }

    inline fun JSONArray.foreach(action: (JSONObject) -> Unit) {
        for (i in 0 until length())
            action(getJSONObject(i))
    }

    inline fun JSONArray.foreachArray(action: (JSONArray) -> Unit) {
        for (i in 0 until length())
            action(getJSONArray(i))
    }

    inline fun JSONArray.foreachDouble(action: (Double) -> Unit) {
        for (i in 0 until length())
            action(getDouble(i))
    }

    inline fun JSONArray.foreachString(action: (String) -> Unit) {
        for (i in 0 until length())
            action(getString(i))
    }

    inline fun JSONArray.foreachInt(action: (Int) -> Unit) {
        for (i in 0 until length())
            action(getInt(i))
    }
}