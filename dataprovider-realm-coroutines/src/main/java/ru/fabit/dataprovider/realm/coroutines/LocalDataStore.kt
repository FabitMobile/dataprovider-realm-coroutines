package ru.fabit.dataprovider.realm.coroutines

interface LocalDataStore {
    suspend fun store(rawJson: String)
}