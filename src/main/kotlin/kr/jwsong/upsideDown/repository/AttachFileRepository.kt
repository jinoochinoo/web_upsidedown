package kr.jwsong.upsideDown.repository

import kr.jwsong.upsideDown.entity.AttachFile
import org.springframework.data.jpa.repository.JpaRepository

interface AttachFileRepository : JpaRepository<AttachFile, Long> {
}