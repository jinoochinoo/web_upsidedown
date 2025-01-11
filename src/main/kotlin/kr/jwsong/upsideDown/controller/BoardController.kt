package kr.jwsong.upsideDown.controller

import jakarta.validation.Valid
import kr.jwsong.upsideDown.common.BaseResponse
import kr.jwsong.upsideDown.common.enum.ResultCode
import kr.jwsong.upsideDown.dto.BoardDtoRequest
import kr.jwsong.upsideDown.entity.Board
import kr.jwsong.upsideDown.service.BoardService
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.collections.HashMap

@RestController
class BoardController(
    private val boardService: BoardService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

//    private final val rootPath: Path = Paths.get("./").toAbsolutePath().normalize()

    // 스프링 부트의 resources/static 디렉토리를 기준으로 설정
    private final val rootPath: Path = Paths.get("src/main/resources/static").toAbsolutePath().normalize()

    // 업로드 폴더 경로 (예: ./src/main/resources/static/upload/image)
    private val uploadPath: Path = rootPath.resolve("upload/image")

//    private val uploadPath: String = "$rootPath/upload/image"

    @PostMapping("/upload/imageUpload")
    fun imageUpload(@RequestParam file: MultipartFile): BaseResponse<HashMap<String, String>> {

        val resultData: HashMap<String, String> = hashMapOf()
        try{
            val originalFileName: String = file.originalFilename.toString();
            val fileExtension: String = originalFileName.substring(originalFileName.lastIndexOf("."))

            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath)

            val savedFileName = UUID.randomUUID().toString() + fileExtension;
            file.transferTo(File(uploadPath.toString(), savedFileName))

            resultData["savedFileName"] = savedFileName
            resultData["originalFileName"] = originalFileName

        } catch(e: Exception){
            e.printStackTrace()
        }

        return BaseResponse(data = resultData)
    }


    @PostMapping("/upload/imageDelete")
    fun imageDelete(@RequestParam fileName: String): BaseResponse<Unit> {

        try {
            val path: Path = Paths.get(uploadPath.toString(), fileName)
            Files.delete(path)
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return BaseResponse()
    }

    @GetMapping("/upload/image/{filename}")
    @Throws(MalformedURLException::class)
    fun getImage(@PathVariable filename: String): ResponseEntity<Resource> {
        val path: Path = Paths.get(uploadPath.toString()).resolve(filename)
        val resource: Resource = UrlResource(path.toUri())

        logger.info("getImage > path > $path")

        return if (resource.exists() || resource.isReadable) {
            ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // MIME 타입을 지정
                .body(resource)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping("/writeBoard")
    fun writeBoard(@RequestBody @Valid boardDtoRequest: BoardDtoRequest): Board {
        logger.info("writeBoard > $boardDtoRequest")
        return boardService.writeBoard(boardDtoRequest)
    }

    @PostMapping("/updateBoard")
    fun updateBoard(@RequestBody @Valid boardDtoRequest: BoardDtoRequest): Board {
        logger.info("updateBoard > $boardDtoRequest")
        return boardService.updateBoard(boardDtoRequest)
    }

    // BoardRequest 클래스 정의
    data class BoardRequest(val boardId: Long)

    @PostMapping("/getBoard")
    fun getBoardDetail(@RequestBody boardRequest: BoardRequest): ResponseEntity<Board> {

        val boardId = boardRequest.boardId
        logger.info("getBoardDetail > $boardId")

        val board = boardService.getBoardById(boardId)
        return if (board != null) {
            ResponseEntity(board, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping("/deleteBoard")
    fun deleteBoard(@RequestBody boardRequest: BoardRequest): ResponseEntity<Void> {

        logger.info("deleteBoard > $boardRequest")

        val boardId = boardRequest.boardId
        logger.info("boardId > $boardId")

        return if (boardService.deleteBoard(boardId)) {
            ResponseEntity(HttpStatus.NO_CONTENT)  // 삭제 성공
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)  // 게시글을 찾을 수 없음
        }
    }

    @PostMapping("/getBoardList")
    fun getBoardList(): List<BoardDtoRequest> {
        return boardService.getBoardList()
    }

}