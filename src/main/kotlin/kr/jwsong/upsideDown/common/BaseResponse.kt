package kr.jwsong.upsideDown.common

import kr.jwsong.upsideDown.common.enum.ResultCode

data class BaseResponse<T> (
    val resultCode: String = ResultCode.SUCCESS.name,
    val data: T? = null,
    val message: String = ResultCode.SUCCESS.msg
)