package ru.fabit.dataprovider.realm.coroutines

import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import ru.fabit.localservice.realm.coroutines.database.LocalService
import ru.fabit.localservice.realm.coroutines.database.LocalServiceParams
import ru.fabit.localservice.realm.coroutines.util.AggregationFunction
import ru.fabit.remoteservicecoroutines.remoteservice.RemoteService
import kotlin.reflect.KClass

class EntityDataProviderImpl(
    private val remoteService: RemoteService,
    private val localService: LocalService
) : EntityDataProvider {

    override suspend fun <T> getRemoteObject(importRequest: ImportRequest<T>): T {
        return getRemoteEntity(importRequest)
    }

    override suspend fun <T> getRemoteList(importRequest: ImportRequest<List<T>>): List<T> {
        return getRemoteEntity(importRequest)
    }

    override suspend fun <T> getLocalList(
        clazz: KClass<out RealmObject>,
        predicate: (RealmQuery<out RealmObject>) -> RealmQuery<out RealmObject>,
        dataToDomainCommonMapper: RealmMapper<T>,
        sort: Pair<String, Sort>?
    ): Flow<List<T>> {
        return localService.get(LocalServiceParams(clazz, predicate, sort))
            .map { list ->
                dataToDomainCommonMapper.map(list)
            }
    }

    override suspend fun getLocalAggregationFuncValue(
        clazz: KClass<out RealmObject>,
        predicate: (RealmQuery<out RealmObject>) -> RealmQuery<out RealmObject>,
        aggregationFunction: AggregationFunction,
        nameField: String
    ): Flow<Number?> {
        return localService.get(clazz, predicate, aggregationFunction, nameField)
    }

    override suspend fun updateLocal(
        clazz: KClass<out RealmObject>,
        predicate: (RealmQuery<out RealmObject>) -> RealmQuery<out RealmObject>,
        action: (RealmObject) -> Unit
    ) {
        localService.update(clazz, predicate, action)
    }

    override suspend fun deleteLocal(
        clazz: KClass<out RealmObject>,
        predicate: (RealmQuery<out RealmObject>) -> RealmQuery<out RealmObject>
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
        params: Map<String, Any>,
        headers: Map<String, String>
    ): JSONObject {
        return baseUrl?.let {
            remoteService.getRemoteJson(
                requestMethod,
                baseUrl,
                path,
                HashMap(params),
                headers
            )
        } ?: kotlin.run {
            remoteService.getRemoteJson(
                requestMethod,
                path,
                HashMap(params),
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
        when (entityPathLocal == null) {
            false -> localDataStore?.store(localJson)
            true -> if (isAllJsonSave) {
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