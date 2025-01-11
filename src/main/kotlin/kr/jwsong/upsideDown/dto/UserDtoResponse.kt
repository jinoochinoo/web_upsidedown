package kr.jwsong.upsideDown.dto

import kr.jwsong.upsideDown.entity.User

data class UserDtoResponse(
    val id: Long,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val company: String,
    val role: String
) {
    companion object {
        fun fromEntity(user: User): UserDtoResponse {
            return UserDtoResponse(
                id = user.id!!,
                email = user.email,
                name = user.name,
                phoneNumber = user.phoneNumber,
                company = user.company,
                role = user.role
            )
        }
    }
}