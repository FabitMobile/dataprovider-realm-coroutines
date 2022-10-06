package ru.fabit.dataprovider.realm.coroutines

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import ru.fabit.dataprovider.realm.coroutines.JsonTransformerUtils.foreach

abstract class BaseCommonMapper<ReturnType>: Mapper<String, List<ReturnType>> {
    abstract fun map(jsonObject: JSONObject): ReturnType

    override fun map(value: String): List<ReturnType> {
        val list = mutableListOf<ReturnType>()
        try {
            val jsonTokener = JSONTokener(value).nextValue()
            if (jsonTokener is JSONObject) {
                list.add(map(JSONObject(value)))
            } else {
                JSONArray(value).foreach {
                    list.add(map(it))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return list
    }
}