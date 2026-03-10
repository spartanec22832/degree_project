package com.degree.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DegreeBackendApplication

fun main(args: Array<String>) {
	runApplication<DegreeBackendApplication>(*args)
}