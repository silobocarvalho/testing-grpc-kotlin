package br.com.zup.car

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface CarRepository : JpaRepository<Car, Int> {
    fun existsByLicense(license: String) : Boolean
}