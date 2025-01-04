package kr.jwsong.upsideDown.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.jwsong.upsideDown.common.enum.ROLE
import kr.jwsong.upsideDown.dto.LoginDtoRequest
import kr.jwsong.upsideDown.dto.UserDtoRequest
import kr.jwsong.upsideDown.service.LoginService
import kr.jwsong.upsideDown.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class LoginFilter(
    private val loginService: LoginService,
    private val loginSuccessHandler: LoginSuccessHandler
): OncePerRequestFilter() {

    // 로그인 URL path
    private var loginPath: String = "/login"
    // 세션을 얼마나 유지하도록 할 것인지
    private var maxInactiveInterval: Int = 60 * 60 * 24 * 3

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val isAuthenticated = SecurityContextHolder.getContext().authentication != null
        val requestURI = request.requestURI

        // 이미 로그인한 경우
        if (isAuthenticated && requestURI == "/") {

            logger.info("filter 1")

            return response.sendRedirect("/main")
        } // 메소드가 POST가 아니거나, 로그인 path와 일치하지 않으면 다음 필터 진행
        //else if (request.method != HttpMethod.POST.name() || request.requestURI != loginPath) {
        else if (request.method != HttpMethod.POST.name() || request.requestURI != loginPath) {

            logger.info("filter 2")

            filterChain.doFilter(request, response)
        }
        // 메소드만 다르면, METHOD_NOT_ALLOWED 반환
        else if (request.method != HttpMethod.POST.name()) {

            logger.info("filter 3")

            failLogin(response, HttpStatus.METHOD_NOT_ALLOWED)
        }
         // 세션 로그인 진행
        else if(request.method == HttpMethod.POST.name() && request.requestURI == loginPath) {

            logger.info("filter 4")

            val newRequest = RequestWrapper(request)
            try {
                // request body의 객체화 및 validation
                val om = jacksonObjectMapper()
                val loginDtoRequest = om.readValue(newRequest.inputStream, LoginDtoRequest::class.java)

                // 유저가 정보 조회, 없으면 FORBIDDEN 반환
                val user = loginService.fetchUser(loginDtoRequest.email, loginDtoRequest.password) ?: return failLogin(response, HttpStatus.FORBIDDEN)

                if(user.role == ROLE.NONE.toString()) return failLoginWithMsg(response, HttpStatus.FORBIDDEN, "가입 승인을 기다려주세요!")

                logger.info("user > $user")

                val auth = UserAuthentication(user)

                // 세션에 Authentication 저장
                // 현재 리퀘스트 바운드에 세션 적용
                SecurityContextHolder.getContext().authentication = auth
                // 리퀘스트 바운드 영역 데이터를 글로벌한 영역으로 저장함. 향후 다른 리퀘스트에서도 세션이 유지되도록
                newRequest.session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
                )
                // 해당 세션 비활성화 유지 시간 설정
                newRequest.session.maxInactiveInterval = maxInactiveInterval

                logger.info("newRequest.session.maxInactiveInterval > $maxInactiveInterval")

                loginSuccessHandler.onAuthenticationSuccess(request, response, auth)
            } catch (err: Exception) {
                err.printStackTrace()
                return failLogin(response, HttpStatus.FORBIDDEN)
            }
        }
    }

    private fun failLogin(response: HttpServletResponse, status: HttpStatus) {
        logger.info("failLogin > ")
        response.sendError(status.value())
    }

    private fun failLoginWithMsg(response: HttpServletResponse, status: HttpStatus, errorMessage: String) {
        logger.info("failLoginWithMsg > ")

        // 상태 코드 설정
        response.status = status.value()

        // 에러 메시지를 추가하고 싶을 경우
        errorMessage.let {
            response.contentType = "application/json;charset=UTF-8"  // 응답의 Content-Type 설정 (필요에 따라 변경)
            response.writer.write("{\"errMsg\": \"$it\"}")  // JSON 형태로 에러 메시지 전송
        }
    }

    fun setLoginPath(loginPath: String): LoginFilter {
        this.loginPath = loginPath
        return this
    }

    fun setMaxInactiveInterval(value: Int): LoginFilter{
        this.maxInactiveInterval = value
        return this
    }
}