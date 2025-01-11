package kr.jwsong.upsideDown.repository

import kr.jwsong.upsideDown.entity.AttachFile
import kr.jwsong.upsideDown.entity.Board
import org.springframework.data.jpa.repository.JpaRepository

interface AttachFileRepository : JpaRepository<AttachFile, Long> {
    fun findByBoardId(boardId: Long): List<AttachFile>
    fun findByBoard(board: Board): List<AttachFile>
}