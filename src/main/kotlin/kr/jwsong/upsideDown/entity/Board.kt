package kr.jwsong.upsideDown.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
@Table
class Board(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val title: String,

    @Column(nullable = false, length = 1000)
    val content: String,

    @Column(nullable = false, length = 100)
    val email: String,

    // Board와 AttachFile은 1:N 관계이므로, AttachFile 리스트를 추가합니다.
    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonManagedReference
    val attachFiles: List<AttachFile> = emptyList()

): BaseEntity() {

    // toString 메소드 오버라이드
    override fun toString(): String {
        return "Board(id=$id, title='$title', content='$content', email='$email')"
    }
}