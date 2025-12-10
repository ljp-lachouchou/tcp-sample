package com.ljp.tcp_sample.handler

import com.ljp.tcp_sample.io.Connection

/**
 * 收到一个 Connection，对它做事。
 */
interface ConnectionHandler {
    suspend fun onConnection(conn: Connection)
}