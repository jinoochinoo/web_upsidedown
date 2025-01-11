package kr.jwsong.upsideDown.service

import jakarta.transaction.Transactional
import kr.jwsong.upsideDown.dto.LoginDtoRequest
import kr.jwsong.upsideDown.dto.UserDtoRequest
import kr.jwsong.upsideDown.dto.UserDtoResponse
import kr.jwsong.upsideDown.entity.User
import kr.jwsong.upsideDown.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@Transactional
class UserService (
    private val userRepository: UserRepository,
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    fun register(userDtoRequest: UserDtoRequest): String{
        var user: User? = userRepository.findByEmail(userDtoRequest.email)
        if(user != null){
            return "이미 등록된 아이디입니다"
        }

        user = userDtoRequest.toEntity()
        user.password = passwordEncoder.encode(user.password)
        userRepository.save(user)

        return "회원가입에 성공했습니다."
    }

    fun getUserList(): List<UserDtoResponse> {
        val users = userRepository.findAllByOrderByIdAsc()
        return users.map { UserDtoResponse.fromEntity(it) }
    }

    fun updateUser(updatedUser: UserDtoResponse): UserDtoResponse {

        logger.info("updatedUser > $updatedUser")

        // 유저 정보 가져오기
        val user = userRepository.findById(updatedUser.id ?: throw IllegalArgumentException("ID is required"))
            .orElseThrow { IllegalArgumentException("User not found") }

        // 데이터 업데이트
        user.name = updatedUser.name
        user.phoneNumber = updatedUser.phoneNumber
        user.company = updatedUser.company
        user.role = updatedUser.role

        // 비밀번호는 수정하지 않으므로 그대로 두기
        userRepository.save(user)

        return UserDtoResponse.fromEntity(user)
    }

}