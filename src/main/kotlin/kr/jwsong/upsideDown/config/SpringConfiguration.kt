package kr.jwsong.upsideDown.config

import kr.jwsong.upsideDown.common.LoginFilter
import kr.jwsong.upsideDown.common.LoginSuccessHandler
import kr.jwsong.upsideDown.repository.UserRepository
import kr.jwsong.upsideDown.service.LoginService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SpringConfiguration(
    private val userRepository: UserRepository,
    private val loginSuccessHandler: LoginSuccessHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/css/**", "/js/**", "/images/**", "/summernote/**", "/upload/**","/favicon.ico").permitAll()
                it.requestMatchers("/").anonymous()
                it.requestMatchers("/register").permitAll()
                it.requestMatchers("/**").permitAll()
                //it.requestMatchers("/**").hasAnyRole("ADMIN", "USER")
            }
            .addFilterBefore(
                loginFilter(loginSuccessHandler) // LoginSuccessHandler를 주입
                    .setLoginPath("/login")
                    .setMaxInactiveInterval(60 * 60 * 24 * 3), // 세션 만료 시간 (3일)
                UsernamePasswordAuthenticationFilter::class.java // 로그인 필터가 UsernamePasswordAuthenticationFilter 이후에 실행되도록 설정
            )
            .cors{}
            .logout { logout ->
                logout.logoutUrl("/logout")  // 로그아웃 경로 설정
                    .logoutSuccessUrl("/")  // 로그아웃 후 이동할 URL 설정
                    .permitAll()  // 로그아웃 경로는 모든 사용자에게 허용
            }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    fun loginService(): LoginService {
        return LoginService(passwordEncoder(), userRepository)
    }

    @Bean
    fun loginFilter(loginSuccessHandler: LoginSuccessHandler): LoginFilter {
        return LoginFilter(loginService(), this.loginSuccessHandler)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        // 허용할 URL들
        configuration.allowedOrigins = listOf("http://localhost:8080")
        // 허용할 Method들
        configuration.allowedMethods = listOf("POST", "GET", "DELETE", "PUT")
        // 허용할 Header들
        configuration.allowedHeaders = listOf("*")
        // 세션 쿠키를 유지할 것 인지
        configuration.allowCredentials = true

        // cors 적용
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}