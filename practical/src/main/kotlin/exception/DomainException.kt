package exception

sealed class DomainException : Exception() {
    class UnreachableCodeException() : DomainException()
}