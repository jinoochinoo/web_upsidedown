package kr.jwsong.upsideDown.controller

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.validation.Valid
import kr.jwsong.upsideDown.common.BaseResponse
import kr.jwsong.upsideDown.dto.LoginDtoRequest
import kr.jwsong.upsideDown.dto.UserDtoRequest
import kr.jwsong.upsideDown.dto.UserDtoResponse
import kr.jwsong.upsideDown.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @PostMapping("/getUserList")
    fun getUserList(): ResponseEntity<List<UserDtoResponse>> {
        val userList = userService.getUserList()
        return ResponseEntity.ok(userList)
    }

    @PostMapping("/updateUser")
    fun updateUser(@RequestBody updatedUser: UserDtoResponse): ResponseEntity<UserDtoResponse> {
        return try {
            val updatedUserData = userService.updateUser(updatedUser)
            ResponseEntity.ok(updatedUserData)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null)
        }
    }

}