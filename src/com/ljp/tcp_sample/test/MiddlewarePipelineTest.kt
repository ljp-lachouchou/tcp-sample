package com.ljp.tcp_sample.test

import com.ljp.tcp_sample.handler.ConnectionHandler
import com.ljp.tcp_sample.middleware.Middleware
import com.ljp.tcp_sample.io.Connection
import com.ljp.tcp_sample.pipeline.MiddlewarePipeline
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MiddlewarePipelineTest {
    class FakeConnection : Connection {
        val writes = mutableListOf<String>()
        var readValue: String? = null

        override suspend fun read(): String? = readValue
        override suspend fun write(data: String) { writes.add(data) }
        override fun close() {}
    }
    @Test
    fun `middlewares should be executed in order`() = runTest {

        val log = mutableListOf<String>()

        val m1 = object : Middleware {
            override suspend fun process(connection: Connection, next: suspend () -> Unit) {
                log += "m1"
                next()
            }

        }
        val m2 = object : Middleware {
            override suspend fun process(connection: Connection, next: suspend () -> Unit) {
                log += "m2"
                next()
            }

        }

        val handler = object : ConnectionHandler {
            override suspend fun onConnection(conn: Connection) {
                log += "handler"
            }

        }

        val pipeline = MiddlewarePipeline(listOf(m1, m2), handler)
        pipeline.execute(FakeConnection())

        assertEquals(listOf("m1", "m2", "handler"), log)
    }

    @Test
    fun `middleware can break chain`() = runTest {
        val log = mutableListOf<String>()

        val blocker = object : Middleware {
            override suspend fun process(connection: Connection, next: suspend () -> Unit) {
                log += "block"
            }

        }

        val handler = object : ConnectionHandler {
            override suspend fun onConnection(conn: Connection) {
                log += "handler"
            }

        }

        val pipeline = MiddlewarePipeline(listOf(blocker), handler)

        pipeline.execute(FakeConnection())

        assertEquals(listOf("block"), log)
    }
}