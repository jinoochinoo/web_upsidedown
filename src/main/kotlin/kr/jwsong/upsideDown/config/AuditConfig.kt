package kr.jwsong.upsideDown.config

import kr.jwsong.upsideDown.dto.LoginDtoRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

@Configuration
@EnableJpaAuditing
class AuditConfig {

    @Bean
    fun auditorAware(): AuditorAware<String> {
        return AuditorAware<String> {
            Optional.ofNullable(SecurityContextHolder.getContext())
                .map{ it.authentication }
                .filter { it.isAuthenticated && !it.name.equals("anonymousUser") }
                .map { it.principal as LoginDtoRequest }
                .map { it.email }
        }
    }
}