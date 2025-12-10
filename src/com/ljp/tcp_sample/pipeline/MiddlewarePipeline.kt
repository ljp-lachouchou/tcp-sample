package com.ljp.tcp_sample.pipeline

import com.ljp.tcp_sample.handler.ConnectionHandler
import com.ljp.tcp_sample.middleware.Middleware
import com.ljp.tcp_sample.io.Connection

/**
 * 把middleware链接起来
 *
 */
class MiddlewarePipeline(
    private val middlewares:List<Middleware>,
    private val finalHandler: ConnectionHandler
) {
    suspend fun execute(conn:Connection) {
        executeAt(0,conn)
    }

    private suspend fun executeAt(index: Int, conn: Connection) {
        if (index == middlewares.size) {
            finalHandler.onConnection(conn)
            return
        }
        val middleware = middlewares[index]
        middleware.process(conn) {
            executeAt(index +1,conn)
        }
    }
}