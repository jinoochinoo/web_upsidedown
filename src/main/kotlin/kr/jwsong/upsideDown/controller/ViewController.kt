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

            authentication.let{
                logger.info("ROLE > ${it.authorities}")
                logger.info("EMAIL > ${it.name}")
                model.addAttribute("userEmail", it.name)
            }

        } else {
            // 인증되지 않은 경우
            logger.warn("No authentication found in SecurityContextHolder.")
        }

        logger.info("ViewController > main")

        return "boardList"
    }

    @GetMapping("/userList")
    fun userInfo(model: Model): String{
        logger.info("ViewController > userList")

        val authentication = SecurityContextHolder.getContext().authentication
        authentication.let{
            logger.info("ROLE > ${it.authorities}")
            logger.info("EMAIL > ${it.name}")
            model.addAttribute("userEmail", it.name)
        }

        return "userList"
    }

    @GetMapping("/boardWrite")
    fun boardWrite(model: Model): String{
        logger.info("ViewController > boardWrite")
        return "boardWrite"
    }

    @GetMapping("/boardEdit")
    fun boardEdit(@RequestParam boardId: Long, model: Model): String{
        logger.info("ViewController > boardEdit")

        // boardId
        model.addAttribute("boardId", boardId)

        return "boardEdit"
    }

    @GetMapping("/boardDetail")
    fun boardDetail(@RequestParam boardId: Long, model: Model): String{
        logger.info("ViewController > boardDetail")

        // boardId
        model.addAttribute("boardId", boardId)

        return "boardDetail"
    }

    @GetMapping("/boardList")
    fun boardList(model: Model): String{
        logger.info("ViewController > boardList")

        val authentication = SecurityContextHolder.getContext().authentication
        authentication.let{
            logger.info("ROLE > ${it.authorities}")
            logger.info("EMAIL > ${it.name}")
            model.addAttribute("userEmail", it.name)
        }

        return "boardList"
    }

}