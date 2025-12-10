package com.ljp.tcp_sample.test

import com.ljp.tcp_sample.middleware.LoggingMiddleware
import com.ljp.tcp_sample.middleware.ResponseAppendMiddleware
import com.ljp.tcp_sample.middleware.echoHandler
import com.ljp.tcp_sample.server.MiddlewareTcpServer
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class ServerMiddlewareE2ETest {
    suspend fun tcpSendReceive(port: Int, msg: String): List<String> =
        withContext(Dispatchers.IO) {
            val socket = Socket("127.0.0.1", port)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

            writer.write(msg)
            writer.newLine()
            writer.flush()

            socket.shutdownOutput()

            val response = mutableListOf<String>()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response += line!!
            }
            socket.close()
            response
        }

    @Test
    fun `end-to-end middleware pipeline test`() = runBlocking {
        val log = mutableListOf<String>()
        val port = 29099

        val server = MiddlewareTcpServer(
            port = port,
            middlewares = mutableListOf(
                LoggingMiddleware(log),
                ResponseAppendMiddleware("HELLO-")
            ),
            handler = echoHandler
        )

        server.start()
        delay(500)
        val result = tcpSendReceive(port,"world")

        delay(500)
        server.stop()
        println("ssssssada")

        // 1) 验证中间件执行顺序
        assertEquals(listOf("before", "after"), log)

        // 2) 验证响应内容
        assertEquals(listOf(
            "HELLO-",     // middleware 追加
            "echo:world"  // handler 生成
        ), result)
    }


}
