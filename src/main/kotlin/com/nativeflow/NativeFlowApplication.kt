package com.nativeflow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.nativeflow",
        "com.content",
        "com.identity",
        "com.gamification",
        "com.learning"
    ]
)
class NativeFlowApplication

fun main(args: Array<String>) {
    runApplication<NativeFlowApplication>(*args)
}
