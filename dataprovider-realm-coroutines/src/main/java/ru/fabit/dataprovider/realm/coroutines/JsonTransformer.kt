package ru.fabit.dataprovider.realm.coroutines

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ru.fabit.dataprovider.realm.coroutines.JsonTransformerUtils.map

interface JsonTransformer {
    fun apply(jsonObject: JSONObject): JSONObject

    fun apply(jsonArray: JSONArray): JSONArray {
        return jsonArray.map {
            try {
                apply(it)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}