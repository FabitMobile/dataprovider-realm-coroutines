package ru.fabit.dataprovider.realm.coroutines

import java.util.*

data class ImportRequest<T>(
    val path: String,
    val requestMethod: Int,
    val jsonMapper: Mapper<String, T>,
    val params: HashMap<String, Any> = hashMapOf(),
    val headers: HashMap<String, String> = hashMapOf(),
    val isAllJsonSave: Boolean = false,
    val baseUrl: String? = null,
    val entityPathRemote: String? = null,
    val entityPathLocal: String? = null,
    val localDataStore: LocalDataStore? = null
)