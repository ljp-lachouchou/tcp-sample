package com.ljp.tcp_sample.io

/**
 *
 * 创建一个抽象接口来表达“一个 TCP 连接能做什么”。
 */
interface Connection :AutoCloseable {
    /**
     * 为了以便和I/O进行解耦
     */
    suspend fun read():String?
    suspend fun write(data:String)
    override fun close()
}