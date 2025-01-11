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
    var title: String,

    @Column(nullable = false, length = 1000)
    var content: String,

    @Column(nullable = false, length = 100)
    val email: String,

    // Board와 AttachFile은 1:N 관계이므로, AttachFile 리스트를 추가합니다.
    @OneToMany(mappedBy = "board", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonManagedReference
    val attachFiles: List<AttachFile> = emptyList(),

    @Column(nullable = false)
    var viewCnt: Long = 0  // 조회수 컬럼 추가

): BaseEntity() {

    // toString 메소드 오버라이드
    override fun toString(): String {
        return "Board(id=$id, title='$title', content='$content', email='$email')"
    }

    // 조회수를 증가시키는 메소드
    fun incrementViewCount() {
        this.viewCnt++
    }

}