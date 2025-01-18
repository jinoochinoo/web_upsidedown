package kr.jwsong.upsideDown.common

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.jwsong.upsideDown.common.enum.ROLE
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class LoginSuccessHandler: AuthenticationSuccessHandler {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {

        logger.info("Authentication successful for user: ${authentication?.name}")

        // 인증 정보가 제대로 설정되었는지 확인
        if (SecurityContextHolder.getContext().authentication != null) {
            logger.info("SecurityContext is set with authentication: ${SecurityContextHolder.getContext().authentication}")
        }

        // HttpServletResponse가 null인지 확인
        if (response == null) {
            logger.error("HttpServletResponse is null")
        } else {
            logger.info("HttpServletResponse is not null")
        }

        // Authentication이 null인지 확인
        if (authentication == null) {
            logger.error("Authentication is null")
        } else {
            logger.info("Authentication is not null. User: ${authentication.name}")
        }

        // 권한 정보 (Roles)
        val authorities = authentication?.authorities
        if (authorities != null) {
            logger.info("Authorities: ${authorities.joinToString { it.authority }}")
            logger.info("jwsong > " + authorities.joinToString { it.authority }.substring(5))
        } else {
            logger.info("No authorities assigned")
        }

        //val baseUrl = request?.serverName + request?.serverPort;
        if(authorities != null){
            when (authorities.joinToString { it.authority }.substring(5)) {
                ROLE.USER.toString() ->  response?.sendRedirect("/main")
                ROLE.ADMIN.toString() ->  response?.sendRedirect("/main")
            }
        }

    }

}