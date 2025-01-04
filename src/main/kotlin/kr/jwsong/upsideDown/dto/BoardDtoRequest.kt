package kr.jwsong.upsideDown.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.ElementCollection
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import kr.jwsong.upsideDown.common.UserAuthentication
import kr.jwsong.upsideDown.common.enum.ROLE
import kr.jwsong.upsideDown.entity.Board
import kr.jwsong.upsideDown.entity.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.multipart.MultipartFile

// 파일 정보를 담는 클래스
data class FileData(
    val savedFileName: String,
    val originalFileName: String
)

data class BoardDtoRequest (
    val id: Long?,

    @field:NotBlank(message = "제목을 입력하세요!")
    @JsonProperty("title")
    private val _title: String,

    @field:NotBlank(message = "내용을 입력하세요!")
    @JsonProperty("content")
    private val _content: String,

    // 파일 정보가 포함된 리스트
    @JsonProperty("files")
    val files: List<FileData>
) {

    val title: String
        get() = _title
    val content: String
        get() = _content
    val email: String
        get() = SecurityContextHolder.getContext().authentication.name

    fun toEntity(): Board {
        return Board(id, title, content, email)
    }
}