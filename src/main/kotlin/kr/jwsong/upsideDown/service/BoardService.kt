package kr.jwsong.upsideDown.service

import jakarta.transaction.Transactional
import kr.jwsong.upsideDown.dto.BoardDtoRequest
import kr.jwsong.upsideDown.dto.FileData
import kr.jwsong.upsideDown.entity.AttachFile
import kr.jwsong.upsideDown.entity.Board
import kr.jwsong.upsideDown.entity.User
import kr.jwsong.upsideDown.repository.AttachFileRepository
import kr.jwsong.upsideDown.repository.BoardRepository
import kr.jwsong.upsideDown.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Service
@Transactional
class BoardService (
    private val boardRepository: BoardRepository,
    private val attachFileRepository: AttachFileRepository,
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    // 스프링 부트의 resources/static 디렉토리를 기준으로 설정
    private val rootPath: Path = Paths.get("src/main/resources/static").toAbsolutePath().normalize()

    // 업로드 폴더 경로 (예: ./src/main/resources/static/upload/image)
    private val uploadPath: Path = rootPath.resolve("upload/image")

    fun writeBoard(boardDtoRequest: BoardDtoRequest): Board {

        logger.info("BoardService > writeBoard > $boardDtoRequest")

        // Board 엔티티 생성
        val board = boardDtoRequest.toEntity()

        logger.info("board > " + board.toString())

        val savedBoard = boardRepository.save(board)

        logger.info("board > finish")

        // 첨부파일 저장
        val savedFiles = saveAttachFiles(boardDtoRequest.files, savedBoard)

        // AttachFile 엔티티 저장
        savedFiles.forEach { attachFileRepository.save(it) }

        return savedBoard
    }

    private fun saveAttachFiles(files: List<FileData>,board: Board): List<AttachFile> {
        val savedFiles = mutableListOf<AttachFile>()


        for (fileName in files) {
            val filePath = uploadPath.resolve(fileName.savedFileName).toString()
            val attachFile = AttachFile(
                originalFileName = fileName.originalFileName,  // 실제 파일명
                savedFileName = fileName.savedFileName,     // 저장된 파일명 (디렉토리 내 파일명)
                filePath = filePath,          // 파일 경로
                board = board
            )
            savedFiles.add(attachFile)
        }

        return savedFiles
    }

    // 게시글 조회 기능
    fun getBoardById(id: Long): Board? {
        return boardRepository.findById(id).orElse(null) // 게시글이 없으면 null 반환
    }

    // 게시글 삭제
    fun deleteBoard(id: Long): Boolean {

        // 게시글 조회
        val board = boardRepository.findById(id).orElse(null) ?: return false

        // 게시글에 연관된 첨부파일들을 먼저 찾아서 삭제
        board.attachFiles.forEach { attachFile ->
            // 파일 경로에서 실제 파일 삭제
            val filePath = attachFile.filePath
            try {
                // 파일이 존재하는지 확인하고, 존재하면 삭제
                val file = File(filePath)
                if (file.exists()) {
                    if (file.delete()) {
                        logger.info("파일 삭제 성공: $filePath")
                    } else {
                        logger.info("파일 삭제 실패: $filePath")
                    }
                }
            } catch (e: Exception) {
                logger.info("파일 삭제 중 오류 발생: $filePath, 오류: ${e.message}")
            }
        }

        // 게시글 삭제
        return runCatching { boardRepository.delete(board) }.isSuccess

        /**
         * // 게시글 삭제
         *     fun deleteBoard(id: Long): Boolean {
         *         return boardRepository.findById(id).map {
         *             boardRepository.delete(it)  // 게시글 삭제
         *             true
         *         }.orElse(false)  // 게시글이 없으면 삭제 실패
         *     }
         */
    }

}