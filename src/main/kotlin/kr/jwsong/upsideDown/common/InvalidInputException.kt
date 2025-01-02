package kr.jwsong.upsideDown.common

class InvalidInputException (
    val fieldName: String = "",
    message: String = "Invalid Input"
): RuntimeException(message)