package com.ljp.tcp_sample.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.Socket

class BlockingConnection(private val socket:Socket): Connection {
    private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
    override suspend fun read(): String?  = withContext(Dispatchers.IO) {
        reader.readLine()
    }
    override suspend fun write(data: String)  = withContext(Dispatchers.IO) {
        writer.write(data)
        writer.newLine()
        writer.flush()
    }

    override fun close() {
        runCatching { socket.close() }
    }

}