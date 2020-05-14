package de.tbollmeier.grammarous

sealed class Result<T> {
    class Success<T>(val value: T) : Result<T>()
    class Failure<T>(val message: String) : Result<T>()
}
