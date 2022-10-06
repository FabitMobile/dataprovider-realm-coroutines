package ru.fabit.dataprovider.realm.coroutines

import org.json.JSONArray

class TransformerCompositionArray(private var jsonArray: JSONArray) {

    fun apply(jsonTransformer: JsonTransformer): TransformerCompositionArray {
        jsonArray = jsonTransformer.apply(jsonArray)
        return this
    }

    fun finish(): JSONArray {
        return jsonArray
    }
}