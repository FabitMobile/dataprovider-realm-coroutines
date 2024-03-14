package ru.fabit.dataprovider.realm.coroutines

import io.realm.kotlin.types.RealmObject
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import ru.fabit.localservice.realm.coroutines.database.LocalService

open class BaseLocalDataStore<T: RealmObject> (
    private val localService: LocalService,
    private val jsonTransformer: JsonTransformer,
    private val jsonToRealmMapper: JsonToRealmMapper<T>
): LocalDataStore {
    override suspend fun store(rawJson: String) {
        val json = JSONTokener(rawJson.replace("_id", "id")).nextValue()
        if (json is JSONObject) {
            localService.storeObject(jsonToRealmMapper.apply(jsonTransformer.apply(json)))
        } else if (json is JSONArray) {
            localService.storeObjects(jsonToRealmMapper.apply(jsonTransformer.apply(json)))
        }
    }
}