package kr.jwsong.upsideDown.entity

import jakarta.persistence.*

@Entity
@Table
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 30, updatable = false)
    val email: String,

    @Column(nullable = false, length = 30)
    var name: String,

    @Column(nullable = false, length = 30)
    var phoneNumber: String,

    @Column(nullable = false, length = 30)
    var company: String,

    @Column(nullable = false, length = 100)
    var password: String,

    @Column(nullable = false, length = 30)
    var role: String

): BaseEntity() {

}