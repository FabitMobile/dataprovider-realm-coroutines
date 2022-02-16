package ru.fabit.dataprovider.realm.coroutines

import io.realm.RealmModel
import io.realm.RealmQuery
import io.realm.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import ru.fabit.localservice.realm.coroutines.database.LocalService
import ru.fabit.localservice.realm.coroutines.database.LocalServiceParams
import ru.fabit.localservice.realm.coroutines.util.AggregationFunction
import ru.fabit.remoteservicecoroutines.remoteservice.RemoteService
import java.util.*

class EntityDataProviderImpl(
    private val remoteService: RemoteService,
    private val localService: LocalService
) : EntityDataProvider {

    override suspend fun <T> getRemoteObject(importRequest: ImportRequest<T>): T {
        return getRemoteEntity(importRequest)
    }

    override suspend fun <T> getRemoteList(importRequest: ImportRequest<T>): List<T> {
        return getRemoteEntity(importRequest) as List<T>
    }

    override suspend fun <T> getLocalList(
        clazz: Class<RealmModel>,
        predicate: (RealmQuery<RealmModel>) -> RealmQuery<RealmModel>,
        dataToDomainCommonMapper: Mapper<List<RealmModel>, List<T>>,
        sort: Map.Entry<Array<String>, Array<Sort>>?
    ): Flow<List<T>> {
        return localService.get(LocalServiceParams(clazz, predicate, sort))
            .map { list ->
                dataToDomainCommonMapper.map(list)
            }
    }

    override suspend fun getLocalAggregationFuncValue(
        clazz: Class<RealmModel>,
        predicate: (RealmQuery<RealmModel>) -> RealmQuery<RealmModel>,
        aggregationFunction: AggregationFunction,
        nameField: String
    ): Flow<Number?> {
        return localService.get(clazz, predicate, aggregationFunction, nameField)
    }

    override suspend fun updateLocal(
        clazz: Class<RealmModel>,
        predicate: (RealmQuery<RealmModel>) -> RealmQuery<RealmModel>,
        action: (RealmModel) -> Unit
    ) {
        localService.update(clazz, predicate, action)
    }

    override suspend fun deleteLocal(
        clazz: Class<RealmModel>,
        predicate: (RealmQuery<RealmModel>) -> RealmQuery<RealmModel>
    ) {
        localService.delete(clazz, predicate)
    }

    private suspend fun <T> getRemoteEntity(importRequest: ImportRequest<T>): T {
        val path = importRequest.path
        val baseUrl = importRequest.baseUrl
        val requestMethod = importRequest.requestMethod
        val entityPathRemote = importRequest.entityPathRemote
        val entityPathLocal = importRequest.entityPathLocal
        val localDataStore = importRequest.localDataStore
        val params = importRequest.params
        val headers = importRequest.headers
        val isAllJsonSave = importRequest.isAllJsonSave
        val jsonMapper = importRequest.jsonMapper

        val jsonObject = getJsonObject(path, baseUrl, requestMethod, params, headers)
        importJsonObject(jsonObject, isAllJsonSave, entityPathLocal, localDataStore)
        val remoteJson = getJson(jsonObject, entityPathRemote)
        return getEntity(remoteJson, jsonMapper)
    }

    private suspend fun getJsonObject(
        path: String,
        baseUrl: String?,
        requestMethod: Int,
        params: HashMap<String, Any>,
        headers: HashMap<String, String>
    ): JSONObject {
        return baseUrl?.let {
            remoteService.getRemoteJson(
                requestMethod,
                baseUrl,
                path,
                params,
                headers
            )
        } ?: kotlin.run {
            remoteService.getRemoteJson(
                requestMethod,
                path,
                params,
                headers
            )
        }
    }

    private suspend fun importJsonObject(
        jsonObject: JSONObject,
        isAllJsonSave: Boolean,
        entityPathLocal: String?,
        localDataStore: LocalDataStore?
    ) {
        val localJson = getJson(jsonObject, entityPathLocal)
        when (entityPathLocal != null) {
            true -> localDataStore?.store(localJson)
            false -> if (isAllJsonSave) {
                localDataStore?.store(localJson)
            }
        }
    }

    private fun getJson(response: JSONObject, path: String?): String {
        return path?.let {
            when (response.has(path)) {
                true -> response[path].toString()
                false -> response.toString()
            }
        } ?: kotlin.run {
            response.toString()
        }
    }

    private fun <T> getEntity(json: String, jsonMapper: Mapper<String, T>) =
        jsonMapper.map(json)

}