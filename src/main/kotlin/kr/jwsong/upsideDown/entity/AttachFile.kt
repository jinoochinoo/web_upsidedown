package kr.jwsong.upsideDown.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table
class AttachFile(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 200)
    val originalFileName: String,

    @Column(nullable = false, length = 200)
    val savedFileName: String,

    @Column(nullable = false, length = 200)
    val filePath: String,

    // ManyToOne 관계에서 외래 키 컬럼을 지정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)  // 'board_id' 외래 키로 설정
    @JsonBackReference
    val board: Board

): BaseEntity() {

}