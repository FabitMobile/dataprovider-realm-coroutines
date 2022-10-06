package ru.fabit.dataprovider.realm.coroutines

import io.realm.kotlin.types.RealmObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ru.fabit.dataprovider.realm.coroutines.JsonTransformerUtils.foreach

interface JsonToRealmMapper<T: RealmObject> {
    fun apply(jsonObject: JSONObject): T

    fun apply(jsonArray: JSONArray): List<T> {
        val list = mutableListOf<T>()
        jsonArray.foreach {
            try {
                val newObject = apply(it)
                list.add(newObject)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return list
    }
}