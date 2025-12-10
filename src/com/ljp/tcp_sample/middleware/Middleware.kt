package com.ljp.tcp_sample.middleware

import com.ljp.tcp_sample.io.Connection

/**
 * 每个中间件
 *
 */
interface Middleware {
    suspend fun process(connection:Connection,next:suspend () ->Unit)
}