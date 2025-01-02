package kr.jwsong.upsideDown.service

import jakarta.transaction.Transactional
import kr.jwsong.upsideDown.dto.LoginDtoRequest
import kr.jwsong.upsideDown.dto.UserDtoRequest
import kr.jwsong.upsideDown.entity.User
import kr.jwsong.upsideDown.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@Transactional
class UserService (
    private val userRepository: UserRepository,
) {

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

    fun signIn(loginDtoRequest: LoginDtoRequest): User? {
        val user: User = userRepository.findByEmail(loginDtoRequest.email) ?: return null

        val isMatch = passwordEncoder.matches(loginDtoRequest.password, user.password)
        if(!isMatch) return null

        return user
    }

}