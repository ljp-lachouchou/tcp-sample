package com.ljp.tcp_sample.server

import com.ljp.tcp_sample.handler.ConnectionHandler
import com.ljp.tcp_sample.io.BlockingConnection
import com.ljp.tcp_sample.middleware.Middleware
import com.ljp.tcp_sample.pipeline.MiddlewarePipeline
import kotlinx.coroutines.*
import java.io.IOException
import java.net.ServerSocket
import java.net.SocketException


class MiddlewareTcpServer(
    private val port:Int,
    private val middlewares:MutableList<Middleware>,
    private val handler: ConnectionHandler
) :TcpServer {
    private var job : Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val pipeline = MiddlewarePipeline(middlewares,handler)
    private var serverSocket: ServerSocket? = null
    override suspend fun start() {
        job =scope.launch {
            if (serverSocket == null && isActive) { serverSocket = ServerSocket(port) }
            while (isActive) {
                try {
                    // 阻塞等待连接，若ServerSocket被关闭则抛出SocketException
                    val client = serverSocket?.accept() ?: break
                    val conn = BlockingConnection(client)
                    launch {
                        pipeline.execute(conn)
                        conn.close()
                    }
                } catch (e: SocketException) {
                    // 捕获"Socket closed"异常，判断是否为正常关闭
                    if (!isActive) {
                        // 协程已取消，属于正常关闭，忽略异常
                        println("ServerSocket closed normally")
                    } else {
                        // 其他Socket异常（如端口被占用），需要处理
                        e.printStackTrace()
                    }
                    break // 退出循环
                }
            }
        }
    }

    override suspend fun stop() {
        job?.cancel()
        // 关闭ServerSocket，触发accept()抛出异常
        serverSocket?.close()
        // 等待协程处理异常并结束
        job?.join()
        serverSocket = null
    }
}