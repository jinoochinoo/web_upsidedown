package kr.jwsong.upsideDown.repository

import kr.jwsong.upsideDown.entity.Board
import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository : JpaRepository<Board, Long> {
}