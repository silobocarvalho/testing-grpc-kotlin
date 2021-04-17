package br.com.zup.car

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
class Car(
    @field:NotBlank @Column(nullable = false) val model: String,
    @field:NotBlank @Column(nullable = false) val license: String,
) {
    @Id
    @GeneratedValue
    val id: Int? = null
}
