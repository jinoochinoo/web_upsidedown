package kr.jwsong.upsideDown.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.ElementCollection
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import kr.jwsong.upsideDown.common.enum.ROLE
import kr.jwsong.upsideDown.entity.User

data class UserDtoRequest (
    val id: Long?,

    @field:NotBlank(
        message = "이메일을 입력하세요!"
    )
    @field:Email
    @JsonProperty("email")
    private val _email: String?,

    @field:NotBlank(
        message = "이름을 입력하세요!"
    )
    @JsonProperty("name")
    private val _name: String?,

    @field:NotBlank@field:NotBlank(
        message = "휴대폰번호를 입력하세요!"
    )
    @JsonProperty("phoneNumber")
    private val _phoneNumber: String?,

    @field:NotBlank(
        message = "회사명을 입력하세요!"
    )
    @JsonProperty("company")
    private val _company: String?,

    @field:Pattern(
        regexp="^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#\$%^&*])[a-zA-Z0-9!@#\$%^&*]{8,20}\$",
        message = "영문, 숫자, 특수 문자를 포함한 8~20 자리로 입력하세요!"
    )
    @field:NotBlank(
        message = "비밀번호를 입력하세요!"
    )
    @JsonProperty("password")
    private val _password: String,

    @field:NotBlank
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private val _role: String = ROLE.NONE.toString()

) {

    val email: String
        get() = _email!!
    val name: String
        get() = _name!!
    val phoneNumber: String
        get() = _phoneNumber!!
    val company: String
        get() = _company!!
    val password: String
        get() = _password!!
    val role: String
        get() = _role!!

    fun toEntity(): User = User(id, email, name, phoneNumber, company, password, role)
}