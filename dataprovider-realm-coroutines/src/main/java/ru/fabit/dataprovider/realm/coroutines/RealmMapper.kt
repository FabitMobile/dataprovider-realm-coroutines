package ru.fabit.dataprovider.realm.coroutines

import io.realm.kotlin.types.RealmObject

fun interface RealmMapper<ReturnType> {
    fun map(value: List<RealmObject>): List<ReturnType>
}