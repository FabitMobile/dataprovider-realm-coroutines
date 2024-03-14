package ru.fabit.dataprovider.realm.coroutines

data class ImportRequest<T>(
    val path: String,
    val requestMethod: Int,
    val jsonMapper: Mapper<String, T>,
    val params: Map<String, Any> = mapOf(),
    val headers: Map<String, String> = mapOf(),
    val isAllJsonSave: Boolean = false,
    val baseUrl: String? = null,
    val entityPathRemote: String? = null,
    val entityPathLocal: String? = null,
    val localDataStore: LocalDataStore? = null,
    val needAuth: Boolean = false
)