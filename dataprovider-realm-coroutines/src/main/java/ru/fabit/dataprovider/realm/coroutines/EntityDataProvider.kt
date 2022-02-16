package ru.fabit.dataprovider.realm.coroutines

import io.realm.RealmModel
import io.realm.RealmQuery
import io.realm.Sort
import kotlinx.coroutines.flow.Flow
import ru.fabit.localservice.realm.coroutines.util.AggregationFunction

interface EntityDataProvider {
    suspend fun <T> getRemoteList(importRequest: ImportRequest<T>): List<T>

    suspend fun <T> getRemoteObject(importRequest: ImportRequest<T>): T

    suspend fun <T> getLocalList(
        clazz: Class<RealmModel>,
        predicate: (RealmQuery<RealmModel>) -> RealmQuery<RealmModel>,
        dataToDomainCommonMapper: Mapper<List<RealmModel>, List<T>>,
        sort: Map.Entry<Array<String>, Array<Sort>>? = null
    ): Flow<List<T>>

    suspend fun getLocalAggregationFuncValue(
        clazz: Class<RealmModel>,
        predicate: (RealmQuery<RealmModel>) -> RealmQuery<RealmModel>,
        aggregationFunction: AggregationFunction,
        nameField: String
    ): Flow<Number?>

    suspend fun updateLocal(
        clazz: Class<RealmModel>,
        predicate: (RealmQuery<RealmModel>) -> RealmQuery<RealmModel>,
        action: (RealmModel) -> Unit
    )

    suspend fun deleteLocal(
        clazz: Class<RealmModel>,
        predicate: (RealmQuery<RealmModel>) -> RealmQuery<RealmModel>
    )
}