package kr.jwsong.upsideDown.common

import kr.jwsong.upsideDown.entity.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class UserAuthentication(val user: User): Authentication {
    override fun getName(): String {
        return user.email
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        // user 객체의 role 속성이 ROLE enum 타입
        // user.role이 ROLE.ADMIN이라면 "ROLE_ADMIN" 권한을 반환
        val authorities = ArrayList<GrantedAuthority>()
        // 사용자 역할에 맞는 권한을 생성하여 추가
        authorities.add(SimpleGrantedAuthority("ROLE_${user.role}"))
        return authorities
    }

    override fun getCredentials(): Any {
        return user.password
    }

    override fun getDetails(): Any? {
        return user.toString()
    }

    override fun getPrincipal(): Long? {
        return user.id
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {

    }
}