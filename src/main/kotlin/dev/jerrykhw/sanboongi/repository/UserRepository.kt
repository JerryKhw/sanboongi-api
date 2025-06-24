package dev.jerrykhw.sanboongi.repository

import dev.jerrykhw.sanboongi.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>