package kr.jwsong.upsideDown.service

import jakarta.transaction.Transactional
import kr.jwsong.upsideDown.common.enum.ROLE
import kr.jwsong.upsideDown.dto.UserDtoRequest
import kr.jwsong.upsideDown.entity.User
import kr.jwsong.upsideDown.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@Transactional
class LoginService (
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun fetchUser(email: String, password: String): User? {
        val user = userRepository.findByEmail(email) ?: return null

        val isMatch = passwordEncoder.matches(password, user.password)
        if(!isMatch) return null

        //if(user.role == ROLE.NONE.toString()) return null

        return user
    }
}