package ru.fabit.dataprovider.realm.coroutines

fun interface Mapper<in InputType, out ReturnType> {
    fun map(value: InputType): ReturnType
}