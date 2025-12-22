package com.gamification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GamificationApplication

fun main(args: Array<String>) {
    runApplication<GamificationApplication>(*args)
}
