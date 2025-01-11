package kr.jwsong.upsideDown.service

import jakarta.transaction.Transactional
import kr.jwsong.upsideDown.common.exception.BoardNotFoundException
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
        val savedFiles = saveAttachFiles(savedBoard, boardDtoRequest.files)

        // AttachFile 엔티티 저장
        savedFiles.forEach { attachFileRepository.save(it) }

        return savedBoard
    }

    private fun saveAttachFiles(board: Board, files: List<FileData>): List<AttachFile> {
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

    fun updateBoard(boardDtoRequest: BoardDtoRequest): Board {
        // 1. 게시글 조회 (수정할 게시글 찾기)
        val board = boardRepository.findById(boardDtoRequest.id!!)
            .orElseThrow { BoardNotFoundException("게시글을 찾을 수 없습니다.") }

        // 2. 수정된 제목과 내용 반영
        board.title = boardDtoRequest.title
        board.content = boardDtoRequest.content

        // 3. 기존 첨부파일 삭제 처리 (필요한 경우)
        removeOldAttachFiles(board)

        // 4. 새로운 첨부파일 저장
        val savedFiles = saveAttachFiles(board, boardDtoRequest.files)

        // 5. AttachFile 엔티티 저장 (새로 추가된 파일들)
        savedFiles.forEach { attachFileRepository.save(it) }

        // 6. 게시글 정보 업데이트
        val updatedBoard = boardRepository.save(board)

        logger.info("BoardService > updateBoard > finished")

        return updatedBoard
    }

    private fun removeOldAttachFiles(board: Board) {
        // 기존 게시글에 첨부된 파일들을 삭제
        val existingFiles = attachFileRepository.findByBoard(board)

        // 파일이 있을 경우, 삭제
        existingFiles.forEach {
            attachFileRepository.delete(it)
        }
    }

    // 게시글 조회 기능
    fun getBoardById(id: Long): Board? {
        val board = boardRepository.findById(id).orElse(null)
        board?.let {
            it.incrementViewCount()  // 조회수 증가
            boardRepository.save(it) // 변경된 Board 객체 저장
        }

        return board
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

    fun getBoardList(): List<BoardDtoRequest> {
        // 게시글 목록 조회 및 각 게시글에 대해 DTO 변환
        return boardRepository.findAll().map { board ->
            // BoardDtoRequest 변환
            BoardDtoRequest(
                id = board.id,
                title = board.title,
                content = board.content,
                files = board.attachFiles.map { attachFile ->
                    // AttachFile을 FileData로 변환
                    FileData(
                        savedFileName = attachFile.savedFileName,
                        originalFileName = attachFile.originalFileName
                    )
                },
                viewCnt = board.viewCnt
            )
        }
    }

}