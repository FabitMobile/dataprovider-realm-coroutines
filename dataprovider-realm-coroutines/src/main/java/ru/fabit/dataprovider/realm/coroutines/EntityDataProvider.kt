package ru.fabit.dataprovider.realm.coroutines

import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import ru.fabit.localservice.realm.coroutines.util.AggregationFunction
import kotlin.reflect.KClass

interface EntityDataProvider {
    suspend fun <T> getRemoteList(importRequest: ImportRequest<List<T>>): List<T>
    suspend fun <T> getRemoteObject(importRequest: ImportRequest<T>): T

    suspend fun <T> getLocalList(
        clazz: KClass<out RealmObject>,
        predicate: (RealmQuery<out RealmObject>) -> RealmQuery<out RealmObject>,
        dataToDomainCommonMapper: RealmMapper<T>,
        sort: Pair<String, Sort>? = null
    ): Flow<List<T>>

    suspend fun getLocalAggregationFuncValue(
        clazz: KClass<out RealmObject>,
        predicate: (RealmQuery<out RealmObject>) -> RealmQuery<out RealmObject>,
        aggregationFunction: AggregationFunction,
        nameField: String
    ): Flow<Number?>

    suspend fun updateLocal(
        clazz: KClass<out RealmObject>,
        predicate: (RealmQuery<out RealmObject>) -> RealmQuery<out RealmObject>,
        action: (RealmObject) -> Unit
    )

    suspend fun deleteLocal(
        clazz: KClass<out RealmObject>,
        predicate: (RealmQuery<out RealmObject>) -> RealmQuery<out RealmObject> = { it }
    )
}