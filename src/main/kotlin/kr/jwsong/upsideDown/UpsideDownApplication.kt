package kr.jwsong.upsideDown

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UpsideDownApplication{

	private val logger = LoggerFactory.getLogger(this.javaClass)

	@Value("\${this-app.welcome-message}")
	val appWelcomeMessage: String = ""

	@PostConstruct
	fun printAppWelcomeMessage(){
		println("appWelcomeMessage > $appWelcomeMessage")
	}
}

fun main(args: Array<String>) {
	runApplication<UpsideDownApplication>(*args)

	// Logging


}