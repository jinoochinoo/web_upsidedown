package kr.jwsong.upsideDown.common

import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import org.apache.tomcat.util.http.fileupload.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class RequestWrapper(request: HttpServletRequest): HttpServletRequestWrapper(request) {
    private val contents = ByteArrayOutputStream()

    override fun getInputStream(): ServletInputStream {
        // 원본 request의 byte를 복사하기, 원본 ServletInputStream은 사용되지 않았음
        IOUtils.copy(super.getInputStream(), contents)

        // 복사한 값을 ServletInputStream 인터페이스에 맞추어서 익명 클래스 제작
        return object : ServletInputStream() {
            private val buffer = ByteArrayInputStream(contents.toByteArray())
            override fun read(): Int = buffer.read()

            override fun isFinished(): Boolean = buffer.available() == 0

            override fun isReady(): Boolean = true

            override fun setReadListener(p0: ReadListener?) = throw NotImplementedError()
        }
    }
}