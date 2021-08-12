package main

import db.ConnectionPool
import http.AvailableRequests
import http.ReqKey
import http.Worker
import java.net.http.HttpClient
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

var logger: Logger = Logger.getLogger("HttpRequester2")
val sql_db_stock = ConnectionPool()
val reqKey = ReqKey()
val availableRequests = AvailableRequests()
val httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build()
val key = "&apikey=${reqKey.key}"

fun main() {
    logger.useParentHandlers = false
    logger.addHandler(object : Handler() {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

        override fun publish(record: LogRecord?) {
            if (record != null) {
                println("[${format.format(Date())}] [${record.level.name}] [${record.sourceClassName}::${record.sourceMethodName}] ${record.message}")
            }
        }

        override fun flush() {}

        override fun close() {}
    })
    logger.log(Level.INFO, "Started Logger")
    val worker = Worker()
    worker.work()
}