package kr.jwsong.upsideDown.controller

import kr.jwsong.upsideDown.service.BoardService
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ViewController(
    private val boardService: BoardService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/")
    fun login(model: Model): String{
        logger.info("ViewController > login")
        model.addAttribute("isSidebarToggled", true)
        return "login"
    }

    @GetMapping("/main")
    fun main(model: Model): String{

        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        if (authentication != null) {
            // 인증 객체가 존재하면, 로그로 정보를 출력
            logger.info("Authentication Principal (User): ${authentication.principal}")
            logger.info("Authentication Name (Username): ${authentication.name}")
            logger.info("Is Authenticated: ${authentication.isAuthenticated}")

            // 권한 정보 (Roles)
            val authorities = authentication.authorities
            if (authorities.isNotEmpty()) {
                logger.info("Authorities: ${authorities.joinToString { it.authority }}")
            } else {
                logger.info("No authorities assigned")
            }

            // 사용자 정보가 UserDetails로 캐스팅 가능하면 추가 정보 추출
            if (authentication.principal is UserDetails) {
                val userDetails = authentication.principal as UserDetails
                logger.info("User Password: ${userDetails.password}")
            }
        } else {
            // 인증되지 않은 경우
            logger.warn("No authentication found in SecurityContextHolder.")
        }

        logger.info("ViewController > main")
        model.addAttribute("isSidebarToggled", true)
        return "main"
    }

    @GetMapping("/userInfo")
    fun userInfo(model: Model): String{
        logger.info("ViewController > userInfo")
        model.addAttribute("isSidebarToggled", false)
        return "userInfo"
    }

    @GetMapping("/boardEdit")
    fun boardEdit(model: Model): String{
        logger.info("ViewController > boardEdit")
        model.addAttribute("isSidebarToggled", false)
        return "boardEdit"
    }

    @GetMapping("/boardDetail")
    fun boardDetail(@RequestParam boardId: Long, model: Model): String{
        logger.info("ViewController > boardDetail")

        // boardId
        model.addAttribute("boardId", boardId)

        // 사이드바 토글 상태
        model.addAttribute("isSidebarToggled", false)
        return "boardDetail"
    }

}