package com.ljp.tcp_sample.middleware

import com.ljp.tcp_sample.handler.ConnectionHandler
import com.ljp.tcp_sample.io.Connection

/**
 * 常用中间件
 */
class LoggingMiddleware(private val log: MutableList<String>) : Middleware {
    override suspend fun process(conn: Connection, next: suspend () -> Unit) {
        log += "before"
        next()
        log += "after"
        println("logging is completed")
    }
}
class ResponseAppendMiddleware(private val prefix: String) : Middleware {
    override suspend fun process(conn: Connection, next: suspend () -> Unit) {
        conn.write(prefix)
        next()
    }
}
val echoHandler = object : ConnectionHandler {
    override suspend fun onConnection(conn: Connection){
        val msg = conn.read() ?: return
        conn.write("echo:$msg")
        println("echoHandler is completed")
    }
}