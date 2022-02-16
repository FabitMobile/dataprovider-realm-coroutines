package ru.fabit.dataprovider.realm.coroutines

interface Mapper<T1, T2> {
    fun map(value: T1): T2
}