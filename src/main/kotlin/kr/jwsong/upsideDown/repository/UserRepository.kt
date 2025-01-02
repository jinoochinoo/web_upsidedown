package kr.jwsong.upsideDown.repository

import kr.jwsong.upsideDown.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun findByEmailAndPassword(email: String, password: String): User?
}