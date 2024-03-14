package ru.fabit.dataprovider.realm.coroutines

import io.realm.kotlin.types.RealmObject
import org.json.JSONException

@Suppress("UNCHECKED_CAST")
interface BaseRealmMapper<InputType: RealmObject, ReturnType>: RealmMapper<ReturnType> {
    fun map(realmObject: InputType): ReturnType
    override fun map(value: List<RealmObject>): List<ReturnType> {
        return try {
            value.map { realmObject ->
                map(realmObject as InputType)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            emptyList()
        }
    }
}