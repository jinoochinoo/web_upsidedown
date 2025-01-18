package kr.jwsong.upsideDown

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class UpsideDownApplication : SpringBootServletInitializer() {

	private val logger = LoggerFactory.getLogger(this.javaClass)

	@Value("\${this-app.welcome-message}")
	val appWelcomeMessage: String = ""

	@PostConstruct
	fun printAppWelcomeMessage(){
		println("appWelcomeMessage > $appWelcomeMessage")
	}

    /*
    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        printAppWelcomeMessage();
        return application.sources(UpsideDownApplication::class.java)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(UpsideDownApplication::class.java, *args)
        }
    }
    */
}

fun main(args: Array<String>) {
	runApplication<UpsideDownApplication>(*args)
}