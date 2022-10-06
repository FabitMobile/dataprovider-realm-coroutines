package ru.fabit.dataprovider.realm.coroutines

import org.json.JSONObject

class TransformerCompositionObject(private var jsonObject: JSONObject) {
    fun apply(jsonTransformer: JsonTransformer): TransformerCompositionObject {
        jsonObject = jsonTransformer.apply(jsonObject)
        return this
    }

    fun finish(): JSONObject {
        return jsonObject
    }
}