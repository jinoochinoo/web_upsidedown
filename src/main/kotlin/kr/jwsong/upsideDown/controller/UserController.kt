package kr.jwsong.upsideDown.controller

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Valid
import kr.jwsong.upsideDown.common.BaseResponse
import kr.jwsong.upsideDown.dto.LoginDtoRequest
import kr.jwsong.upsideDown.dto.UserDtoRequest
import kr.jwsong.upsideDown.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping("/register")
    fun register(@RequestBody @Valid userDtoRequest: UserDtoRequest): BaseResponse<Unit> {
        logger.info("register > $userDtoRequest")
        val resultMsg: String = userService.register(userDtoRequest)
        logger.info("resultMsg > $resultMsg");
        return BaseResponse(message = resultMsg)
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody @Valid loginDtoRequest: LoginDtoRequest): BaseResponse<Unit> {
        logger.info("requestBody > $loginDtoRequest")

        // Param Map 형태로 받는 코드
        //val paramMap: Map<String, Any> = ObjectMapper().readValue(requestBody, HashMap::class.java) as Map<String, Any>

        val resultMsg: String = userService.signIn(loginDtoRequest).toString()
        return BaseResponse(message = resultMsg)
    }

}