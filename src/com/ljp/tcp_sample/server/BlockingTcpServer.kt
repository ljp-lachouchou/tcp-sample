package com.ljp.tcp_sample.server

import com.ljp.tcp_sample.handler.ConnectionHandler
import com.ljp.tcp_sample.io.BlockingConnection
import kotlinx.coroutines.*
import java.net.ServerSocket

class BlockingTcpServer(private val port:Int,
    private val handler: ConnectionHandler
    ) : TcpServer {
    private var job:Job? = null
    override suspend fun start() {
        job = CoroutineScope(Dispatchers.IO).launch {
            val serverSocket = ServerSocket(port)
            while (isActive) {
                val client = serverSocket.accept()
                val conn = BlockingConnection(client)
                launch {
                    handler.onConnection(conn)
                    conn.close()
                }
            }
        }
    }

    override suspend fun stop() {
        job?.cancelAndJoin()
    }
}