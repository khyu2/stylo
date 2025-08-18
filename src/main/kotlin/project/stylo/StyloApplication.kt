package project.stylo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StyloApplication

fun main(args: Array<String>) {
    runApplication<StyloApplication>(*args)
}
