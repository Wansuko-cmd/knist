package com.wsr

sealed class DomainException : Exception() {
    class UnreachableCodeException() : DomainException()
    class CannotCastDimensionException : DomainException()
}