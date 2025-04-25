package ru.mobileup.samples.features.chat.domain.exception

sealed class FileOperationException(cause: Throwable? = null) : Exception(cause)

class FileCopingException(cause: Throwable? = null) : FileOperationException()
class InvalidMediaTypeException : FileOperationException()
class TooBigFileSizeException : FileOperationException()

fun Throwable.isFileCopingException(): Boolean {
    return this is FileCopingException
}

fun Throwable.isTooBigFileSizeException(): Boolean {
    return this is TooBigFileSizeException
}