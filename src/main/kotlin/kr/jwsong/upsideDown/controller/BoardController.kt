package kr.jwsong.upsideDown.controller

import kr.jwsong.upsideDown.common.BaseResponse
import kr.jwsong.upsideDown.common.enum.ResultCode
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
class BoardController {

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

            // 파일 시스템 캐시 갱신 (필요시)
            //Files.newByteChannel(uploadPath, StandardOpenOption.READ);

            resultData["savedFileName"] = savedFileName

        } catch(e: Exception){
            e.printStackTrace()
        }

        return BaseResponse(data = resultData)
    }


    @PostMapping("/upload/imageDelete")
    fun imageDelete(@RequestParam filename: String): BaseResponse<Unit> {

//        val errors = mapOf(ex.fieldName to (ex.message ?: "Not Exception Message"))
//        return ResponseEntity(BaseResponse(
//            ResultCode.ERROR.name,
//            errors,
//            ResultCode.ERROR.msg
//        ), HttpStatus.BAD_REQUEST)

        try {
            val path: Path = Paths.get(uploadPath.toString(), filename)
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
}