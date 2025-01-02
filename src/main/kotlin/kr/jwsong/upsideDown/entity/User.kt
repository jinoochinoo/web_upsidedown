package kr.jwsong.upsideDown.entity

import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_email", columnNames = ["email"])
    ]
)
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 30, updatable = false)
    val email: String,

    @Column(nullable = false, length = 30)
    val name: String,

    @Column(nullable = false, length = 30)
    val phoneNumber: String,

    @Column(nullable = false, length = 30)
    val company: String,

    @Column(nullable = false, length = 100)
    var password: String,

    @Column(nullable = false, length = 30)
    val role: String

): BaseEntity() {

}