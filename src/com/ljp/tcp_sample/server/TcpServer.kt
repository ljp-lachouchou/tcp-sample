package com.ljp.tcp_sample.server

/**
 * 把 TCP 监听逻辑与连接处理逻辑完全分离。
 */
interface TcpServer {
    suspend fun start()
    suspend fun stop()
}