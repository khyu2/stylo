package project.stylo.common.exception

import org.springframework.http.HttpStatus

interface ExceptionType {
    val status: HttpStatus
    val code: String
    val message: String
}