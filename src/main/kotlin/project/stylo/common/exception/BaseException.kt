package project.stylo.common.exception

class BaseException(
    val exceptionType: ExceptionType,
    cause: Throwable? = null
): RuntimeException(exceptionType.message, cause)