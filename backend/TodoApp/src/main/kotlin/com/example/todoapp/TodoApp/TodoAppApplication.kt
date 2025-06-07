package com.example.todoapp.TodoApp

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class TodoAppApplication

@org.springframework.stereotype.Component
class DataInitializer(private val userRepository: UserRepository) : org.springframework.boot.CommandLineRunner {
    override fun run(vararg args: String?) {
        // Delete all existing users
        userRepository.deleteAll()

        // Create sample users
        val users = listOf(
            User(null, "Nguyen Van A", 25, "Ha Noi"),
            User(null, "Tran Thi B", 30, "Ho Chi Minh"),
            User(null, "Le Van C", 22, "Da Nang")
        )

        // Save all users
        userRepository.saveAll(users)

        println("Sample users inserted successfully!")
        println("Total users: ${userRepository.count()}")
    }
}

@Document
data class User(@Id val id: String? , val name: String?, val age: Int?, val address: String?)

data class CreateUserDto(val name: String, val age: Int, val address: String ){
	init {
	    require(age > 15) { "Age must be greater than 15" }
	}
}
interface UserRepository: MongoRepository<User , String>

@RestController
@RequestMapping("users")
class UserController(val userRepository: UserRepository){
	@PostMapping
	fun create(@RequestBody createUserDto: CreateUserDto): User {
		return userRepository.save(User(
			id = null,
			name = createUserDto.name,
			age = createUserDto.age,
			address = createUserDto.address
		))
	}

	@PostMapping("/sample")
	fun insertSampleUser(): User {
		val sampleUser = User(
			id = null,
			name = "Sample User ${System.currentTimeMillis()}",
			age = 20,
			address = "Sample Address"
		)
		return userRepository.save(sampleUser)
	}

	@GetMapping
	fun findAll(): List<User> {
		return userRepository.findAll()
	}

	@GetMapping("/{id}")
	fun findById(@PathVariable id: String): User {
		return userRepository.findById(id).orElseThrow { RuntimeException("User not found") }
	}
}



fun main(args: Array<String>) {
	runApplication<TodoAppApplication>(*args)
}
